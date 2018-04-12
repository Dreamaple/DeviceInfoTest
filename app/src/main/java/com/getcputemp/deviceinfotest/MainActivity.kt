package com.getcputemp.deviceinfotest

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.getcputemp.deviceinfotest.Util.DeviceInfo
import com.getcputemp.deviceinfotest.Util.PermissionUtils
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import android.content.pm.PackageManager
import android.os.Build
import com.getcputemp.deviceinfotest.model.BatteryInfoBean
import android.content.Intent
import android.content.IntentFilter
import com.getcputemp.deviceinfotest.broadcastReceiver.BatteryInfoBroadcastReceiver
import com.getcputemp.deviceinfotest.model.EventInfoMessage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import android.content.Context
import android.hardware.Sensor.*
import android.hardware.SensorManager
import android.os.Handler
import com.getcputemp.deviceinfotest.model.GPUInfoBean
import com.getcputemp.deviceinfotest.view.DemoGLSurfaceView
import java.text.DecimalFormat
import java.util.ArrayList


class MainActivity : AppCompatActivity() {
    private var deviceInfo:DeviceInfo?=null
    private var b_baseInfo:Boolean = true
    private var b_batteryInfo:Boolean = true
    private var cpuMaxList: MutableList<String> = ArrayList()
    private var cpuMinList: MutableList<String> = ArrayList()
    private var cpuCurList: MutableList<String> = ArrayList()
    private var mCPUCoreNum:Int = DeviceInfo.getNumCores()
    private val handler = Handler()
    private var flag:Boolean = true
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
        val glView = DemoGLSurfaceView(this@MainActivity)
        ll_main_glview.addView(glView)
        b_baseInfo= ll_main_base_info.visibility==View.VISIBLE
        b_batteryInfo= ll_main_battery_info.visibility==View.VISIBLE
        deviceInfo = DeviceInfo.getInstance(applicationContext)
        pb_main_loading.visibility = View.VISIBLE

//        tv_main_phone_modle.
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {

            grantResults
                    .filter { it != PackageManager.PERMISSION_GRANTED }
                    .forEach { return }
            when (requestCode) {
                200 -> takePhoto()
            }
        } else {
            Toast.makeText(this, "该功能需要相机和读写文件权限", Toast.LENGTH_SHORT).show()
        }
    }
    private fun initView(){
        tv_main_resolution.text = "分辨率："+deviceInfo!!.display
        tv_main_android_version.text = "Android版本："+deviceInfo!!.sdk
        tv_main_phone_modle.text = "设备型号："+android.os.Build.MODEL
        tv_main_ram.text = "运行内存："+deviceInfo!!.formatSize(deviceInfo!!.getTotalMemory(applicationContext))
        tv_main_sdcard.text = "SDCard："+deviceInfo!!.formatSize(deviceInfo!!.sdCardMemory.get(0))
        tv_main_rom.text = "ROM："+deviceInfo!!.formatSize(deviceInfo!!.romMemroy.get(0))
        tv_main_screen_size.text = "屏幕尺寸"+deviceInfo!!.getScreenSizeInInch(this@MainActivity)
        tv_main_root.text = "ROOT："+deviceInfo!!.hasRootPermission()

//        getSensorList()
        var senserBooleanArray:BooleanArray=deviceInfo!!.getSenser(this@MainActivity.applicationContext)
        var i = 0
        var temp:String
        var str1 = StringBuffer()
        for (item in senserBooleanArray){
            when(i){
                TYPE_ACCELEROMETER->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("加速度传感器：$temp \n")}
                TYPE_AMBIENT_TEMPERATURE ->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("温度传感器：$temp \n")}
                TYPE_ACCELEROMETER->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("加速度传感器：$temp \n")}
                TYPE_GRAVITY ->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("重力传感器：$temp \n")}
                TYPE_GYROSCOPE ->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("陀螺仪传感器：$temp \n")}
                TYPE_LIGHT  ->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("光线感应传感器：$temp \n")}
                TYPE_LINEAR_ACCELERATION ->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("线性加速度传感器：$temp \n")}
                TYPE_MAGNETIC_FIELD ->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("磁力传感器传感器：$temp \n")}
                TYPE_ORIENTATION ->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("方向传感器：$temp \n")}
                TYPE_PRESSURE ->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("压力传感器：$temp \n")}
                TYPE_PROXIMITY ->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("接近传感器：$temp \n")}
                TYPE_RELATIVE_HUMIDITY ->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("湿度传感器：$temp \n")}
                TYPE_ROTATION_VECTOR ->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("旋转矢量传感器：$temp \n")}
                TYPE_SIGNIFICANT_MOTION ->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("特殊动作触发传感器：$temp \n")}
                TYPE_STEP_COUNTER ->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("计步传感器：$temp \n")}
                TYPE_STEP_DETECTOR ->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("步行检测传感器：$temp \n")}
                TYPE_AMBIENT_TEMPERATURE->{if (item){temp="支持"}else{temp="不支持"}
                    str1.append("温度传感器：$temp \n")}
            }
            i++
            if(!str1.none())
            tv_main_senser_list.text = str1
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tv_main_cpu.text="CPU："+deviceInfo!!.getCpuName()
        }else{
            tv_main_cpu.text="CPU："+android.os.Build.CPU_ABI
        }
        if(PermissionUtils.checkPermission(this@MainActivity
                ,arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE),200)){
            takePhoto()
        }

        btn_main_base_info.setOnClickListener {
            if (b_baseInfo){
                ll_main_base_info.visibility = View.GONE
            }else{
                ll_main_base_info.visibility = View.VISIBLE
            }
            b_baseInfo = !b_baseInfo
        }
        btn_main_battery_info.setOnClickListener {
            if (b_batteryInfo){
                ll_main_battery_info.visibility = View.GONE
            }else{
                ll_main_battery_info.visibility = View.VISIBLE
            }
            b_batteryInfo = !b_batteryInfo
        }
    }
    @SuppressLint("SetTextI18n")
    private fun takePhoto() {
        tv_main_front_camera.text = "前置摄像头："+DeviceInfo.getCameraPixels(DeviceInfo.HasFrontCamera())
        tv_main_rear_camera.text = "后置摄像头："+DeviceInfo.getCameraPixels(DeviceInfo.HasBackCamera());
        getBattery()
    }
    private fun getBattery(){
        val receiver = BatteryInfoBroadcastReceiver()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        this@MainActivity.registerReceiver(receiver, filter)

    }


    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun  eventThread(eventInfoMessage: EventInfoMessage<BatteryInfoBean> ) {
        when(eventInfoMessage.tempFlag){
            0->{
                tv_main_battery_now.text ="当前电量："+eventInfoMessage.infoData!!.batteryLevel
                tv_main_battery_batt_vol.text ="电池电压："+ eventInfoMessage.infoData!!.batteryBattVol
                tv_main_battery_batt_temp.text ="电池温度："+ eventInfoMessage.infoData!!.batteryBattTemp
                tv_main_battery_status.text ="电池状态："+ eventInfoMessage.infoData!!.batteryStatus
                tv_main_battery_health.text ="健康状态："+ eventInfoMessage.infoData!!.batteryHealth
                tv_main_battery_present.text ="是否存在电池："+ eventInfoMessage.infoData!!.batteryPresent
                tv_main_battery_capacity.text ="电池总容量："+ eventInfoMessage.infoData!!.batteryCapacity
                tv_main_battery_technology.text ="电池技术："+ eventInfoMessage.infoData!!.batteryTechnology
                tv_main_usb_online.text ="充电方式："+ eventInfoMessage.infoData!!.usbOnline
                iv_main_battery_icon.setImageResource(eventInfoMessage.infoData!!.iconSmallId)
                pb_main_loading.visibility=View.GONE
//                val glView = DemoGLSurfaceView(this@MainActivity)
//                ll_main_glview.addView(glView)
            }
        }
    }
    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun  eventThread1(eventInfoMessage: EventInfoMessage<GPUInfoBean> ) {
        when(eventInfoMessage.tempFlag){
            1->{
                tv_main_gpu_list.text = """${eventInfoMessage.infoData!!.GLRenderer}
                    |${eventInfoMessage.infoData!!.GLVendor}""".trimMargin()
            }
        }
    }
    private fun getSensorList() {
        // 获取传感器管理器
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // 获取全部传感器列表
        val sensors = sensorManager.getSensorList(TYPE_ALL)

        // 打印每个传感器信息
        val strLog = StringBuilder()
        var iIndex = 1
        for (item in sensors) {
            strLog.append(iIndex.toString() + ".")
            strLog.append(" Sensor Type - " + item.type + "\r\n")
            strLog.append(" Sensor Name - " + item.name + "\r\n")
            strLog.append(" Sensor Version - " + item.version + "\r\n")
            strLog.append(" Sensor Vendor - " + item.vendor + "\r\n")
            strLog.append(" Maximum Range - " + item.maximumRange + "\r\n")
            strLog.append(" Minimum Delay - " + item.minDelay + "\r\n")
            strLog.append(" Power - " + item.power + "\r\n")
            strLog.append(" Resolution - " + item.resolution + "\r\n")
            strLog.append("\r\n")
            iIndex++
        }
        println(strLog.toString())
        tv_main_senser_list.text=strLog.toString()
    }

    private fun cpuInfoThread(){
        object : Thread() {
            override fun run() {//在run()方法实现业务逻辑；
                //...
                var strb1:StringBuffer= StringBuffer()
                while (flag) {
                    strb1.setLength(0)
                    cpuMaxList.clear()
                    cpuMinList.clear()
                    cpuCurList.clear()
                    strb1.append("CPU核心数：$mCPUCoreNum\n")
                for (i in 0 until mCPUCoreNum) {
                    cpuMaxList.add(getKHz(DeviceInfo.getMaxCpuFreq(i)))
                    cpuMinList.add(getKHz(DeviceInfo.getMinCpuFreq(i)))
                    cpuCurList.add(getKHz(DeviceInfo.getCurCpuFreq(i)))
                    strb1.append("Cpu$i Max:${cpuMaxList.get(i)} Min:${cpuMinList.get(i)} Cur:${cpuCurList.get(i)}\n")
                }
                    handler.post {
                        if (flag){
                            tv_main_cpu_info.setText(strb1)
                        }
                    }
                    sleep(1000)
                }
            }
        }.start()
    }

    private fun getKHz(hzStr: String): String {
        try {
            val hz = Integer.parseInt(hzStr)
            val df = DecimalFormat("###.0")
            return df.format(hz.toDouble() / 1000)
        } catch (e: Exception) {
            return "N/A"
        }

    }

    override fun onResume() {
        flag = true
        cpuInfoThread()
        initView()
        super.onResume()
    }

    override fun onPause() {
        flag = false
        super.onPause()
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
