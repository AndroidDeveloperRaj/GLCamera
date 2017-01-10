package com.happyiterating.gl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by guowei on 09/01/2017.
 */

public class GWGLRenderer implements GLSurfaceView.Renderer,
        SurfaceTexture.OnFrameAvailableListener {

    //TextureManager mTextureManager;
    GWBaseFilter mBaseFilter;
    int mTextureId;
    private SurfaceTexture mSurfaceTexture;
    private GWGLSurfaceView mSurfaceView;
    private GWCameraManager mCameraManager;
    private Context mContext;

    public void setSurfaceView(GWGLSurfaceView surfaceView) {
        mSurfaceView = surfaceView;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void onSurfaceCreated(GL10 unused, javax.microedition.khronos.egl.EGLConfig config) {
        //mTextureManager = new TextureManager();
        //mSurfaceTexture = mTextureManager.createTexture();
        mBaseFilter = new GWBlueorangeFilter();
        //mBaseFilter = new GWOriginalFilter();
        mSurfaceTexture = mBaseFilter.createTexture();
        mSurfaceTexture.setOnFrameAvailableListener(this);

        mCameraManager = new GWCameraManager(mContext);
        mCameraManager.setPreviewTexture(mSurfaceTexture);
        mCameraManager.initCamera();
        mCameraManager.startPreview();
    }

    public void onDrawFrame(GL10 unused)
    {
        float[] mtx = new float[16];
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mtx);

        /*if(mTextureManager != null){
            mTextureManager.drawFrame();
        }*/
        if (mBaseFilter != null) {
            mBaseFilter.drawFrame();
        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
    }

    static public int loadShader(int type, String shaderCode)
    {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    static private int createTexture()
    {
        int[] texture = new int[1];

        GLES20.glGenTextures(1,texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }

    public void setSurface(SurfaceTexture _surface)
    {
        mSurfaceTexture = _surface;
    }

    public SurfaceTexture getSurface() {
        return mSurfaceTexture;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (mSurfaceView != null) {
            mSurfaceView.requestRender();
        }
    }

    public void onPause()
    {
        if (mCameraManager != null) {
            mCameraManager.stopPreview();
            mCameraManager.release();
        }
    }

    public void onResume() {

    }
}
