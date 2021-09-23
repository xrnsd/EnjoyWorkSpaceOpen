
package com.kuyou.avc.ui.thermal;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.peergine.plugin.android.pgDevVideoIn;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private boolean m_bStarted = false;
    private boolean m_bStoped = false;

    private Handler m_Handler = null;
    private Camera m_Camera = null;
    private SurfaceHolder m_Holder;

    private int m_iCameraWidth = 0;
    private int m_iCameraHeight = 0;
    private int m_iCameraFrmRate = 0;

    private int m_iDevID = -1;
    private int m_iCameraNo = -1;
    private int m_iCameraFormat = -1;
    private int m_iCameraRotate = 0;
    private int m_iCameraImgRotate = 0;

    private int m_iCameraOpenStatus = -1;
    private int m_iCameraCloseStatus = -1;
    private byte[] m_byBufPreview = null;

    public CameraView(Context ctx) {
        super(ctx);
    }

    public boolean Initialize() {
        try {
            Log.d("DevExtend", "CameraView.Initialize");

            m_Handler = new Handler() {
                public void handleMessage(Message msg) {
                    try {
                    } catch (Exception ex) {
                        Log.d("CameraView", "handleMessage Exception");
                    }
                }
            };

            m_Holder = getHolder();
            m_Holder.addCallback(this);

            return true;
        } catch (Exception ex) {
            Log.d("DevExtend", "CameraView.Start, ex=" + ex.toString());

            return false;
        }
    }

    public boolean Start(int iDevID, int iCameraNo, int iW, int iH, int iBitRate, int iFrmRate, int iKeyFrmRate) {
        try {
            Log.d("DevExtend", "CameraView.Start: iW=" + iW + ", iH=" + iH + ", iBitRate="
                    + iBitRate + ", iFrmRate=" + iFrmRate + ", iKeyFrmRate=" + iKeyFrmRate);

            if (m_bStarted) {
                return true;
            }

            // Store the input parameters.
            m_iDevID = iDevID;
            m_iCameraNo = -1; // Set to -1, mean that it auto select the Camera No.
            m_iCameraWidth = iW;
            m_iCameraHeight = iH;
            m_iCameraFrmRate = iFrmRate;

            // Get extend camera parameters.
            m_iCameraRotate = pgDevVideoIn.GetParam(iCameraNo, pgDevVideoIn.PG_DEV_VIDEO_IN_PARAM_ROTATE);
            m_iCameraImgRotate = pgDevVideoIn.GetParam(iCameraNo, pgDevVideoIn.PG_DEV_VIDEO_IN_PARAM_IMG_ROTATE);

            Log.d("DevExtend", "CameraView.Start, No=" + iCameraNo
                    + ", Rotate=" + m_iCameraRotate + ", ImgRotate=" + m_iCameraImgRotate);

            // Reset the open status.
            m_iCameraOpenStatus = -1;

            // Post the open camera handle to UI thread.
            m_Handler.post(new Runnable() {
                public void run() {
                    Log.d("DevExtend", "CameraView.Start, run PreviewOpen");
                    PreviewOpen();
                }
            });

            // wait and check the open result, timeout 10 seconds.
            int i = 0;
            while (i < 100) {
                if (m_iCameraOpenStatus >= 0) {
                    break;
                }
                Thread.sleep(100);
                i++;
            }
            if (m_iCameraOpenStatus <= 0) {
                return false;
            }

            // If open the front camera.
            int iFacing;
            if (m_iCameraNo == CameraInfo.CAMERA_FACING_FRONT) {
                iFacing = 1;
            } else {
                iFacing = 0;
            }

            // Opposite rotate if opening the front camera.
            int iRotate = m_iCameraRotate;
            int iImgRotate = m_iCameraImgRotate;
            if (iFacing != 0) {
                iRotate = ((360 - iRotate) % 360);
                iImgRotate = ((360 - iImgRotate) % 360);
            }

            Log.d("DevExtend", "CameraView.Start, CameraNo=" + m_iCameraNo
                    + ", Facing=" + iFacing + ", Rotate=" + iRotate + ", ImgRotate=" + iImgRotate);

            // Feedback the extend parameters;
            //2
            pgDevVideoIn.SetParam(iCameraNo, pgDevVideoIn.PG_DEV_VIDEO_IN_PARAM_NO, m_iCameraNo);
            pgDevVideoIn.SetParam(iCameraNo, pgDevVideoIn.PG_DEV_VIDEO_IN_PARAM_FACING, iFacing);
            pgDevVideoIn.SetParam(iCameraNo, pgDevVideoIn.PG_DEV_VIDEO_IN_PARAM_ROTATE, iRotate);
            pgDevVideoIn.SetParam(iCameraNo, pgDevVideoIn.PG_DEV_VIDEO_IN_PARAM_IMG_ROTATE, iImgRotate);

            m_iJpegCount = 0;
            m_bStarted = true;
            m_bStoped = false;

            return true;
        } catch (Exception ex) {
            Log.d("DevExtend", "CameraView.Start, ex=" + ex.toString());
            m_bStarted = false;
            return false;
        }
    }

    public void Ctrl(int iDevID, int iCtrl, int iParam) {
        if (!m_bStarted) {
            return;
        }

        if (iDevID != m_iDevID) {
            return;
        }
    }

    public void Stop() {
        if (!m_bStarted) {
            return;
        }

        try {
            Log.d("DevExtend", "CameraView.Stop");

            m_bStoped = true;

            // Reset the close status.
            m_iCameraCloseStatus = -1;

            // Post the close camera handle to UI thread.
            m_Handler.post(new Runnable() {
                public void run() {
                    Log.d("DevExtend", "CameraView.Stop, run PreviewClose");
                    PreviewClose();
                }
            });

            // wait and check the close result
            int i = 0;
            while (i < 5) {
                if (m_iCameraCloseStatus >= 0) {
                    break;
                }
                Thread.sleep(100);
                i++;
            }

            m_iJpegCount = 0;
            m_bStarted = false;
        } catch (Exception ex) {
            Log.d("DevExtend", "CameraView.Stop, ex=" + ex.toString());
            m_bStarted = false;
        }
    }

    public void PreviewOpen() {
        try {
            Log.d("DevExtend", "CameraView.PreviewOpen");

            getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            if (getVisibility() != View.GONE) {
                setVisibility(View.GONE);
            }
            setVisibility(View.VISIBLE);
        } catch (Exception ex) {
            Log.d("DevExtend", "CameraView.PreviewOpen, ex=" + ex.toString());
        }
    }

    public void PreviewClose() {
        try {
            Log.d("DevExtend", "CameraView.PreviewClose");
            if (m_Camera != null) {
                if (getVisibility() != View.GONE) {
                    setVisibility(View.GONE);
                }
                getHolder().setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
                m_Camera = null;
            }
        } catch (Exception ex) {
            Log.d("DevExtend", "CameraView.PreviewClose, ex=" + ex.toString());
        }
    }

    public void surfaceCreated(SurfaceHolder surfaceholder) {
        Log.d("DevExtend", "CameraView.surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
        CameraOpen();
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        CameraClose();
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        try {
            // Capture jpeg pictures.
            //CaptureJpeg(data, "/sdcard/Download", 2);

            if (m_iDevID < 0) {
                m_Camera.addCallbackBuffer(m_byBufPreview);
                return;
            }

            Log.d("DevExtend", "CameraView.onPreviewFrame, begin. dataSize=" + data.length);

            if (m_bStoped) {
                m_Camera.addCallbackBuffer(m_byBufPreview);
                return;
            }

            // Uncompress callback.
            pgDevVideoIn.CaptureProcExt(m_iDevID, data, 0, data.length, m_iCameraFormat, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        m_Camera.addCallbackBuffer(m_byBufPreview);
    }

    private void CaptureJpeg(byte[] data, String sRoot, int iMaxCount) {
        if (m_iJpegCount >= iMaxCount) {
            return;
        }

        try {
            int iYuvFormat;
            switch (m_iCameraFormat) {
                case pgDevVideoIn.PG_DEV_VIDEO_IN_FMT_YUV422SP:
                    iYuvFormat = ImageFormat.NV16;
                    break;

                case pgDevVideoIn.PG_DEV_VIDEO_IN_FMT_NV21:
                    iYuvFormat = ImageFormat.NV21;
                    break;

                case pgDevVideoIn.PG_DEV_VIDEO_IN_FMT_YUYV:
                    iYuvFormat = ImageFormat.YUY2;
                    break;

                case pgDevVideoIn.PG_DEV_VIDEO_IN_FMT_YV12:
                    iYuvFormat = ImageFormat.YV12;
                    break;

                default:
                    return;
            }

            String sJpegName = sRoot + "/cap_img_" + m_iJpegCount + ".jpg";
            File jpegFile = new File(sJpegName);
            jpegFile.createNewFile();

            FileOutputStream filecon = new FileOutputStream(jpegFile);
            YuvImage image = new YuvImage(data, iYuvFormat, m_iCameraWidth, m_iCameraHeight, null);
            image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 75, filecon);

            Log.d("DevExtend", "CameraView.CaptureJpeg, sJpegName=" + sJpegName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        m_iJpegCount++;
    }

    private int m_iJpegCount = 0;

    private void CameraOpen() {
        try {
            if (m_Camera == null) {

                int iCameraNo = -1;
                int iCameraInd = -1;
                int iCameraIndFront = -1;
                Camera cameraTemp = null;

                for (int iInd = 0; iInd < Camera.getNumberOfCameras(); iInd++) {
                    CameraInfo info = new CameraInfo();
                    Camera.getCameraInfo(iInd, info);
                    if (info.facing == m_iCameraNo) {
                        iCameraInd = iInd;
                        iCameraNo = m_iCameraNo;
                        Log.d("DevExtend", "CameraView.CameraOpen: Select iCameraNo=" + m_iCameraNo);
                        break;
                    }
                    if (iCameraIndFront < 0 && info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                        iCameraIndFront = iInd;
                    }
                }

                // If not 'iCameraNo' camera, select front camera.
                if (iCameraInd < 0 && iCameraIndFront >= 0) {
                    iCameraInd = iCameraIndFront;
                    iCameraNo = CameraInfo.CAMERA_FACING_FRONT;
                    Log.d("DevExtend", "CameraView.CameraOpen: Select front camera.");
                }

                // Try to open the selected camera.
                if (iCameraInd >= 0) {
                    cameraTemp = Camera.open(iCameraInd);
                }

                if (cameraTemp == null) {
                    cameraTemp = Camera.open(0);
                    if (cameraTemp == null) {
                        Log.d("DevExtend", "CameraView.CameraOpen, open camera failed.");
                        return;
                    }

                    iCameraNo = CameraInfo.CAMERA_FACING_BACK;
                }

                m_iCameraNo = iCameraNo;
                m_Camera = cameraTemp;
            } else {
                m_Camera.stopPreview();
            }

            // Check support size.
            boolean bHasSizeMode = false;
            Camera.Parameters Param = m_Camera.getParameters();
            List<Camera.Size> sizeList = Param.getSupportedPreviewSizes();
            for (int i = 0; i < sizeList.size(); i++) {
                Camera.Size size = sizeList.get(i);
                Log.d("DevExtend", "CameraView.CameraOpen: List preview size: width=" + size.width + ", height=" + size.height);
            }

            for (int i = 0; i < sizeList.size(); i++) {
                Camera.Size size = sizeList.get(i);
                if (size.width == m_iCameraWidth && size.height == m_iCameraHeight) {
                    Log.d("DevExtend", "CameraView.CameraOpen: Use preview size: width=" + size.width + ", height=" + size.height);
                    bHasSizeMode = true;
                    break;
                }
            }
            if (!bHasSizeMode) {
                Log.d("DevExtend", "CameraView.CameraOpen: Not find valid size mode");
                m_iCameraOpenStatus = 0;
                return;
            }

            // Set preview size
            Param.setPreviewSize(m_iCameraWidth, m_iCameraHeight);

            // List all preview format.
            List<Integer> fmtList = Param.getSupportedPreviewFormats();
            for (int i = 0; i < fmtList.size(); i++) {
                Log.d("DevExtend", "CameraView.CameraOpen: Format=" + fmtList.get(i));
            }

            // Select an support format.
            int[] iFmtList = new int[]{ImageFormat.NV21,
                    ImageFormat.NV16, ImageFormat.YUY2, ImageFormat.YV12};

            int iFmtSel = -1;
            for (int i = 0; i < iFmtList.length; i++) {
                for (int j = 0; j < fmtList.size(); j++) {
                    if (iFmtList[i] == fmtList.get(j)) {
                        iFmtSel = iFmtList[i];
                        break;
                    }
                    Log.d("DevExtend", "CameraView.CameraOpen: Not match format: iFmtList=" + iFmtList[i]);
                }
                if (iFmtSel >= 0) {
                    break;
                }
            }
            if (iFmtSel < 0) {
                m_iCameraOpenStatus = 0;
                return;
            }

            Log.d("DevExtend", "CameraView.CameraOpen: Format=, iFmtSel=" + iFmtSel);
            Param.setPreviewFormat(iFmtSel);

            switch (iFmtSel) {
                case ImageFormat.NV16:
                    m_iCameraFormat = pgDevVideoIn.PG_DEV_VIDEO_IN_FMT_YUV422SP;
                    break;

                case ImageFormat.NV21:
                    m_iCameraFormat = pgDevVideoIn.PG_DEV_VIDEO_IN_FMT_NV21;
                    break;

                case ImageFormat.YUY2:
                    m_iCameraFormat = pgDevVideoIn.PG_DEV_VIDEO_IN_FMT_YUYV;
                    break;

                case ImageFormat.YV12:
                    m_iCameraFormat = pgDevVideoIn.PG_DEV_VIDEO_IN_FMT_YV12;
                    break;
            }

            // Set preview frame rates.
            int iRateInput = (1000 / m_iCameraFrmRate);
            int iRateSet = iRateInput;

            int iDeltaMin = 65536;
            List<Integer> listRate = m_Camera.getParameters().getSupportedPreviewFrameRates();
            for (int i = 0; i < listRate.size(); i++) {
                Log.d("DevExtend", "CameraView.CameraOpen: List preview rate=" + listRate.get(i));
            }

            for (int i = 0; i < listRate.size(); i++) {
                int iRateTemp = listRate.get(i);
                int iDeltaTemp = iRateTemp - iRateInput;
                if (iDeltaTemp < 0) {
                    iDeltaTemp = -iDeltaTemp;
                }
                if (iDeltaTemp < iDeltaMin) {
                    iDeltaMin = iDeltaTemp;
                    iRateSet = iRateTemp;
                }
            }

            Log.d("DevExtend", "CameraView.CameraOpen: Param.setPreviewFrameRate, iRateSet=" + iRateSet);
            Param.setPreviewFrameRate(iRateSet);

            // Set rotate
            if (m_iCameraRotate != 0) {
                Param.set("rotation", (m_iCameraRotate % 360));
                m_Camera.setDisplayOrientation((m_iCameraRotate % 360));
            }

            m_Camera.setParameters(Param);

            int iTempSize = (m_iCameraWidth * m_iCameraHeight);
            iTempSize = (iTempSize * ImageFormat.getBitsPerPixel(iFmtSel)) / 8;
            m_byBufPreview = new byte[iTempSize];
            m_Camera.setPreviewCallbackWithBuffer(this);
            m_Camera.addCallbackBuffer(m_byBufPreview);

            m_Camera.setPreviewDisplay(m_Holder);
            m_Camera.startPreview();

            // Start success.
            m_iCameraOpenStatus = 1;

            Log.d("DevExtend", "CameraView.CameraOpen, startPreview.");
        } catch (Exception ex) {
            m_Camera.setPreviewCallback(null);
            m_Camera.stopPreview();
            m_Camera.release();
            m_Camera = null;
            m_byBufPreview = null;
            m_iCameraOpenStatus = 0;
            Log.d("DevExtend", "CameraView.CameraOpen, ex=" + ex.toString());
        }
    }

    private void CameraClose() {
        if (m_Camera != null) {
            try {
                m_Camera.setPreviewCallback(null);
                m_Camera.stopPreview();
                m_Camera.release();
                m_Camera = null;
                m_byBufPreview = null;
                m_iCameraCloseStatus = 1;
            } catch (Exception ex) {
                m_Camera = null;
                Log.d("DevExtend", "CameraView.CameraClose, ex=" + ex.toString());
                m_iCameraCloseStatus = 0;
            }
        }

        Log.d("DevExtend", "CameraView.CameraClose");
    }
}
