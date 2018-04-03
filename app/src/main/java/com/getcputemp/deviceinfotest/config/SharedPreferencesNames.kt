package com.getcputemp.deviceinfotest.config

object SharedPreferencesNames {
    // 存储action上次时间和时间间隔的SharedPreferences
    val SP_Name_Action_Time = "AAA_TEE"

    // request urls cached in SP
    val SP_Name_Request_Urls = "RRR_URR"

    // register clientid SP
    val SP_Name_ClientId = "cid"

    // all the things which you don't need to save separately
    val SP_Name_Action_Set = "A_St"

    // 只用来存储固定通知栏开关与否
    val SP_Name_Fixed_Notif = "sp_fixed_notif"

}
