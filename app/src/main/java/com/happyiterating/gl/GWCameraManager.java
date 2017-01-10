package com.happyiterating.gl;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import com.happyiterating.util.GWLog;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by guowei on 09/01/2017.
 */

public class GWCameraManager {

    private Camera mCamera;
    private String TAG = "GWCameraManager";

    public final static int HIGH_PHONE_WIDTH = 720;
    public final static int HIGH_PHONE_HEIGH = 1280;
    public final static int MAX_FRAME_RATE = 30;

    int mDisplayRotate;
    Point mPreviewSize;
    int mMaxWidth;
    int mMaxHeight;

    // 分辨率系数，选取摄像头预览和图片大小的时候，需要与预期值进行比例和差距加权求出差异值，然后取差异最小的
    final static double COEFFICIENT = 1000.0d;

    private Context mContext;
    boolean mUseFrontCamera;

    public GWCameraManager(Context context) {
        mContext = context;
        mUseFrontCamera = true;
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);

        mMaxWidth = HIGH_PHONE_WIDTH;
        mMaxHeight = HIGH_PHONE_HEIGH;
    }

    public GWCameraManager(Context context, int cameraId) {
        mContext = context;
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mUseFrontCamera = true;
        } else {
            mUseFrontCamera = false;
        }
        mCamera = Camera.open(cameraId);

        mMaxWidth = HIGH_PHONE_WIDTH;
        mMaxHeight = HIGH_PHONE_HEIGH;
    }


    public void setPreviewTexture(SurfaceTexture surfaceTexture) {
        try {
            //mCamera.setDisplayOrientation(90);
            mCamera.setPreviewTexture(surfaceTexture);
        }
        catch (IOException ioe) {
            Log.w(TAG, "CAM LAUNCH FAILED");
        }
    }

    public boolean initCamera() {
        boolean status = true;
        int cameraId = mUseFrontCamera ? Camera.CameraInfo.CAMERA_FACING_FRONT :
                Camera.CameraInfo.CAMERA_FACING_BACK;
        //initRotateDegree(cameraId);
        setRotation();
        safeSetPreviewFrameRate(mCamera);
        //safeSetPreviewSize(mCamera);
        setPreviewSize();
        return status;
    }


    public void startPreview() {
        mCamera.startPreview();
    }

    public void stopPreview() {
        mCamera.stopPreview();
    }

    public void release() {
        mCamera.release();
    }


    void safeSetPreviewFrameRate(Camera camera) {
        Camera.Parameters params = camera.getParameters();
        int fitRate = -1;

        @SuppressWarnings("deprecation")
        List<Integer> rateList = params.getSupportedPreviewFrameRates();
        if (null == rateList || 0 == rateList.size()) {
            Log.e(TAG, "getSupportedPrviewFrameRates failed");
            return;
        }

        for (Integer rate : rateList) {
            Log.d(TAG, "supportPriviewFrameRate, rate: " + rate);
            if (rate <= MAX_FRAME_RATE && (-1 == fitRate || rate > fitRate)) {
                fitRate = rate;
            }
        }

        if (-1 == fitRate) {
            Log.e(TAG, "can't find fit rate, use camera default value");
            return;
        }

        try {
            Log.i(TAG, "setPreviewFrameRate, fitRate: " + fitRate);
            //noinspection deprecation
            params.setPreviewFrameRate(fitRate);
            camera.setParameters(params);
        } catch (Exception e) {
            Log.e(TAG, "setPreviewFrameRate failed, " + e.getMessage());
        }
    }

    boolean setRotation() {
        mCamera.setDisplayOrientation(90);
        return true;
    }

    boolean setPreviewSize() {
        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewSize(1280, 720);
        mCamera.setParameters(params);
        return true;
    }

    boolean safeSetPreviewSize(Camera camera) {
        Camera.Parameters params = camera.getParameters();
        Point size = null;

        List<Camera.Size> sizeLst = params.getSupportedPreviewSizes();
        if (null == sizeLst || 0 == sizeLst.size()) {
            Log.e(TAG, "getSupportedPrviewSizes failed");
            return false;
        }

        int diff = Integer.MAX_VALUE;
        for (Camera.Size it : sizeLst) {
            int width = it.width;
            int height = it.height;
            if (mDisplayRotate == 90 || mDisplayRotate == 270) {
                height = it.width;
                width = it.height;
            }

            GWLog.d(TAG, "supportPreview, width: %d, height: %d", width, height);
            if (width * height <= mMaxHeight * mMaxWidth) {
                int newDiff = diff(height, width, mMaxHeight, mMaxWidth);
                GWLog.d(TAG, "diff: " + newDiff);
                if (null == size || newDiff < diff) {
                    size = new Point(it.width, it.height);
                    diff = newDiff;
                }
            }
        }

        if (null == size) {
            Collections.sort(sizeLst, new Comparator<Camera.Size>() {
                @Override
                public int compare(Camera.Size lhs, Camera.Size rhs) {
                    return lhs.width * lhs.height - rhs.width * rhs.height;
                }
            });

            Camera.Size it = sizeLst.get(sizeLst.size() / 2);
            size = new Point(it.width, it.height);
        }

        try {
            GWLog.i(TAG, "setPreviewSize, width: %d, height: %d", size.x, size.y);
            params.setPreviewSize(size.x, size.y);
            if (mDisplayRotate == 90 || mDisplayRotate == 270) {
                mPreviewSize = new Point(size.y, size.x);
            } else {
                mPreviewSize = new Point(size.x, size.y);
            }
            camera.setParameters(params);
        } catch (Exception e) {
            GWLog.e(TAG, "setPreviewSize failed, " + e.getMessage());
            return false;
        }
        return true;
    }

    int diff(double realH, double realW, double expH, double expW) {
        double rateDiff = Math.abs(COEFFICIENT * (realH / realW - expH / expW));
        return (int) (rateDiff + Math.abs(realH - expH) + Math.abs(realW - expW));
    }

    public static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    void initRotateDegree(int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        GWLog.d(TAG, "cameraId: %d, roation: %d",
                cameraId, info.orientation);
        int rotation = getWindowManager(mContext).getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        mDisplayRotate = (info.orientation - degrees + 360) % 360;
    }

}
