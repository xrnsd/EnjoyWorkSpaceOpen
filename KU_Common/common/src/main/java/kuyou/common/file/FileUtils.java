package kuyou.common.file;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import kuyou.common.permission.PermissionUtil;

/**
 * <p>
 * action : 文件操作封装工具<br/>
 * class: FileUtils <br/>
 * package: com.wgx.common.file <br/>
 * author: wuguoxian <br/>
 * date: 20200521 <br/>
 * version:V0.2<br/>
 */
public class FileUtils {
    private static final String TAG = "kuyou.common.file > Utils";

    private List<File> mFileCacheList = new ArrayList<>();

    private String SDCardRoot;

    private static FileOutputStream sOutputStream;

    public static boolean writeInternalAntennaDevice(final String devPath, final int value) {
        String val = String.valueOf(value);
        return writeInternalAntennaDevice(devPath, String.valueOf(value));
    }

    public static boolean writeInternalAntennaDevice(final String devPath, final String val) {
        synchronized (new Object()) {
            BufferedOutputStream bos = null;
            File gpsAntSwitch = new File(devPath);
            byte[] buffer = new byte[val.length() + 64];
            try {
                sOutputStream = new FileOutputStream(gpsAntSwitch);
                bos = new BufferedOutputStream(sOutputStream, buffer.length);
            } catch (FileNotFoundException e) {
                Log.e(TAG, Log.getStackTraceString(e));
                return false;
            }
            try {
                buffer = val.getBytes();
                bos.write(buffer, 0, buffer.length);
                bos.flush();
                bos.close();
                Log.d(TAG, "write success > val =" + val);
                sOutputStream.close();
                return true;
            } catch (IOException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return false;
    }
    
    //路径相关转换
    public static String getFileNameByPath(String path){
        return new File(path).getName();
    }

    public static String getParentByPath(String path){
        return new File(path).getParent();
    }

    

    /**
     * ----------------注意权限的添加----------------
     */
    private FileUtils(Context context) {
        SDCardRoot = getSdRootDirectory(context);
    }

    public static FileUtils getInstance(Context context) {
        if (!PermissionUtil.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.e(TAG, "getInstance > process fail missing permissions : READ_EXTERNAL_STORAGE , WRITE_EXTERNAL_STORAGE ");
            return null;
        }
        return new FileUtils(context.getApplicationContext());
    }

    private String getSdRootDirectory(Context context) {
        String dirPath = context.getFilesDir().getAbsolutePath();//本应用目录
        //外置存储的路径
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        //Log.d(TAG, "getSdRootDirectory > dirPath=" + dirPath);
        return dirPath;
    }

    /**
     * 创建文件
     *
     * @param fileRelativePath 文件相对路径
     */
    public File createFile(String... items) {
        return createFile(getFileByRelativePath(items));

    }

    public static boolean isExists(String path) {
        return new File(path).exists();
    }

    /**
     * 创建文件
     */
    public File createFile(File file) {
        File dir = new File(file.getParent());
        try {
            if (!dir.exists())
                dir.mkdirs();
            if (null != file && !file.exists())
                file.createNewFile();
            if (null != file && !file.exists()) {
                Log.e(TAG, "createFile fail : " + file.getPath() + " is create fail");
                return null;
            }
            return file;
        } catch (FileNotFoundException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dir 目录路径，相当于文件夹
     * @return
     */
    public File createDirPath(String dir) {
        File dirFile = getFileByRelativePath(dir);
        if (null != dirFile && !dirFile.exists()) {
            Log.d(TAG, "createDirPath > create new dirs");
            dirFile.mkdirs();
        } else
            Log.d(TAG, "createDirPath > is no exists");
        return dirFile;
    }

    /**
     * 根据相对路径创建File，不需要关心具体存放位置
     *
     * @param items 路径列表，方法会自动拼接成一个路径
     * @return 一个file对象，路径为：以SDCardRoot为开头加上items
     */
    public File getFileByRelativePath(String... items) {
        if (null == items
                || items.length < 1)
            return null;
        StringBuilder pathBuilder = new StringBuilder(SDCardRoot).append(File.separator);
        for (String item : items) {
            pathBuilder.append(item).append(File.separator);
        }
        synchronized (mFileCacheList) {
            String path = pathBuilder.toString();
            for (File file : mFileCacheList) {
                if (null != file && file.getPath().equals(path))
                    return file;
            }
            File file = new File(path);
            mFileCacheList.add(file);
            return file;
        }
    }

    /**
     * 判断SD卡上的文件夹是否存在
     *
     * @param fileRelativePath 文件相对路径
     * @return
     */
    public boolean isFileExist(String fileRelativePath) {
        File file = getFileByRelativePath(fileRelativePath);
        return null != file && file.exists();
    }

    /**
     * 将一个字节数组数据写入到文件中
     *
     * @param items 文件相对路径
     * @param bytes 待写入的数据
     * @return 是否写入成功
     */
    public boolean writeFile(byte[] bytes, String... items) {
        if (bytes == null) {
            return false;
        }
        OutputStream outputStream = null;
        try {
            File file = createFile(items);
            if (null == file) {
                return false;
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            outputStream = new BufferedOutputStream(new FileOutputStream(
                    file));
            outputStream.write(bytes);
            outputStream.flush();
            return true;
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }
        return false;
    }

    /**
     * 将一个InputStream里面的数据写入到文件中
     *
     * @param fileRelativePath 文件相对路径
     * @param input            待写入的流
     * @return 是否写入成功
     */
    public boolean writeFromInput(String fileRelativePath, InputStream input) {
        OutputStream output = null;
        try {
            File file = createFile(fileRelativePath);
            if (null == file) {
                return false;
            }
            output = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            int temp;
            while ((temp = input.read(buffer)) != -1) {
                output.write(buffer, 0, temp);
            }
            output.flush();
            return true;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return false;
    }

    public static class Flag {
        public Flag(boolean val) {
            setValue(val);
        }

        private boolean value;

        public boolean getValue() {
            return value;
        }

        public void setValue(boolean val) {
            value = val;
        }
    }

    public boolean writeFromInput(String fileRelativePath, InputStream input, Flag flag) {
        int autoCreateOldFileCount = -1;
        BufferedReader reader = null;
        FileOutputStream out = null;
        try {
            File currentFile = createFile(fileRelativePath);
            if (null == currentFile) {
                return false;
            }
            out = new FileOutputStream(currentFile);
            reader = new BufferedReader(new InputStreamReader(input), 1024);
            String line = null;
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

    public boolean writeLogFromInput(String fileRelativePath, InputStream input, Flag flag) {
        int autoCreateOldFileCount = -1;
        BufferedReader reader = null;
        FileOutputStream out = null;
        try {
            File currentFile = createFile(fileRelativePath);
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
                    String filePath = currentFile.getPath();
                    String filrPathBase = filePath.substring(0, filePath.lastIndexOf("."));
                    String filrPathSuffix = filePath.substring(filePath.lastIndexOf(".") + 1);
                    currentFile = createFile(new StringBuilder(filrPathBase).append("_e.").append(filrPathSuffix).toString());
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

    /**
     * 读取文件
     *
     * @param fileRelativePath 文件相对路径
     * @return 文件的数据
     */
    public String readData(String fileRelativePath) {
        File file = getFileByRelativePath(fileRelativePath);
        if (null == file || !file.exists()) {
            return "";
        }
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            return new String(data);
        } catch (FileNotFoundException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }
        return "";
    }

    /**
     * 使用FileWriter在文件末尾添加内容
     *
     * @param fileRelativePath 文件相对路径
     * @param content          追加数据
     */
    public void appendContent(String fileRelativePath, String content) {
        OutputStream outputStream = null;
        try {
            File file = getFileByRelativePath(fileRelativePath);
            outputStream = new FileOutputStream(file, true);
            byte[] enter = new byte[2];
            enter[0] = 0x0d;
            enter[1] = 0x0a;// 用于输入换行符的字节码
            String finalString = new String(enter);// 将该字节码转化为字符串类型
            content = content + finalString;
            outputStream.write(content.getBytes());
            outputStream.flush();
        } catch (FileNotFoundException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     */
    public long getFileOrFolderSize(File file) {
        long size = 0;
        try {
            if (!file.isDirectory())
                return file.length();
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory())
                    size = size + getFileOrFolderSize(fileList[i]);
                else
                    size = size + fileList[i].length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @return
     */
    private void deleteFolderFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            Log.e(TAG, "deleteFolderFile > process fail : filePath is invalid = " + filePath);
        }
        try {
            File file = new File(filePath);
            if (file.isDirectory()) {// 处理目录
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFolderFile(files[i].getAbsolutePath());
                }
                return;
            }
            file.delete();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public File[] orderByDate(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;
            }

            public boolean equals(Object obj) {
                return true;
            }

        });
        return files;
    }

    public boolean autoClean(String path, long maxSize) {
        File dir = new File(path);
        if (!dir.isDirectory()) {
            Log.e(TAG, "autoClean > process fail : path is invalid = " + path);
            return false;
        }
        long sizeNow = getFileOrFolderSize(dir);
        long sizeFlag = Float.valueOf(maxSize * 0.875F).longValue();
        if (sizeNow < sizeFlag) {
            Log.e(TAG, "autoClean > process fail : sizeNow < 0.875F");
            return false;
        }
        long sizeDeleteFlag = Float.valueOf(maxSize * 0.25F).longValue();
        if (sizeNow > maxSize) {
            sizeDeleteFlag += (sizeNow - maxSize);
        }

        long sizeDeleteNow = 0;
        for (File subFile : orderByDate(path)) {
            sizeDeleteNow += getFileOrFolderSize(subFile);
            subFile.delete();
            //Log.d(TAG, "autoClean > delete subFile = " + subFile.getPath());
            //Log.d(TAG, "autoClean > sizeDeleteNow = " + sizeDeleteNow);
            if (sizeDeleteNow >= sizeDeleteFlag) {
                break;
            }
        }
        Log.i(TAG, "autoClean > process success ");
        return true;
    }

    public static void copyFile(Context context, String assetsFile, String destination) {
        InputStream is = null;
        FileOutputStream fos = null;
        byte[] buf1 = new byte[512];
        try {
            File des = new File(destination, assetsFile);
            if (des.exists()) {
                return;
            }
            if (!new File(destination).exists()) {
                Log.d(TAG, "mkdirs  " + new File(destination).mkdirs());
            }
            Log.d(TAG, "copy to: " + des.getAbsolutePath());
            fos = new FileOutputStream(des);
            is = context.getAssets().open(assetsFile);
            int readCount;
            while ((readCount = is.read(buf1)) > 0) {
                fos.write(buf1, 0, readCount);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "copy: ", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
