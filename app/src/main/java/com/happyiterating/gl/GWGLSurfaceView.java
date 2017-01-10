package com.happyiterating.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by guowei on 09/01/2017.
 */

public class GWGLSurfaceView extends GLSurfaceView {

    GWGLRenderer mRenderer;
    public GWGLSurfaceView(Context context)
    {
        super(context);
        setEGLContextClientVersion(2);
        mRenderer = new GWGLRenderer();
        mRenderer.setSurfaceView(this);
        mRenderer.setContext(context);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public GWGLRenderer getRenderer()
    {
        return mRenderer;
    }

    public void onPause()
    {
        if (mRenderer != null) {
            mRenderer.onPause();
        }
        super.onPause();
    }

    public void onResume()
    {
        if (mRenderer != null) {
            mRenderer.onResume();
        }
        super.onResume();
    }
}
