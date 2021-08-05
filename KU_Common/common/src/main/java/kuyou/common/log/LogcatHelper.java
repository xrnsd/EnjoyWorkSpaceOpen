package kuyou.common.log;

import android.Manifest;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import kuyou.common.file.FileUtils;
import kuyou.common.permission.PermissionUtil;
import kuyou.common.utils.AdbUtils;
import kuyou.common.utils.CommonUtils;

/**
 * <p>
 * action : APP的log持久化工具<br/>
 * class: LogcatHelper <br/>
 * package: com.wgx.common.log <br/>
 * author: wuguoxian <br/>
 * date: 20200521 <br/>
 * version:V0.1<br/>
 */
public class LogcatHelper {
    private static final String TAG = "LogcatHelper";

    private static final String FILE_NAME_BASE_DEF = "BDMsg";
    private static final String FILE_NAME_END = ".txt";
    private static final String DIR_PATH_KU_LOG = "KuYou";

    private static LogcatHelper INSTANCE = null;

    private LogDumper mLogDumper = null;
    private Context mContext;

    private LogcatHelper(Context context) {
        mContext = context.getApplicationContext();
        mLogDumper = new LogDumper(FileUtils.getInstance(context));
    }

    public static LogcatHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LogcatHelper(context);
        }
        return INSTANCE;
    }

    /**
     * <p>
     * action : 设定基本的log的相对路径<br/>
     * author: wuguoxian <br/>
     * date: 20200519 <br/>
     * remark:<br/>
     * &nbsp 示例：/xx/xx/xx/yy1/yy2/yy3/zz.aa  <br/>
     * &nbsp 示例说明：xx代表存储位置为工具自动设定，yy们对应dirPath <br/>
     *
     * @param dirPath log的相对路径
     */
    public LogcatHelper setSaveLogDirPath(String dirPath) {
        mLogDumper.setDirPathSaveLog(new StringBuilder()
                .append("/kuyou/logcat")
                .append(dirPath)
                .toString());
        return this;
    }

    /**
     * <p>
     * action : 设定log的最大大小<br/>
     * author: wuguoxian <br/>
     * date: 20200519 <br/>
     * remark:<br/>
     * &nbsp 示例：/xx/xx/xx/yy1/yy2/yy3/zz.aa  <br/>
     * &nbsp 示例说明：xx代表存储位置为工具自动设定，yy们对应dirPath <br/>
     *
     * @param dirPath log的相对路径
     */
    public LogcatHelper setLogSizeMax(long val) {
        mLogDumper.setLogSizeMax(val);
        return this;
    }

    public void start() {
        String policy = null;

        // policy = "logcat *:e *:w | grep \"(" + mPID + ")\"";
        // policy = "logcat | grep \"(" + mPID + ")\"";//打印所有日志信息
        // policy = "logcat -s way";//打印标签过滤信息
        // policy = "logcat | grep \"(" + mPId + ")\"";
        // policy = "logcat  -s 12345677777778899";
        policy = "logcat --pid=" + android.os.Process.myPid();

        start(policy);
    }

    public void start(String policy) {
        if (Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) <= 0) { //release版本未开usb调试不运行
            Log.d(TAG, "start > cancel");
            return;
        }
        Log.d(TAG, "start");
        if (PermissionUtil.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            mLogDumper.setLogPolicy(policy);
            mLogDumper.start();
        } else {
            Log.w(TAG, new StringBuilder("缺少权限：")
                    .append(": WRITE_EXTERNAL_STORAGE , READ_EXTERNAL_STORAGE").toString());
        }
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private static class LogDumper extends Thread {
        private String mLogPolicy = null;
        private String mDirPathSaveLog = null;

        private long mLogSizeMax = 1024 * 1024 * 50;//def:50M

        private FileUtils mFileUtils;
        private FileUtils.Flag mRunning = new FileUtils.Flag(true);

        public LogDumper(FileUtils fu) {
            mFileUtils = fu;
        }

        public void setLogSizeMax(long logSizeMax) {
            mLogSizeMax = logSizeMax;
        }

        public void stopLogs() {
            mRunning.setValue(false);
        }

        public void setLogPolicy(String policy) {
            if (null == policy || policy.length() < 1)
                mLogPolicy = "logcat | grep \"(" + android.os.Process.myPid() + ")\"";
            else
                mLogPolicy = policy;
        }

        public void setDirPathSaveLog(String path) {
            mDirPathSaveLog = path;
            Log.w(TAG, "setSaveLogDirPath > mDirPathSaveLog=" + path);
            if (null == mFileUtils)
                return;
            mFileUtils.createDirPath(mDirPathSaveLog);
        }

        @Override
        public void run() {
            if (null == mFileUtils)
                return;
            final String logFilePath = new StringBuilder(mDirPathSaveLog)
                    .append(File.separator)
                    .append(CommonUtils.formatLocalTimeByMilSecond(System.currentTimeMillis(), "yyyyMMdd_HHmmss")).append(FILE_NAME_END)
                    .toString();
            if (null == mFileUtils.createFile(logFilePath))
                return;

            Log.d(TAG, "cmds=" + mLogPolicy);
            Process logcatProc = AdbUtils.runCmdByRuntime(mLogPolicy);
            if (null != logcatProc) {
                writeLogFromInput(logFilePath, logcatProc.getInputStream(), mRunning);
                logcatProc.destroy();
            }
        }

        public boolean writeLogFromInput(String fileRelativePath, InputStream input, FileUtils.Flag flag) {
            int autoCreateOldFileCount = -1;
            BufferedReader reader = null;
            FileOutputStream out = null;
            try {
                File currentFile = mFileUtils.createFile(fileRelativePath);
                if (null == currentFile) {
                    return false;
                }
                out = new FileOutputStream(currentFile);
                reader = new BufferedReader(new InputStreamReader(input), 1024);
                String line = null;
                int lineCount = 0;
                while (flag.getValue() && (line = reader.readLine()) != null) {
                    if (!flag.getValue()) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (!currentFile.exists()
                            && autoCreateOldFileCount < 5) {
                        currentFile.createNewFile();
                        out = new FileOutputStream(currentFile);
                        autoCreateOldFileCount += 1;
                    }
                    if (out != null) {
                        out.write((line + "\n").getBytes());
                    }
                    lineCount += 1;
                    if (lineCount > 5000) { //log文件大小控制
                        mFileUtils.autoClean(mDirPathSaveLog, mLogSizeMax);
                        String filePath = currentFile.getPath();
                        String filrPathBase = filePath.substring(0, filePath.lastIndexOf("."));
                        String filrPathSuffix = filePath.substring(filePath.lastIndexOf(".") + 1);
                        currentFile = mFileUtils.createFile(new StringBuilder(filrPathBase).append("_e.").append(filrPathSuffix).toString());
                        if (null == currentFile) {
                            return false;
                        }
                        out = new FileOutputStream(currentFile);
                        reader = new BufferedReader(new InputStreamReader(input), 1024);
                        line = null;
                        lineCount = 0;
                    }
                }
                return true;
            } catch (FileNotFoundException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            } catch (IOException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                        reader = null;
                    } catch (IOException e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                    out = null;
                }
            }
            return false;
        }
    }
}