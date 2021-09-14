package com.kuyou.ft.item;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.kuyou.ft.R;
import com.kuyou.ft.basic.ipc.TestItemIpc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kuyou.common.ku09.protocol.basic.IDeviceConfig;
import kuyou.common.utils.CommonUtils;
import kuyou.common.utils.SystemPropertiesUtils;

public class TestItemHwSwInfo extends TestItemIpc {

    private static final String FILENAME_PROC_VERSION = "/proc/version";
    private static final String FILENAME_MSV = "/sys/board_properties/soc/msv";
    private static final String GSM_BASEBAND_VERSION = "gsm.version.baseband";
    private static final String MEDIATEK_PLATFORM = "ro.mediatek.platform";
    private static final String CUSTOM_BUILD_VERSION = "ro.build.custom.version";
    private static final String CUSTOM_BUILD_DATE = "ro.build.date";

    private static final int MSG_ID_GET_BARCODE = 0x01;

    private static int bResult = -1;

    private String mStrBbChip,
            mStrMsBoard,
            mStrKernelVersion,
            mStrDeviceModel,
            mStrBaseBandVersion,
            mStrFrameworkVersion,
            mStrBuilTime,
            mStrSoftwareVersion,
            mStrCustomBuildVersion,
            mStrImei1,
            mStrImei2;

    private LinearLayout mVersionLayout = null;
    private TelephonyManager mTM = null;
    private Phone mPhone = null;
    private final Handler mATCmdHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ID_GET_BARCODE:
                    LinearLayout LlTemp;
                    AsyncResult ar;
                    boolean ret = true;
                    ar = (AsyncResult) msg.obj;
                    if (ar.exception == null) {
                        String rt[] = (String[]) ar.result;
                        if (rt.length > 0) {
                            if (rt[0].lastIndexOf(" 10") == -1) {
                                ret = false;
                            }
                        } else {
                            ret = false;
                        }
                    } else {
                        ret = false;
                    }
                    LlTemp = createItem(getApplicationContext(), TestItemHwSwInfo.this.getString(R.string.test_version_cali_status),
                            ret ? TestItemHwSwInfo.this.getString(R.string.test_version_cali_ok) : TestItemHwSwInfo.this.getString(R.string.test_version_cali_failed),
                            false);
                    mVersionLayout.addView(LlTemp, 0);
                    String serialno = new String("Not find serialNO");
                    String tmpNO = SystemPropertiesUtils.get("ro.serialno", null);
                    if (!TextUtils.isEmpty(tmpNO)) {
                        serialno = serialno.replace(serialno, tmpNO);
                    }

                    LlTemp = createItem(getApplicationContext(), "SerialNo:", serialno, false);
                    mVersionLayout.addView(LlTemp, 1);

                    LlTemp = createItem(getApplicationContext(), " ", " ", false);
                    mVersionLayout.addView(LlTemp, 2);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public int getTestPolicy() {
        int policy = 0;
        policy |= POLICY_TEST;
        policy |= POLICY_TEST_AUTO;
        return policy;
    }

