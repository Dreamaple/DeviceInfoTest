package com.getcputemp.deviceinfotest.Util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;

import com.getcputemp.deviceinfotest.config.SharedPreferencesNames;
import com.getcputemp.deviceinfotest.model.BatteryInfoBean;
import com.getcputemp.deviceinfotest.network.NetworkUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//import java.security.Signature;

public class DeviceInfo {
	private TelephonyManager tm = null;
	private static DeviceInfo deviceInfo = null;
	private Context context = null;



	private DeviceInfo(Context context)	{
		if(tm == null) {
			tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		}
		this.context = context;
	}
	
	public static DeviceInfo getInstance(Context context){
		if(DeviceInfo.deviceInfo == null) {
            DeviceInfo.deviceInfo = new DeviceInfo(context.getApplicationContext());
		}


        return deviceInfo;
	}
	
	public boolean isGPVersion()
	{
		return true;
	}
	
	public String getClientId(){
		String clientId = "";
		SharedPreferences sp = context.getSharedPreferences(SharedPreferencesNames.INSTANCE.getSP_Name_ClientId(), Context.MODE_PRIVATE);
		clientId = sp.getString("cid", "");
		clientId = clientId.trim();
		return clientId;
	}
	
	public void setClientId(String clientId)
	{
		SharedPreferences sp = context.getSharedPreferences(SharedPreferencesNames.INSTANCE.getSP_Name_ClientId(), Context.MODE_PRIVATE);
		Editor spEditor = sp.edit();
		spEditor.putString("cid", clientId);
		spEditor.apply();
	}
	
	public int getPid()
	{
		int pid = 4;
		return pid;
	}

    /**
     * deprecated, only return empty string
     * @return
     */
//	public String getImei()
//	{
//		String imei = "";
////		try {
////			imei = tm.getDeviceId();
////		} catch(SecurityException se) {
////
////		}
//		return imei;
//	}
	
	public String getAndroidId()
	{
		String androidId = System.getString(context.getContentResolver(), Secure.ANDROID_ID);
		return androidId;
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private String getSerial()
	{
		return Build.SERIAL;
	}
	
	public String getAndroidSerial()
	{
		String serail = "";
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			serail = this.getSerial();
		}
		return serail;
	}
	
	public String getModel()
	{
		return Build.MODEL;
	}
	
	public String getPackageName(Context context)
	{
		String pname = context.getPackageName();
		return pname;
	}
	
