package com.happyiterating.glcamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.Window;

import com.happyiterating.gl.GWCameraManager;
import com.happyiterating.util.GWLog;

import net.majorkernelpanic.streaming.gl.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class DemoSurface extends Activity implements SurfaceHolder.Callback,
        ImageReader.OnImageAvailableListener {

    private SurfaceView mSurfaceView;
    private GWCameraManager mCameraManager;
    private ImageReader mImageReader;
    public static final String TAG = "DemoSurface";
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    public static int mFrames = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_demo_surface);

        GWLog.setLogLevel(0);
        mCameraManager = new GWCameraManager(getApplicationContext());
        mCameraManager.initCamera();
        mSurfaceView = (SurfaceView)findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        GWLog.d(TAG, "surfaceCreated");
        if (mCameraManager != null) {
            mSurfaceView.startGLThread();
            mCameraManager.setPreviewTexture(mSurfaceView.getSurfaceTexture());
            mCameraManager.startPreview();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        GWLog.d(TAG, "surface width = %d, height = %d", width, height);
        startBackgroundThread();
        mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 5);
        //mImageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 5);
        mImageReader.setOnImageAvailableListener(this, mBackgroundHandler);
        if (mSurfaceView != null) {
            mSurfaceView.addMediaCodecSurface(mImageReader.getSurface());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopBackgroundThread();
        mSurfaceView.removeMediaCodecSurface();
        if (mCameraManager != null) {
            mCameraManager.stopPreview();
        }
        if (null != mImageReader) {
            mImageReader.close();
            mImageReader = null;
        }
    }


    @Override
    public void onImageAvailable(ImageReader reader) {
        //mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage()));
        mBackgroundHandler.post(new ImageSaver(reader.acquireLatestImage()));
//        final Image image = reader.acquireNextImage();
//        int width = image.getWidth();
//        int height = image.getHeight();
//        GWLog.d(TAG, "width = %d, height = %d", width, height);
//        image.close();
    }


    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class ImageSaver implements Runnable {

        private final Image mImage;
        private final File mFile;

        public ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        public ImageSaver(Image image) {
            mImage = image;
            mFile = null;
        }

        @Override
        public void run() {
            Log.d(TAG, "Image Avaible");
            int width = mImage.getWidth();
            int height = mImage.getHeight();
            int stride = mImage.getPlanes()[0].getRowStride();
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            GWLog.d(TAG, "width = %d, height = %d, stride = %d", width, height, stride);

            mFrames ++;

            if ((mFrames % 30) == 0) {
                //Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                //bitmap.copyPixelsFromBuffer(buffer);
                long timestamp = System.currentTimeMillis();
                saveImage(mImage, new File("/sdcard/Download/test/" + timestamp + ".jpg"));
                //saveBmpToFile(bitmap, new File("/sdcard/Download/test/" + timestamp + ".jpg"));

//                byte[] bytes = new byte[buffer.remaining()];
//                buffer.get(bytes);
//                FileOutputStream output = null;
//                long timestamp = System.currentTimeMillis();
//                File file = new File("/sdcard/Download/test/" + timestamp + ".jpg");
//                try {
//                    output = new FileOutputStream(file);
//                    output.write(bytes);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    //mImage.close();
//                    if (null != output) {
//                        try {
//                            output.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
            }

            mImage.close();
        }
    }

    public static boolean saveBmpToFile(Bitmap bmp, File file) {
        if (null == bmp || null == file) {
            Log.e(TAG, "bmp or file is null");
            return false;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return writeToFile(baos.toByteArray(), file);
    }

    public static boolean writeToFile(byte[] data, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Reference
     * http://stackoverflow.com/questions/26673127/android-imagereader-acquirelatestimage-returns-invalid-jpg
     */

    public static void saveImage(Image image, File file) {
        if (image == null) {
            return;
        }
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        if (buffer == null) {
            return;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;

        byte[] data = new byte[width * height * 4];
        int offset = 0;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int pixel = 0;
                pixel |= (buffer.get(offset) & 0xff) << 16;     // R
                pixel |= (buffer.get(offset + 1) & 0xff) << 8;  // G
                pixel |= (buffer.get(offset + 2) & 0xff);       // B
                pixel |= (buffer.get(offset + 3) & 0xff) << 24; // A
                bitmap.setPixel(j, i, pixel);
                offset += pixelStride;
            }
            offset += rowPadding;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            return;
        }

    }
}