    @Override
    public int getTestId() {
        return R.id.test_version;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_version;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.hw_sw_info);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mVersionLayout = findViewById(R.id.version_layout);
        if (null != mDeviceConfig)
            buildVersionContent(mDeviceConfig);
    }

    public void getInfoFromBuilder() {
        mStrBbChip = SystemPropertiesUtils.get(MEDIATEK_PLATFORM, "Unknown");
        mStrMsBoard = android.os.Build.DEVICE;
        mStrKernelVersion = getFormattedKernelVersion();
        mStrDeviceModel = android.os.Build.MODEL + getMsvSuffix();
        mStrFrameworkVersion = android.os.Build.VERSION.RELEASE;
        mStrBaseBandVersion = SystemPropertiesUtils.get(GSM_BASEBAND_VERSION, "Unknown");
        mStrCustomBuildVersion = SystemPropertiesUtils.get(CUSTOM_BUILD_VERSION, "Unknown");

        mStrSoftwareVersion = android.os.Build.DISPLAY;//SystemPropertiesUtils.get(CUSTOM_BUILD_VERSION, "UNKNOWN");
        mStrBuilTime = CommonUtils.formatUTCTimeByMilSecond(android.os.Build.TIME + 8 * 60 * 60 * 1000, "yyyyMMdd_HHmm");//SystemPropertiesUtils.get(CUSTOM_BUILD_DATE, "Unknown");
    }

    public void getCalibrationInfo() {
        Log.d(TAG, "mPhone qsl=getCalibrationInfo ");
        String cmd[] = {"AT+EGMR=0,5", "+EGMR"};
        mPhone = (Phone) PhoneFactory.getDefaultPhone();
        Log.d(TAG, "mPhone qsl= " + mPhone);
        mPhone.invokeOemRilRequestStrings(cmd, mATCmdHander.obtainMessage(MSG_ID_GET_BARCODE));
    }

    IDeviceConfig mDeviceConfig;

    public void buildVersionContent(IDeviceConfig config) {
        Log.d(TAG, "mPhone qsl=buildVersionContent ");
        mDeviceConfig = config;
        if (null == mVersionLayout) {
            return;
        }
        LinearLayout LlTemp;
        mVersionLayout.removeAllViews();

        getInfoFromBuilder();

        LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.title_dev_id), config.getDevId(), false);
        mVersionLayout.addView(LlTemp);
        LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.title_uwb_id), config.getUwbId(), false);
        mVersionLayout.addView(LlTemp);
        LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.title_collecting_end_id), config.getCollectingEndId(), false);
        mVersionLayout.addView(LlTemp);
        LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.title_remote_control_server_address), config.getRemoteControlServerAddress(), false);
        mVersionLayout.addView(LlTemp);
        LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.title_remote_control_server_port), String.valueOf(config.getRemoteControlServerPort()), false);
        mVersionLayout.addView(LlTemp);
        LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.title_remote_photo_server_address), config.getRemotePhotoServerAddress(), false);
        mVersionLayout.addView(LlTemp);
        LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.title_heartbeat_interval), String.valueOf(config.getHeartbeatInterval()), false);
        mVersionLayout.addView(LlTemp);
        LlTemp = createItem(getApplicationContext(), " ", " ", false);
        mVersionLayout.addView(LlTemp);

        LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.test_version_xh), mStrDeviceModel, false);
        mVersionLayout.addView(LlTemp);
        LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.test_version_chip), mStrBbChip, false);
        mVersionLayout.addView(LlTemp);
        LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.test_version_board), mStrMsBoard, false);
        mVersionLayout.addView(LlTemp);

        LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.test_version_kernelver), mStrKernelVersion, false);
        mVersionLayout.addView(LlTemp);
        LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.test_version_androidver), mStrFrameworkVersion, false);
        mVersionLayout.addView(LlTemp);
        LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.test_version_basever), mStrBaseBandVersion, false);

        mVersionLayout.addView(LlTemp);
        LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.test_version_swver), mStrSoftwareVersion, false);
        mVersionLayout.addView(LlTemp);
        LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.test_version_date), mStrBuilTime, false);
        mVersionLayout.addView(LlTemp);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.test_version_screensize), dm.widthPixels + " X " + dm.heightPixels, false);
        LlTemp = createItem(getApplicationContext(), getResources().getString(R.string.test_version_screensize), 
                SystemPropertiesUtils.get("ro.screensize", "Unknown"), false);
        mVersionLayout.addView(LlTemp);
        if (null == mTM) {
            mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        }
        if (null != mTM) {
            mStrImei1 = mTM.getDeviceId(0);
            mStrImei2 = mTM.getDeviceId(1);
        }
        LlTemp = createItem(getApplicationContext(), "MEID: ", mStrImei1, false);
        mVersionLayout.addView(LlTemp);
        LlTemp = createItem(getApplicationContext(), "IMEI: ", mStrImei2, false);
        mVersionLayout.addView(LlTemp);
        getCalibrationInfo();
    }

    private LinearLayout createItem(Context context, String name, String value, boolean bTitle) {
        LinearLayout ll = new LinearLayout(context);
        TextView itemName = new TextView(context);
        TextView itemValue = new TextView(context);

        LayoutParams lp = new LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        LayoutParams childParam = new LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        itemName.setLayoutParams(childParam);
        itemName.setText(name);
        itemName.setTextColor(Color.WHITE);
        itemName.setTextSize(16);
        itemName.setPadding(0, 2, 0, 2);
        itemName.getPaint().setFakeBoldText(true);
        ll.addView(itemName);

        itemValue.setLayoutParams(childParam);
        itemValue.setText((value == null) ? "null" : value);
        itemValue.setTextColor(Color.WHITE);
        itemValue.setTextSize(16);
        itemValue.getPaint().setFakeBoldText(true);
        ll.addView(itemValue);

        return ll;
    }

    /**
     * Reads a line from the specified file.
     *
     * @param filename the file to read from
     * @return the first line, if any.
     * @throws IOException if the file couldn't be read
     */
    private String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    private String getFormattedKernelVersion() {
        String procVersionStr;

        try {
            procVersionStr = readLine(FILENAME_PROC_VERSION);

            final String PROC_VERSION_REGEX =
                    "\\w+\\s+" + /* ignore: Linux */
                            "\\w+\\s+" + /* ignore: version */
                            "([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
                            "\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /* group 2: (xxxxxx@xxxxx.constant) */
                            "\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+" + /* ignore: (gcc ..) */
                            "([^\\s]+)\\s+" + /* group 3: #26 */
                            "(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
                            "(.+)"; /* group 4: date */

            Pattern p = Pattern.compile(PROC_VERSION_REGEX);
            Matcher m = p.matcher(procVersionStr);

            if (!m.matches()) {
                Log.e(TAG, "Regex did not match on /proc/version: " + procVersionStr);
                return "Unavailable";
            } else if (m.groupCount() < 4) {
                Log.e(TAG, "Regex match on /proc/version only returned " + m.groupCount()
                        + " groups");
                return "Unavailable";
            } else {
                return (new StringBuilder(m.group(1)).append("\n").append(
                        m.group(2)).append(" ").append(m.group(3)).append("\n")
                        .append(m.group(4))).toString();
            }
        } catch (IOException e) {
            Log.e(TAG,
                    "IO Exception when getting kernel version for Device Info screen",
                    e);

            return "Unavailable";
        }
    }

    /**
     * Returns " (ENGINEERING)" if the msv file has a zero value, else returns "".
     *
     * @return a string to append to the model number description.
     */
    private String getMsvSuffix() {
        // Production devices should have a non-zero value. If we can't read it, assume it's a
        // production device so that we don't accidentally show that it's an ENGINEERING device.
        try {
            String msv = readLine(FILENAME_MSV);
            // Parse as a hex number. If it evaluates to a zero, then it's an engineering build.
            if (Long.parseLong(msv, 16) == 0) {
                return " (ENGINEERING)";
            }
        } catch (IOException ioe) {
            // Fail quietly, as the file may not exist on some devices.
        } catch (NumberFormatException nfe) {
            // Fail quietly, returning empty string should be sufficient
        }
        return "";
    }
}


