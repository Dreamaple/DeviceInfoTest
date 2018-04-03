package com.getcputemp.deviceinfotest.network;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.telephony.TelephonyManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkUtil {
	public static final int TYPE_UNAVAILABLE = 0;
	public static final int TYPE_WIFI = 1;
	public static final int TYPE_2G = 2;
	public static final int TYPE_3G = 3;
	public static final int TYPE_4G = 4;
	public static final int TYPE_UNKNOWN_MOBILE_NET = 10;
	public static final int TYPE_UNKNOWN_NET = 100;
	
	public static boolean isNetAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		return (info != null && info.isAvailable());
	}
	
	public static int getNetWorkType(Context context)
	{
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		int networkType = NetworkUtil.TYPE_UNAVAILABLE;
		if(info == null || !info.isAvailable()) {
			networkType = NetworkUtil.TYPE_UNAVAILABLE; // 网络未打开或不可用
		} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			networkType = NetworkUtil.TYPE_WIFI; // wifi网络
		} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			int subType = info.getSubtype();
			if(subType == TelephonyManager.NETWORK_TYPE_CDMA
					|| subType == TelephonyManager.NETWORK_TYPE_GPRS
					|| subType == TelephonyManager.NETWORK_TYPE_EDGE) {
				networkType = NetworkUtil.TYPE_2G; // 2g
			} else if (subType == TelephonyManager.NETWORK_TYPE_UMTS
					|| subType == TelephonyManager.NETWORK_TYPE_HSDPA
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_A
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
				networkType = NetworkUtil.TYPE_3G; // 3g
			} else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {
				networkType = NetworkUtil.TYPE_4G; // 4g
			} else {
				networkType = NetworkUtil.TYPE_UNKNOWN_MOBILE_NET; // 移动网络，但是不知道是那种
			}
		} else {
			networkType = NetworkUtil.TYPE_UNKNOWN_NET; // 网络是打开的，但不知道是那种
		}
		
		return networkType;
	}

	public static Map<String, AppNetworkItem> getAppsUsingBandwidth(Context context)
	{
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> pkgs = pm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);

		Map<String, AppNetworkItem> appWifiSpeedMap = new HashMap<String, AppNetworkItem>();
		String selfPkg = context.getPackageName();

		for(PackageInfo pkg : pkgs) {
			if(pkg == null || selfPkg.equals(pkg.packageName)) {
				continue;
			}
			String permissions[] = pkg.requestedPermissions;
			if(permissions == null) {
				continue;
			}
			for (String p : permissions){
				if(Manifest.permission.INTERNET.equalsIgnoreCase(p)) {
					long appRxBytes = TrafficStats.getUidRxBytes(pkg.applicationInfo.uid);
					long appTxBytes = TrafficStats.getUidTxBytes(pkg.applicationInfo.uid);
					long totalBytes = appRxBytes + appTxBytes;
					if(totalBytes == 0) {
						break;
					}
					AppNetworkItem appNetworkItem = new AppNetworkItem();
					appNetworkItem.appName = pkg.applicationInfo.name;
					appNetworkItem.pname = pkg.packageName;
					appNetworkItem.totalNetworkBytes = totalBytes;
					appWifiSpeedMap.put(pkg.packageName, appNetworkItem);
					break;
				}
			}
		}
		return appWifiSpeedMap;
	}

	public static Map<String, AppNetworkItem> getAppsUsingBandwidth(Context context, List<String> pkgs)
	{
		if(pkgs == null || pkgs.size() == 0) {
			return null;
		}
		PackageManager pm = context.getPackageManager();
		String selfPkg = context.getPackageName();
		Map<String, AppNetworkItem> appWifiSpeedMap = new HashMap<String, AppNetworkItem>();
		for(String pkg : pkgs) {
			if(pkg.equals(selfPkg)) {
				continue;
			}
			PackageInfo pkgInfo;
			try {
				pkgInfo = pm.getPackageInfo(pkg, PackageManager.GET_PERMISSIONS);
			} catch (PackageManager.NameNotFoundException e) {
				continue;
			}
			String permissions[] = pkgInfo.requestedPermissions;
			if(permissions == null) {
				continue;
			}
			for (String p : permissions){
				if(Manifest.permission.INTERNET.equalsIgnoreCase(p)) {
					long appRxBytes = TrafficStats.getUidRxBytes(pkgInfo.applicationInfo.uid);
					long appTxBytes = TrafficStats.getUidTxBytes(pkgInfo.applicationInfo.uid);
					long totalBytes = appRxBytes + appTxBytes;
					if(totalBytes == 0) {
						break;
					}
					AppNetworkItem appNetworkItem = new AppNetworkItem();
					appNetworkItem.appName = pkgInfo.applicationInfo.name;
					appNetworkItem.pname = pkgInfo.packageName;
					appNetworkItem.totalNetworkBytes = totalBytes;
					appWifiSpeedMap.put(pkgInfo.packageName, appNetworkItem);
					break;
				}
			}
		}
		return appWifiSpeedMap;
	}
}
