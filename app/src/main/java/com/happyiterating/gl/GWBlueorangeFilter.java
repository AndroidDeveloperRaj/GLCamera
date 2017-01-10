package com.happyiterating.gl;

/**
 * Created by guowei on 10/01/2017.
 */

public class GWBlueorangeFilter extends GWBaseFilter {

    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uSTMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * aPosition;\n" +
                    "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +      // highp here doesn't seem to matter
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER_BLUE_ORANGE =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2  vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "vec2 uv = vTextureCoord.xy;\n" +
                    "vec3 tex = texture2D(sTexture, uv).rgb;\n" +
                    "float shade = dot(tex, vec3(0.333333));\n" +
                    "vec3 col = mix(vec3(0.1, 0.36, 0.8) * (1.0-2.0*abs(shade-0.5)), vec3(1.06, 0.8, 0.55), 1.0-shade);\n" +
                    "gl_FragColor = vec4(col, 1.0);\n" +
                    "}\n";

    public GWBlueorangeFilter() {
        mProgram = createProgram(VERTEX_SHADER, FRAGMENT_SHADER_BLUE_ORANGE);
    }
}
