package com.getcputemp.deviceinfotest.network

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.text.TextUtils

/**
 * Created by ft on 2018/1/23.
 */

class AppNetworkItem : Comparable<AppNetworkItem> {

    var appName: String = String()
    var pname: String? = null
    var totalNetworkBytes: Long = 0
    var speed: Long = 0
    var icon: Drawable? = null

    fun getIcon(pm: PackageManager?): Drawable? {
        if (this.icon != null || pm == null || this.pname == null) {
            return this.icon
        }

        try {
            this.icon = pm.getApplicationIcon(this.pname)
        } catch (e: PackageManager.NameNotFoundException) {
            //                e.printStackTrace();
        } catch (e: Resources.NotFoundException) {
            //                e.printStackTrace();
        }

        return this.icon
    }

    fun getIcon(context: Context): Drawable? {
        if (this.icon != null) {
            return this.icon
        }

        try {
            val pm = context.packageManager
            this.icon = pm.getApplicationIcon(this.pname)
        } catch (e: PackageManager.NameNotFoundException) {
            //                e.printStackTrace();
        } catch (e: Resources.NotFoundException) {
            //                e.printStackTrace();
        }

        return this.icon
    }

    fun getAppName(context: Context): String {
        if (TextUtils.isEmpty(appName)) {
            val pm = context.packageManager
            val packageInfo: PackageInfo
            try {
                packageInfo = pm.getPackageInfo(this.pname, 0)
                this.appName = packageInfo.applicationInfo.loadLabel(pm).toString()
            } catch (e: PackageManager.NameNotFoundException) {
            }

        }
        return appName
    }

    override fun compareTo(a: AppNetworkItem): Int {
        return (a.speed - this.speed).toInt()
    }

}
