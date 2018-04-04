package com.getcputemp.deviceinfotest.model


class EventInfoMessage<T>() {
    var infoData:T ?=null
    lateinit var message:String
    var tempFlag:Int = 0
}