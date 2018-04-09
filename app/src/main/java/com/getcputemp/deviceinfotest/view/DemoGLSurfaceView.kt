package com.getcputemp.deviceinfotest.view

import android.content.Context
import android.opengl.GLSurfaceView

/**
 * Created by Administrator on 2018/4/8.
 */
class DemoGLSurfaceView : GLSurfaceView {
    var mRenderer: GetGPURenderer
    constructor(context: Context) : super(context) {
        setEGLConfigChooser(8, 8, 8, 8, 0, 0)
        mRenderer = GetGPURenderer()
        setRenderer(mRenderer)
    }
}