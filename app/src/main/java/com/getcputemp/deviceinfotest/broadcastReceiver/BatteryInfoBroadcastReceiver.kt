package com.getcputemp.deviceinfotest.broadcastReceiver

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context;
import android.os.BatteryManager.*
import com.getcputemp.deviceinfotest.model.BatteryInfoBean
import com.getcputemp.deviceinfotest.model.EventInfoMessage
import org.greenrobot.eventbus.EventBus



 class BatteryInfoBroadcastReceiver:BroadcastReceiver() {// 广播接收器

    override fun onReceive(context:Context, intent:Intent) {// 接受广播
    if (Intent.ACTION_BATTERY_CHANGED == intent.action)
        {// 判断Action
            var temp:String = String()
            val level = intent.getIntExtra("level", 0)// 取得电池剩余容量
            val scale = intent.getIntExtra("scale", 100)// 取得电池总理
            val voltage = intent.getIntExtra("voltage", 100)// 取得电池的电压
            val status = intent.getIntExtra("status", 100)// 取得电池的电压
            val present = intent.getIntExtra("present", 100)// 判断当前是否存在电池
            val Icon_small = intent.getIntExtra("Icon-small", 100)// 取得电池对应的图标 ID
            val plugged = intent.getIntExtra("plugged", 100)// 连接的电源插座类型
            val health = intent.getIntExtra("health", 100)// 取得电池的健康状态
            var batteryInfoBean: BatteryInfoBean = BatteryInfoBean()
            var eventInfoMessage:EventInfoMessage<BatteryInfoBean> = EventInfoMessage()
            batteryInfoBean.batteryBattTemp = ""+level
            batteryInfoBean.batteryBattVol = ""+voltage
            batteryInfoBean.batteryStatus=when(status){
                BATTERY_STATUS_CHARGING->"电池充电状态"
                BATTERY_STATUS_DISCHARGING->"电池放电状态"
                BATTERY_STATUS_FULL ->"电池满电状态"
                BATTERY_STATUS_NOT_CHARGING ->"电池不充电状态"
                BATTERY_STATUS_UNKNOWN ->"电池未知状态"
                else->"无法获取数据"
            }
            batteryInfoBean.batteryHealth=when(health){
                BATTERY_HEALTH_DEAD ->"电池损坏"
                BATTERY_HEALTH_GOOD ->"电池健康"
                BATTERY_HEALTH_OVERHEAT  ->"电池过热"
                BATTERY_HEALTH_OVER_VOLTAGE  ->"电池电压过大"
                BATTERY_HEALTH_UNSPECIFIED_FAILURE ->"未明示故障"
                else->"无法获取数据"
            }
            batteryInfoBean.usbOnline=when(plugged){
                BATTERY_PLUGGED_USB->"USB 电源"
                BATTERY_PLUGGED_AC->"交流电电源"
                else->"无法获取数据"
            }

            eventInfoMessage.infoData = batteryInfoBean
            eventInfoMessage.tempFlag = 0
            EventBus.getDefault().post(eventInfoMessage);
//        val dialog = AlertDialog.Builder(context)// 创建对话框
//        .setTitle("电池电量")// 设置标题
//        .setMessage(
//                "电池电量为：" + (level * 100 / scale).toString()
//                + "%")// 设置信息
//        .setNegativeButton("关闭",
//                // 设置监听操作  // 设置取消按钮
//                DialogInterface.OnClickListener { dialog, which -> }).create()// 创建Dialog
//        dialog.show()// 显示对话框
        }
    }

}