	public int getVersionCode(Context context) {
        int verCode = 0;
        try {
        	String pname = getPackageName(context);
            verCode = context.getPackageManager().getPackageInfo(pname, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return verCode;
    }
	
	public String getVersionName(Context context) {
		String verName = "";
        try {
        	String pname = getPackageName(context);
        	verName = context.getPackageManager().getPackageInfo(pname, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return verName;
    }

	public String getSignPubKey()
	{
		Map<String, String> signHash = getSignInfo();
		if(signHash == null) {
			return "";
		}
		String pubKey = signHash.get("pubKey");
		if(pubKey == null) {
			pubKey = "";
		}
		return pubKey;
	}
	
	public String getSignNumber()
	{
		Map<String, String> signHash = getSignInfo();
		if(signHash == null) {
			return "";
		}
		String signNumber = signHash.get("signNumber");
		if(signNumber == null) {
			signNumber = "";
		}
		return signNumber;
	}
	
	private Map<String, String> getSignInfo() {
        try {
        	String pname = getPackageName(context);
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(pname, PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            Map<String, String> signInfo = parseSignature(sign.toByteArray());
            return signInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	public String getSignMD5()
	{
        try {
        	String pname = getPackageName(context);
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(pname, PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            if(signs == null || signs.length == 0) {
            	return "";
            }
            Signature sign = signs[0];
            String signMd5 = MD5Util.encodeByMD5(sign.toCharsString());
            return signMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
 
    private Map<String, String> parseSignature(byte[] signature) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
            String pubKey = cert.getPublicKey().toString();
            String signNumber = cert.getSerialNumber().toString();
            Map<String, String> signInfo = new HashMap<String, String>();
            signInfo.put("pubKey", pubKey);
            signInfo.put("signNumber", signNumber);
            return signInfo;
        } catch (CertificateException e) {
            e.printStackTrace();
            return null;
        }
    }

	public String getManufacturer()
	{
		return Build.MANUFACTURER;
	}

	public String getOSManufacturer()
	{
		return Build.MANUFACTURER;
	}
	public String getOSVersion()
	{
		String version = Build.VERSION.RELEASE;
		return version;
	}

	public String getOS()
	{
		return "android";
	}



	public int getSDK()
	{
		int sdk = Build.VERSION.SDK_INT;
		return sdk;
	}

	public String getDisplay(){
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return dm.widthPixels + "*" + dm.heightPixels;
	}

    public String getLanguage()
	{
		Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String country = locale.getCountry();
        if(!("".equals(country))) {
        	language = language + "_" + country;
        }
        return language;
	}

	public String getChannelId()
	{
		String channelId = "";
		return channelId;
	}

	public String getReferrer()
	{
		String referrer = "";
		SharedPreferences sp = context.getSharedPreferences(SharedPreferencesNames.INSTANCE.getSP_Name_ClientId(), Context.MODE_PRIVATE);
		referrer = sp.getString("referrer", "");
		if(referrer == null) {
			referrer = "";
		}
		referrer = referrer.trim();
		return referrer;
	}

	public void setReferrer(String referrer)
	{
		SharedPreferences sp = context.getSharedPreferences(SharedPreferencesNames.INSTANCE.getSP_Name_ClientId(), Context.MODE_PRIVATE);
		Editor spEditor = sp.edit();
		spEditor.putString("referrer", referrer);
		spEditor.apply();
	}

	public int getNetWorkType()
	{
		//int network = tm.getNetworkType();
		int network = NetworkUtil.getNetWorkType(context);
		return network;
	}

	public String getSimSerialNumber()
	{
		String simSerial = "";
		try {
			simSerial = tm.getSimSerialNumber();
		} catch(SecurityException se) {

		}
		return simSerial;
	}

	public String getSimCountryIso()
	{
		String simCountryIso = "";
		try {
			simCountryIso = tm.getSimCountryIso();
		} catch (SecurityException se) {

		}
		return simCountryIso;
	}

	public String getMccMnc()
	{
		String mccMnc = "";
		try {
			mccMnc = tm.getNetworkOperator();
		} catch(SecurityException se) {

		}
		return mccMnc;
	}

	public boolean isNetworkRoaming ()
	{
		boolean isRoaming = false;
		try {
			isRoaming = tm.isNetworkRoaming();
		} catch(SecurityException se) {

		}
		return isRoaming;
	}

	public String getNetworkOperatorName()
	{
		String networkOpName = "";
		try {
			networkOpName = tm.getNetworkOperatorName();
		} catch(SecurityException se) {

		}
		return networkOpName;
	}

	public String getCPUSerial()
	{
		String cpuAddress = "0000000000000000";
        try {
            //cat cpuinfo file
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader inputStream = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(inputStream);
            // find serial one line by one line
            for (int i = 1; i < 1000; i++) {
            	String str = input.readLine();
                if (str != null) {
                    if (str.indexOf("Serial") > -1) {
                        String strCPU = str.substring(str.indexOf(":") + 1, str.length());
                        cpuAddress = strCPU.trim();
                        break;
                    }
                } else {
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cpuAddress;
	}

	public String getMacAddress()
	{
		String macAddr = "";
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if(wifiManager != null) {
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			if(wifiInfo != null) {
				macAddr = wifiInfo.getMacAddress();
			}
		}
		return macAddr;
	}

	public String[] getTotalMemory()
	{
        String[] result = {"",""};  //1-total 2-avail
        MemoryInfo mi = new MemoryInfo();
        //mActivityManager.getMemoryInfo(mi);    
        long mTotalMem = 0;  
        long mAvailMem = mi.availMem;  
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        try {  
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();  
            arrayOfString = str2.split("\\s+");  
            mTotalMem = Long.valueOf(arrayOfString[1]).intValue() * 1024;
            localBufferedReader.close();  
        } catch (IOException e) {
            e.printStackTrace();  
        }  
        result[0] = Formatter.formatFileSize(context, mTotalMem);
        result[1] = Formatter.formatFileSize(context, mAvailMem);
        return result;  
    }
	
	public long getAvailableMemory(Context context)
    {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long availableMemory = memoryInfo.availMem;
        return availableMemory;
    }

	public long getTotalMemory(Context context)
    {
        long mTotalMem = 0;
        // 文件格式
//        MemTotal:        1920740 kB
//        MemFree:           82016 kB
//        Buffers:          255248 kB
        
        String file = "/proc/meminfo";
        String firstLine = "";
        String[] arrayOfString;
        try {
            FileReader localFileReader = new FileReader(file);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 1024);
            firstLine = localBufferedReader.readLine();
            arrayOfString = firstLine.split("\\s+");
            if(arrayOfString.length >= 2) {
            	mTotalMem = Long.valueOf(arrayOfString[1]).longValue() * 1024; // kb转成byte
            }
            localBufferedReader.close();  
        } catch (IOException e) {
        } catch (OutOfMemoryError e) {
        }
        return mTotalMem;
    }
	
	public boolean hasRootPermission(){
        PrintWriter printWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            printWriter = new PrintWriter(process.getOutputStream());
            printWriter.flush();
            printWriter.close();
            int value = process.waitFor();  
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(process!=null){
                process.destroy();
            }
        }
        return false;
    }
	
	private boolean returnResult(int value){
        // 代表成功  
        if (value == 0) {
            return true;
        } else if (value == 1) { // 失败
            return false;
        } else { // 未知情况
            return false;
        }  
    }
    public static final int CAMERA_FACING_BACK = 0;
    public static final int CAMERA_FACING_FRONT = 1;
    public static final int CAMERA_NONE = 2;

    public static int HasBackCamera()
    {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CAMERA_FACING_BACK) {
                return i;
            }
        }
        return 2;
    }

    public static int HasFrontCamera()
    {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CAMERA_FACING_FRONT) {
                return i;
            }
        }
        return 2;
    }

    public static String getCameraPixels(int paramInt)
    {
        if (paramInt == 2)
            return "无";
        Camera localCamera = Camera.open(paramInt);
        Camera.Parameters localParameters = localCamera.getParameters();
        localParameters.set("camera-id", 1);
        List<Camera.Size> localList = localParameters.getSupportedPictureSizes();
        if (localList != null)
        {
            int heights[] = new int[localList.size()];
            int widths[] = new int[localList.size()];
            for (int i = 0; i < localList.size(); i++)
            {
                Camera.Size size = (Camera.Size) localList.get(i);
                int sizehieght = size.height;
                int sizewidth = size.width;
                heights[i] = sizehieght;
                widths[i] =sizewidth;
            }
            int pixels = getMaxNumber(heights) * getMaxNumber(widths);
            localCamera.release();
            return String.valueOf(pixels / 10000) + " 万";
        }
        else return "无";

    }

    public static int getMaxNumber(int[] paramArray)
    {
        int temp = paramArray[0];
        for(int i = 0;i<paramArray.length;i++)
        {
            if(temp < paramArray[i])
            {
                temp = paramArray[i];
            }
        }
        return temp;
    }
    public long[] getRomMemroy() {
        long[] romInfo = new long[2];
        //Total rom memory
        romInfo[0] = getTotalInternalMemorySize();

        //Available rom memory
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        romInfo[1] = blockSize * availableBlocks;
        getVersion();
        return romInfo;
    }

    public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }
    public long[] getSDCardMemory() {
        long[] sdCardInfo=new long[2];
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long bSize = sf.getBlockSize();
            long bCount = sf.getBlockCount();
            long availBlocks = sf.getAvailableBlocks();

            sdCardInfo[0] = bSize * bCount;//总大小
            sdCardInfo[1] = bSize * availBlocks;//可用大小
        }
        return sdCardInfo;
    }
    public String[] getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2="";
        String[] cpuInfo={"",""};
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return cpuInfo;
    }
    public String[] getVersion(){
        String[] version={"null","null","null","null"};
        String str1 = "/proc/version";
        String str2;
        String[] arrayOfString;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            version[0]=arrayOfString[2];//KernelVersion
            localBufferedReader.close();
        } catch (IOException e) {
        }
        version[1] = Build.VERSION.RELEASE;// firmware version
        version[2]=Build.MODEL;//model
        version[3]=Build.DISPLAY;//system version
        return version;
    }
    public String formatSize(long size) {
        String suffix = null;
        float fSize=0;

        if (size >= 1024) {
            suffix = "KB";
            fSize=size / 1024;
            if (fSize >= 1024) {
                suffix = "MB";
                fSize /= 1024;
            }
            if (fSize >= 1024) {
                suffix = "GB";
                fSize /= 1024;
            }
        } else {
            fSize = size;
        }
        java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }
    public double getScreenSizeInInch(Activity context){
        double inch = 0;
        DisplayMetrics dm = new DisplayMetrics();

        context.getWindowManager().getDefaultDisplay().getMetrics(dm);

        //获取屏幕的宽和高
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
//        Log.e("11111111",""+dm.xdpi);
//        Log.e("11111111",""+dm.ydpi);
//        float density = dm.density;      // 屏幕密度
//        float densityDpi = dm.ydpi;  // 屏幕密度DPI
        inch = Math.sqrt(Math.pow(screenWidth/dm.xdpi,2)+Math.pow(screenHeight/dm.ydpi,2));
        inch = (double) Math.round(inch * 100) / 100;
        return inch;
    }
    public String getCpuName(){

        String str1 = "/proc/cpuinfo";
        String str2 = "";

        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr);
            while ((str2=localBufferedReader.readLine()) != null) {
                if (str2.contains("Hardware")) {
                    return str2.split(":")[1];
                }
            }
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return null;

    }

