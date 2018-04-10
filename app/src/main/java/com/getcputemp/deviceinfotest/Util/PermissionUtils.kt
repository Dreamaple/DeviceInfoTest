package com.getcputemp.deviceinfotest.Util

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat

import java.util.ArrayList

/**
 * Created by Administrator on 2018/4/2.
 */

object PermissionUtils {
    /**
     * 检查单个权限
     * @param activity
     * @param permission
     * @param code
     * @return
     */
    fun checkPermission(activity: Activity, permission: String, code: Int): Boolean {
        return checkPermission(activity, arrayOf(permission), code)
    }

    /**
     * 申请没有的权限,已申请的会被过滤,
     * 如果没有过滤,会导致已申请的权限再次申请
     * 返回申请失败,不执行相应的方法
     * @param activity
     * @param permission
     * @param code
     * @return
     */
    fun checkPermission(activity: Activity, permission: Array<String>, code: Int): Boolean {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            val permissions = ArrayList<String>()
            for (per in permission) {
                if (ContextCompat.checkSelfPermission(activity, per) != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(per)
                }
            }
            if (permissions.size > 0) {
                activity.requestPermissions(permissions.toTypedArray(), code)
                return false
            }
        }
        return true
    }
}
