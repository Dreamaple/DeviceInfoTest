package com.getcputemp.deviceinfotest.model

import java.io.Serializable


class BatteryInfoBean :Serializable{
    var usbOnline:String=""
    var batteryStatus:String=""
    var batteryHealth:String=""
    var batteryPresent:String=""
    var batteryCapacity:String=""
    var batteryLevel:String=""
    var batteryBattVol:String=""
    var batteryBattTemp:String=""
    var batteryTechnology:String=""
    var iconSmallId:Int=0
}