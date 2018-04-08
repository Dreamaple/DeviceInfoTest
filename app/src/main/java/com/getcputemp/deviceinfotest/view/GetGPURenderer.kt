package com.getcputemp.deviceinfotest.view

import android.opengl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLSurfaceView
import android.util.Log
import com.getcputemp.deviceinfotest.model.EventInfoMessage
import com.getcputemp.deviceinfotest.model.GPUInfoBean
import org.greenrobot.eventbus.EventBus


/**
 * Created by Administrator on 2018/4/8.
 */
class GetGPURenderer : GLSurfaceView.Renderer {
    override fun onSurfaceCreated(gl: GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        Log.d("SystemInfo", "GL_RENDERER = " + gl!!.glGetString(GL10.GL_RENDERER))
        Log.d("SystemInfo", "GL_VENDOR = " + gl.glGetString(GL10.GL_VENDOR))
        Log.d("SystemInfo", "GL_VERSION = " + gl.glGetString(GL10.GL_VERSION))
        Log.i("SystemInfo", "GL_EXTENSIONS = " + gl.glGetString(GL10.GL_EXTENSIONS))
        var gpuInfoBean:GPUInfoBean = GPUInfoBean()
        gpuInfoBean.GLRenderer = gl.glGetString(GL10.GL_RENDERER)
        gpuInfoBean.GLVendor = gl.glGetString(GL10.GL_VENDOR)
        gpuInfoBean.GLVersion = gl.glGetString(GL10.GL_VERSION)
        gpuInfoBean.GLExtensions = gl.glGetString(GL10.GL_EXTENSIONS)
        var eventInfoMessage:EventInfoMessage<GPUInfoBean> = EventInfoMessage()
        eventInfoMessage.tempFlag = 1
        eventInfoMessage.infoData = gpuInfoBean
        EventBus.getDefault().post(eventInfoMessage);
    }



    override fun onDrawFrame(arg0: GL10) {
        // TODO Auto-generated method stub

    }


    override fun onSurfaceChanged(arg0: GL10, arg1: Int, arg2: Int) {
        // TODO Auto-generated method stub

    }

}