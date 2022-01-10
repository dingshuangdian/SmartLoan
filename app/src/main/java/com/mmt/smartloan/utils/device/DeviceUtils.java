package com.mmt.smartloan.utils.device;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.DhcpInfo;
import android.net.Proxy;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import static android.content.Context.WIFI_SERVICE;

//import com.happy.dce.device.SimCardInfo;

/**
 * Created by zhonglq on 2016/6/3.
 */
public class DeviceUtils {

    private static final String TAG = DeviceUtils.class.getSimpleName();

    private static final String FILE_MEMORY = "/proc/meminfo";

    /**
     * 获取app屏幕尺寸
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * 获取开机到现在的毫秒数(包括睡眠时间)
     */
    public static long getElapsedRealtime() {
        return SystemClock.elapsedRealtime();
    }

    /**
     * 获取开机到现在的毫秒数(不包括睡眠时间)
     */
    public static long getUpdateMills() {
        return SystemClock.uptimeMillis();
    }

    public static int getPhoneType(Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Activity.TELEPHONY_SERVICE);
        return manager.getPhoneType();
    }

    public static String getAndroidId(Context context) {
        try {
            return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        } catch (Exception e) {

        }
        return "unknown";
    }


    public static int getSysVersion() {
        return Build.VERSION.SDK_INT;
    }


    public static String getNetWorkOperatorName(Context context) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
            String operatorName = "";
            if (manager != null) {
                operatorName = manager.getNetworkOperatorName();
            }
            return operatorName;
        } catch (Exception e) {
            return "unknown";
        }
    }

    @SuppressLint({"MissingPermission", "WrongConstant"})
    public static int getNetworkType(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        int networkType = -1;
        if (manager != null) {
            networkType = manager.getNetworkType();
        }
        return networkType;
    }


    /**
     * 获取是否开启USB调试
     */
    public static boolean isOpenUSBDebug(Context context) {
        return (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) > 0);
    }

    /**
     * 获取手机信息 MI 4LTE
     */
    public static String getModelName() {
        return Build.MODEL;
    }

    /**
     * 获取硬件制造商 Xiaomi
     */
    public static String getManufacturerName() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取android系统定制商 设备品牌
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 获取产品名 设备名称
     */
    public static String getProduct() {
        return Build.PRODUCT;
    }

    /**
     * 获取主板信息
     */
    public static String getBoard() {
        return Build.BOARD;
    }


    /**
     * 获取serial信息
     */
    @SuppressLint("HardwareIds")
    public static String getSerial() {
        return Build.SERIAL;
    }

    /**
     * 获取手机是否root
     */
    public static boolean isRoot() {
        boolean bool = false;
        try {
            bool = (new File("/system/bin/su").exists()) || (new File("/system/xbin/su").exists());
            Log.d(TAG, "bool = " + bool);
        } catch (Exception ignored) {
        }
        return bool;
    }

    /**
     * 是否使用vpn
     */
    public static boolean isUsingVPN() {
        if (DeviceUtils.getSysVersion() > 14) {
            String defaultHost = Proxy.getDefaultHost();
            return !TextUtils.isEmpty(defaultHost);
        }
        return false;
    }

    /**
     * 是否使用vpn
     */
    public static boolean isUsingProxyPort() {
        if (DeviceUtils.getSysVersion() > 14) {
            int defaultPort = Proxy.getDefaultPort();
            return defaultPort != -1;
        }
        return false;
    }

    /**
     * 获取传感器信息
     */
    public static String getSensorList(Context context) {
        JSONArray jsonArray = new JSONArray();
        // 获取传感器管理器
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        // 获取全部传感器列表
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        try {
            for (Sensor item : sensors) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", String.valueOf(item.getType()));
                jsonObject.put("name", item.getName());
                jsonObject.put("version", String.valueOf(item.getVersion()));
                jsonObject.put("vendor", item.getVendor());
                jsonObject.put("maxRange", String.valueOf(item.getMaximumRange()));
                jsonObject.put("minDelay", String.valueOf(item.getMinDelay()));
                jsonObject.put("power", String.valueOf(item.getPower()));
                jsonObject.put("resolution", String.valueOf(item.getResolution()));
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    /**
     * 获取网关IP
     */
    public static String getGateWayIp(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        DhcpInfo di = wm.getDhcpInfo();
        return intToIp(di.gateway);
    }

    /**
     * 转换成normal ip
     */
    private static String intToIp(int ipInt) {
        return (ipInt & 0xFF) + "." +
                ((ipInt >> 8) & 0xFF) + "." +
                ((ipInt >> 16) & 0xFF) + "." +
                ((ipInt >> 24) & 0xFF);
    }

    /**
     * 获取可用大小
     */
    public static long getSDFreeSize() {
        // 取得SD卡文件路径
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) return 0;
        try {
            File path = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(path.getPath());
            long blockSize = sf.getBlockSize();
            long freeBlocks = sf.getAvailableBlocks();
            // 单位Byte
            return freeBlocks * blockSize;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取sd卡总大小
     */
    public static long getSDAllSize() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return 0;
        }
        try {
            File path = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(path.getPath());
            long blockSize = sf.getBlockSize();
            long allBlocks = sf.getBlockCount();
            // 单位Byte
            return allBlocks * blockSize;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取可用ram大小
     */
    public static long getFreeMem(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        // 单位Byte
        return info.availMem;
    }

    /**
     * 获取ram总大小
     */
    public static long getRamTotalSize(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        // 单位Byte
        return info.totalMem;
    }

    /**
     * 获取总内存大小
     */
    public static long getTotalMem() {
        try {
            FileReader fr = new FileReader(FILE_MEMORY);
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split("\\s+");
            Log.w(TAG, text);
            // 单位为Byte
            return Long.parseLong(array[1]) * 1024;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取默认国家/地区
     *
     * @return eg.CN或US
     */
    public static String getDefaultCountry() {
        return Locale.getDefault().getCountry();
    }

    /**
     * 获取默认语言
     *
     * @return eg.es/zh
     */
    public static String getDefaultLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取语言编码
     *
     * @return eg.es/zh
     */
    public static String getDefaultIsoLanguage() {
        return Locale.getDefault().getISO3Language();
    }

    /**
     * 获取国家编码
     *
     * @return eg.es/zh
     */
    public static String getDefaultIsoCountry() {
        return Locale.getDefault().getISO3Country();
    }

    /**
     * 获取显示语言
     *
     * @return eg.es/zh
     */
    public static String getDefaultDisplayLanguage() {
        return Locale.getDefault().getDisplayLanguage();
    }

    /**
     * 获取显示国家
     *
     * @return eg.es/zh
     */
    public static String getDefaultDisplayCountry() {
        return Locale.getDefault().getDisplayCountry();
    }

    public static boolean isDeviceInVPN() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equals("tun0") || nif.getName().equals("ppp0")) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getCustomChannel(Context context) {
        return "";
    }


    /**
     * fingerprit 信息
     **/
    public static String getDeviceFubgerprint() {
        return Build.FINGERPRINT;
    }

    /**
     * 设备名
     **/
    public static String getDeviceDevice() {
        return Build.DEVICE;
    }

    /**
     * 获取设备的唯一标识， 需要 “android.permission.READ_Phone_STATE”权限
     */
    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {
        String imei = "000000000000000";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_DENIED) {
                    imei = tm.getDeviceId() != null ? tm.getDeviceId() : "";
                }
                if (imei.equals("0")) {
                    imei = "000000000000000";
                }
                int len = 15 - imei.length();
                for (int i = 0; i < len; i++) {
                    imei += "0";
                }
                //            if ("000000000000000".equals(imei)) {
                //                return getIMEI(context);
                //            }
                return imei;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return imei;
    }

    /**
     * ID
     */
    public static String getDeviceId() {
        return Build.ID;
    }


    /**
     * SDK_INT SDK版本
     */
    public static int getDeviceSDK_INT() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * SDK_INT SDK版本
     */
    public static long getTime() {
        return Build.TIME;
    }

    /**
     * SDK_INT SDK版本
     */
    public static String getSecurityPatchLevel() {
        return Build.VERSION.SECURITY_PATCH;
    }

    /**
     * SDK_INT SDK版本
     */
    public static String getNumber() {
        return Build.DISPLAY;
    }

    /**
     * TimeZone
     */
    public static String getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return tz.getDisplayName(false, TimeZone.SHORT);
    }

    /**
     * TimeZone Id
     */
    public static String getTimeZoneId() {
        TimeZone tz = TimeZone.getDefault();
        return tz.getID();
    }

    /**
     * kernel architecture
     */
    public static String getKernelArchitecture() {
        return Build.CPU_ABI;
    }


    /**
     * CORE-VER
     * 内核版本
     * return String
     */

    public static String getKernelVersion() {
        Process process = null;
        String kernelVersion = "";
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        // get the output line
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);
        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);


        String result = "";
        String line;
        // get the whole standard output string
        try {
            while ((line = brout.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }


        try {
            System.out.println("-----" + result);
            if (result != "") {
                String Keyword = "version ";
                int index = result.indexOf(Keyword);
                line = result.substring(index + Keyword.length());
                index = line.indexOf(" ");
                kernelVersion = line.substring(0, index);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return kernelVersion;
    }


    private static String[] platforms = {
            "https://www.google.cn/",
            "http://pv.sohu.com/cityjson?ie=utf-8",
            "http://ip.chinaz.com/getip.aspx"
    };

    /**
     * InNetIp
     * return String
     */
    public static String getInNetIp(Context context) {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
//            wifiManager.setWifiEnabled(true);
            return "";
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);

        return ip;
    }

    /**
     * OutNetIp
     * return String
     */
    public static String getOutNetIP(Context context, int index) {
        if (index < platforms.length) {
            BufferedReader buff = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(platforms[index]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(5000);//读取超时
                urlConnection.setConnectTimeout(5000);//连接超时
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {//找到服务器的情况下,可能还会找到别的网站返回html格式的数据
                    InputStream is = urlConnection.getInputStream();
                    buff = new BufferedReader(new InputStreamReader(is, "UTF-8"));//注意编码，会出现乱码
                    StringBuilder builder = new StringBuilder();
                    String line = null;
                    while ((line = buff.readLine()) != null) {
                        builder.append(line);
                    }

                    buff.close();//内部会关闭 InputStream
                    urlConnection.disconnect();

                    Log.e("---", builder.toString());
                    if (index == 0 || index == 1) {
                        //截取字符串
                        int satrtIndex = builder.indexOf("{");//包含[
                        int endIndex = builder.indexOf("}");//包含]
                        String json = builder.substring(satrtIndex, endIndex + 1);//包含[satrtIndex,endIndex)
                        if (!TextUtils.isEmpty(json)) {
                            try {
                                JSONObject jo = new JSONObject(json);
                                String ip = jo.getString("cip");
                                return ip;
                            } catch (JSONException e) {
                                Log.d("mlq", "getOutNetIP_JSONException");
                                return "";
                            }

                        }

                    } else if (index == 2) {
                        JSONObject jo = new JSONObject(builder.toString());
                        return jo.getString("ip");
                    }
                }
            } catch (Exception e) {
                Log.d("mlq", "getOutNetIP_Exception");

                e.printStackTrace();
            }
        } else {
            return getInNetIp(context);
        }
        return getOutNetIP(context, ++index);
    }

    /**
     * 获取本机已配对蓝牙列表 bluetooth_saved
     * return String
     */
    public static String getSavedBluetooth(Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
//            System.out.println("本机未发现蓝牙设备。");
        } else {
            if (!bluetoothAdapter.isEnabled()) {//判断蓝牙设备是否已开起
                //开起蓝牙设备
                /*Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivity(intent);*/
                return "";
            }
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            JSONArray jsonArray = new JSONArray();
            try {
                for (Iterator<BluetoothDevice> iterator = devices.iterator(); iterator.hasNext(); ) {
                    BluetoothDevice device = iterator.next();
                    //                System.out.println(device.getAddress());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Device_name", String.valueOf(device.getName()));
                    jsonObject.put("Mac_address", String.valueOf(device.getAddress()));
                    jsonArray.put(jsonObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonArray.toString();
        }
        return "";
    }

    /**
     * 获取本机已配对蓝牙列表 bluetooth_scan
     * return String
     */
    @SuppressLint({"NewApi", "MissingPermission"})
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void getScanBluetooth(Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) { // 本机未发现蓝牙设备
            if (!bluetoothAdapter.isEnabled()) {//判断蓝牙设备是否已开起
                //开起蓝牙设备
                /*Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivity(intent);*/
                return;
            }
            if (bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.startDiscovery();
            }

        }
    }

    /**
     * MEID
     */
    public static String getMEID(Context context) {
        try {
            if (SimCardInfo.getMobSimInfo(context).getMeid() != null) {
                return SimCardInfo.getMobSimInfo(context).getMeid();
            } else {
                return "unknown";
            }
        } catch (Exception e) {
            return "unknown";
        }

    }

    /**
     * IMEI1
     */
    public static String getIMEI1(Context context) {
        try {
            if (SimCardInfo.getMobSimInfo(context).getSim1Imei() != null) {
                return SimCardInfo.getMobSimInfo(context).getSim1Imei();
            } else {
                return "000000000000000";
            }
        } catch (Exception e) {
            return "000000000000000";
        }

    }

    /**
     * IMEI2
     */
    public static String getIMEI2(Context context) {
        try {
            if (SimCardInfo.getMobSimInfo(context).getSim2Imei() != null) {
                return SimCardInfo.getMobSimInfo(context).getSim2Imei();
            } else {
                return "000000000000000";
            }
        } catch (Exception e) {
            return "000000000000000";
        }

    }

    /**
     * IMSI1
     */
    public static String getIMSI1(Context context) {
        try {
            if (SimCardInfo.getMobSimInfo(context).getSim1Imsi() != null) {
                return SimCardInfo.getMobSimInfo(context).getSim1Imsi();
            } else {
                return "unknown";
            }
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * IMSI2
     */
    public static String getIMSI2(Context context) {
        try {
            if (SimCardInfo.getMobSimInfo(context).getSim2Imsi() != null) {
                return SimCardInfo.getMobSimInfo(context).getSim2Imsi();
            } else {
                return "unknown";
            }
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * ICCID1
     */
    public static String getICCID1(Context context) {
        try {
            if (SimCardInfo.getMobSimInfo(context).getSim1IccId() != null) {
                return SimCardInfo.getMobSimInfo(context).getSim1IccId();
            } else {
                return "unknown";
            }
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * ICCID2
     */
    public static String getICCID2(Context context) {
        try {
            if (SimCardInfo.getMobSimInfo(context).getSim2IccId() != null) {
                return SimCardInfo.getMobSimInfo(context).getSim2IccId();
            } else {
                return "unknown";
            }
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * get macAddress
     *
     * @param mContext
     * @return
     */
    @SuppressLint("HardwareIds")
    public static String getMac(Context mContext) {
        if (Build.VERSION.SDK_INT >= 23) {
            return getMacForBuild();
        } else {
            try {
                return getWifiInfo(mContext).getMacAddress();
            } catch (Exception e) {
                return "unknown";
            }

        }
    }

    /**
     * 获取WifiInfo
     *
     * @param mContext
     * @return
     */
    @SuppressLint("MissingPermission")
    private static WifiInfo getWifiInfo(Context mContext) {
        WifiManager mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null) {
            return mWifiManager.getConnectionInfo();
        }
        return null;
    }

    /**
     * >=22的sdk则进行如下算法 mac
     *
     * @return
     */
    private static String getMacForBuild() {
        try {
            for (
                    Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                    networkInterfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if ("wlan0".equals(networkInterface.getName())) {
                    byte[] hardwareAddress = networkInterface.getHardwareAddress();
                    if (hardwareAddress == null || hardwareAddress.length == 0) {
                        continue;
                    }
                    StringBuilder buf = new StringBuilder();
                    for (byte b : hardwareAddress) {
                        buf.append(String.format("%02X:", b));
                    }
                    if (buf.length() > 0) {
                        buf.deleteCharAt(buf.length() - 1);
                    }
                    return buf.toString();
                }
            }
        } catch (SocketException e) {
            Log.i(TAG, e.toString());
        }
        return "unknown";
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getGsfAndroidId(Context context) {
        Uri URI = Uri.parse("content://com.google.android.gsf.gservices");
        String ID_KEY = "android_id";
        String params[] = {ID_KEY};
        Cursor c = context.getContentResolver().query(URI, null, null, params, null);
        if (!c.moveToFirst() || c.getColumnCount() < 2)
            return null;
        try {
            return Long.toHexString(Long.parseLong(c.getString(1)));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
