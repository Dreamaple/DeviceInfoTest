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
import com.getcputemp.deviceinfotest.model.BatteryInfo


class MainActivity : AppCompatActivity() {
    var deviceInfo:DeviceInfo?=null
    var batteryInfo:BatteryInfo= BatteryInfo()
    var b_baseInfo:Boolean = true
    var b_batteryInfo:Boolean = true
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        deviceInfo = DeviceInfo.getInstance(applicationContext)
        batteryInfo = deviceInfo!!.battery
                pb_main_loading.visibility = View.VISIBLE
        initView()
//        tv_main_phone_modle.
        pb_main_loading.visibility=View.GONE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0) {

            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED)
                    return
            }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tv_main_cpu.text="CPU："+deviceInfo!!.getCpuInfo()[0]+deviceInfo!!.getCpuInfo()[1]
        }else{
            tv_main_cpu.text="CPU："+android.os.Build.CPU_ABI
        }
        if(PermissionUtils.checkPermission(this@MainActivity
                ,arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE),200)){
            takePhoto()
        }
        tv_main_battery_status.text = batteryInfo.batteryStatus
        tv_main_battery_health.text = batteryInfo.batteryHealth
        tv_main_battery_present.text = batteryInfo.batteryPresent
        tv_main_battery_capacity.text = batteryInfo.batteryCapacity
        tv_main_battery_batt_vol.text = batteryInfo.batteryBattVol
        tv_main_battery_batt_temp.text = batteryInfo.batteryBattTemp
        tv_main_battery_technology.text = batteryInfo.batteryTechnology
        tv_main_usb_online.text = batteryInfo.usbOnline
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
    private fun takePhoto() {
        tv_main_front_camera.text = "前置摄像头："+DeviceInfo.getCameraPixels(DeviceInfo.HasFrontCamera())
        tv_main_rear_camera.text = "后置摄像头："+DeviceInfo.getCameraPixels(DeviceInfo.HasBackCamera());
    }
}
