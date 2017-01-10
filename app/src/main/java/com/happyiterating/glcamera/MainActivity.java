package com.happyiterating.glcamera;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import com.happyiterating.gl.GWGLSurfaceView;

public class MainActivity extends Activity {

    protected RelativeLayout mGPUImageCtn;
    protected GLSurfaceView mCameraSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mGPUImageCtn = (RelativeLayout) findViewById(R.id.surface_container);

        mCameraSurfaceView = new GWGLSurfaceView(this);
        mGPUImageCtn.addView(mCameraSurfaceView,
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mCameraSurfaceView != null) {
            mCameraSurfaceView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCameraSurfaceView != null) {
            mCameraSurfaceView.onResume();
        }
    }

}
