package com.getcputemp.deviceinfotest.network;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

/**
 * Created by ft on 2018/1/23.
 */

public class AppNetworkItem implements Comparable<AppNetworkItem> {

    public String appName;
    public String pname;
    public long totalNetworkBytes;
    public long speed;
    private Drawable icon;

    public Drawable getIcon(){
        return this.icon;
    }

    public Drawable getIcon(PackageManager pm) {
        if(this.icon != null || pm == null || this.pname == null) {
            return this.icon;
        }

        try {
            this.icon = pm.getApplicationIcon(this.pname);
        } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
        } catch (Resources.NotFoundException e){
//                e.printStackTrace();
        }

        return this.icon;
    }

    public Drawable getIcon(Context context) {
        if(this.icon != null) {
            return this.icon;
        }

        try {
            PackageManager pm = context.getPackageManager();
            this.icon = pm.getApplicationIcon(this.pname);
        } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
        } catch (Resources.NotFoundException e){
//                e.printStackTrace();
        }

        return this.icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName(Context context){
        if(TextUtils.isEmpty(appName)){
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo;
            try {
                packageInfo = pm.getPackageInfo(this.pname, 0);
                this.appName = packageInfo.applicationInfo.loadLabel(pm).toString();
            } catch (PackageManager.NameNotFoundException e) {
            }

            }
        return appName;
    }

    @Override
    public int compareTo(AppNetworkItem a) {
        return (int)(a.speed - this.speed);
    }

}