    public boolean[] getSenser(Context context){
        boolean[] senserCompare = new boolean[21];
        int[] senserType = new int[0];
        // 获取传感器管理器
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        // 获取全部传感器列表
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // 打印每个传感器信息
        StringBuilder strLog = new StringBuilder();
        int iIndex = 1;
        for (Sensor item : sensors) {
//            senserType[iIndex-1]= Integer.parseInt(item.getName());
            if(item.getType()>0&&item.getType()<=20){
                senserCompare[item.getType()]=true;
            }
//            strLog.append(iIndex + ".");
//            strLog.append(" Sensor Type - " + item.getType() + "\r\n");
//            strLog.append(" Sensor Name - " + item.getName() + "\r\n");
//            strLog.append(" Sensor Version - " + item.getVersion() + "\r\n");
//            strLog.append(" Sensor Vendor - " + item.getVendor() + "\r\n");
//            strLog.append(" Maximum Range - " + item.getMaximumRange() + "\r\n");
//            strLog.append(" Minimum Delay - " + item.getMinDelay() + "\r\n");
//            strLog.append(" Power - " + item.getPower() + "\r\n");
//            strLog.append(" Resolution - " + item.getResolution() + "\r\n");
//            strLog.append("\r\n");
//            iIndex++;
        }
        Arrays.sort(senserType);
        return senserCompare;
    }
}
