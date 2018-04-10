package com.getcputemp.deviceinfotest.network

import android.Manifest
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.TrafficStats
import android.telephony.TelephonyManager

import java.util.HashMap

object NetworkUtil {
    val TYPE_UNAVAILABLE = 0
    val TYPE_WIFI = 1
    val TYPE_2G = 2
    val TYPE_3G = 3
    val TYPE_4G = 4
    val TYPE_UNKNOWN_MOBILE_NET = 10
    val TYPE_UNKNOWN_NET = 100

    fun isNetAvailable(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = manager.activeNetworkInfo
        return info != null && info.isAvailable
    }

    fun getNetWorkType(context: Context): Int {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = manager.activeNetworkInfo
        var networkType = NetworkUtil.TYPE_UNAVAILABLE
        if (info == null || !info.isAvailable) {
            networkType = NetworkUtil.TYPE_UNAVAILABLE // 网络未打开或不可用
        } else if (info.type == ConnectivityManager.TYPE_WIFI) {
            networkType = NetworkUtil.TYPE_WIFI // wifi网络
        } else if (info.type == ConnectivityManager.TYPE_MOBILE) {
            val subType = info.subtype
            if (subType == TelephonyManager.NETWORK_TYPE_CDMA
                    || subType == TelephonyManager.NETWORK_TYPE_GPRS
                    || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                networkType = NetworkUtil.TYPE_2G // 2g
            } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS
                    || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                networkType = NetworkUtil.TYPE_3G // 3g
            } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {
                networkType = NetworkUtil.TYPE_4G // 4g
            } else {
                networkType = NetworkUtil.TYPE_UNKNOWN_MOBILE_NET // 移动网络，但是不知道是那种
            }
        } else {
            networkType = NetworkUtil.TYPE_UNKNOWN_NET // 网络是打开的，但不知道是那种
        }

        return networkType
    }

    fun getAppsUsingBandwidth(context: Context): Map<String, AppNetworkItem> {
        val pm = context.packageManager
        val pkgs = pm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES or PackageManager.GET_PERMISSIONS)

        val appWifiSpeedMap = HashMap<String, AppNetworkItem>()
        val selfPkg = context.packageName

        for (pkg in pkgs) {
            if (pkg == null || selfPkg == pkg.packageName) {
                continue
            }
            val permissions = pkg.requestedPermissions ?: continue
            for (p in permissions) {
                if (Manifest.permission.INTERNET.equals(p, ignoreCase = true)) {
                    val appRxBytes = TrafficStats.getUidRxBytes(pkg.applicationInfo.uid)
                    val appTxBytes = TrafficStats.getUidTxBytes(pkg.applicationInfo.uid)
                    val totalBytes = appRxBytes + appTxBytes
                    if (totalBytes == 0L) {
                        break
                    }
                    val appNetworkItem = AppNetworkItem()
                    appNetworkItem.appName = pkg.applicationInfo.name
                    appNetworkItem.pname = pkg.packageName
                    appNetworkItem.totalNetworkBytes = totalBytes
                    appWifiSpeedMap.put(pkg.packageName, appNetworkItem)
                    break
                }
            }
        }
        return appWifiSpeedMap
    }

    fun getAppsUsingBandwidth(context: Context, pkgs: List<String>?): Map<String, AppNetworkItem>? {
        if (pkgs == null || pkgs.size == 0) {
            return null
        }
        val pm = context.packageManager
        val selfPkg = context.packageName
        val appWifiSpeedMap = HashMap<String, AppNetworkItem>()
        for (pkg in pkgs) {
            if (pkg == selfPkg) {
                continue
            }
            val pkgInfo: PackageInfo
            try {
                pkgInfo = pm.getPackageInfo(pkg, PackageManager.GET_PERMISSIONS)
            } catch (e: PackageManager.NameNotFoundException) {
                continue
            }

            val permissions = pkgInfo.requestedPermissions ?: continue
            for (p in permissions) {
                if (Manifest.permission.INTERNET.equals(p, ignoreCase = true)) {
                    val appRxBytes = TrafficStats.getUidRxBytes(pkgInfo.applicationInfo.uid)
                    val appTxBytes = TrafficStats.getUidTxBytes(pkgInfo.applicationInfo.uid)
                    val totalBytes = appRxBytes + appTxBytes
                    if (totalBytes == 0L) {
                        break
                    }
                    val appNetworkItem = AppNetworkItem()
                    appNetworkItem.appName = pkgInfo.applicationInfo.name
                    appNetworkItem.pname = pkgInfo.packageName
                    appNetworkItem.totalNetworkBytes = totalBytes
                    appWifiSpeedMap.put(pkgInfo.packageName, appNetworkItem)
                    break
                }
            }
        }
        return appWifiSpeedMap
    }
}
