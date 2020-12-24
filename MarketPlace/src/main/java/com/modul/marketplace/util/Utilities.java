package com.modul.marketplace.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.modul.marketplace.R;
import com.modul.marketplace.app.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import ly.count.android.sdk.Countly;

public class Utilities {
    public static final String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final String[] PERMISSIONS_CALL_PHONE = {Manifest.permission.CALL_PHONE};

    public static void hideKeyboard(View focusingView, Activity context) {
        try {
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (focusingView != null) {
                imm.hideSoftInputFromWindow(focusingView.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            } else {
                imm.hideSoftInputFromWindow(context.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showMessageOKCancel(Context context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.btn_ok), okListener)
                .setNegativeButton(context.getString(R.string.btn_cancel), null)
                .create()
                .show();
    }

    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void openWeb(AppCompatActivity context, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(browserIntent);
    }

    public static void sendBoard(Context context, String board, String value) {
        Intent i = new Intent();
        i.setAction(board);
        i.putExtra("value", value);
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }

    public static void sendBoardItem(Context context, String board, String value,String id,double quantity) {
        Intent i = new Intent();
        i.setAction(board);
        i.putExtra("value", value);
        i.putExtra("id", id);
        i.putExtra("quantity", quantity);
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }

    public static void sendBoardLib(Context context, String board, String value) {
        Intent i = new Intent();
        i.setAction(board);
        i.putExtra("value", value);
        context.sendBroadcast(i);
    }

    public static void sendBoardLocateLib(Context context, String board, String value,String cityId,String locateId,String locateName) {
        Intent i = new Intent();
        i.setAction(board);
        i.putExtra("value", value);
        i.putExtra("cityId", cityId);
        i.putExtra("locateId", locateId);
        i.putExtra("locateName", locateName);
        context.sendBroadcast(i);
    }

    public static void sendBoardCounlyLib(Context context, String board, String value,String event, String component,  String feature) {
        Intent i = new Intent();
        i.setAction(board);
        i.putExtra("value", value);
        i.putExtra("event", event);
        i.putExtra("component", component);
        i.putExtra("feature", feature);
        context.sendBroadcast(i);
    }

    public static void sendBoardLibString(Context context, String board, String value, String data) {
        Intent i = new Intent();
        i.setAction(board);
        i.putExtra("value", value);
        i.putExtra("object", data);
        context.sendBroadcast(i);
    }

    public static void sendBoardString(Context context, String board, String value, String data) {
        Intent i = new Intent();
        i.setAction(board);
        i.putExtra("value", value);
        i.putExtra("object", data);
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }


    private static Map<Character, Character> MAP_NORM;

    public static String removeAccents(String value) {
        if (MAP_NORM == null || MAP_NORM.size() == 0) {
            MAP_NORM = new HashMap<Character, Character>();
            MAP_NORM.put('À', 'A');
            MAP_NORM.put('Á', 'A');
            MAP_NORM.put('Â', 'A');
            MAP_NORM.put('Ã', 'A');
            MAP_NORM.put('Ä', 'A');
            MAP_NORM.put('È', 'E');
            MAP_NORM.put('É', 'E');
            MAP_NORM.put('Ê', 'E');
            MAP_NORM.put('Ë', 'E');
            MAP_NORM.put('Í', 'I');
            MAP_NORM.put('Ì', 'I');
            MAP_NORM.put('Î', 'I');
            MAP_NORM.put('Ï', 'I');
            MAP_NORM.put('Ù', 'U');
            MAP_NORM.put('Ú', 'U');
            MAP_NORM.put('Û', 'U');
            MAP_NORM.put('Ü', 'U');
            MAP_NORM.put('Ò', 'O');
            MAP_NORM.put('Ó', 'O');
            MAP_NORM.put('Ơ', 'O');
            MAP_NORM.put('Ợ', 'O');
            MAP_NORM.put('Ô', 'O');
            MAP_NORM.put('Ộ', 'O');
            MAP_NORM.put('Õ', 'O');
            MAP_NORM.put('Ö', 'O');
            MAP_NORM.put('Ñ', 'N');
            MAP_NORM.put('Ç', 'C');
            MAP_NORM.put('ª', 'A');
            MAP_NORM.put('º', 'O');
            MAP_NORM.put('§', 'S');
            MAP_NORM.put('³', '3');
            MAP_NORM.put('²', '2');
            MAP_NORM.put('¹', '1');
            MAP_NORM.put('à', 'a');
            MAP_NORM.put('á', 'a');
            MAP_NORM.put('ả', 'a');
            MAP_NORM.put('â', 'a');
            MAP_NORM.put('ậ', 'a');
            MAP_NORM.put('ầ', 'a');
            MAP_NORM.put('ã', 'a');
            MAP_NORM.put('ă', 'a');
            MAP_NORM.put('ä', 'a');
            MAP_NORM.put('ạ', 'a');
            MAP_NORM.put('è', 'e');
            MAP_NORM.put('ẻ', 'e');
            MAP_NORM.put('é', 'e');
            MAP_NORM.put('ê', 'e');
            MAP_NORM.put('ễ', 'e');
            MAP_NORM.put('ế', 'e');
            MAP_NORM.put('ệ', 'e');
            MAP_NORM.put('ề', 'e');
            MAP_NORM.put('ë', 'e');
            MAP_NORM.put('í', 'i');
            MAP_NORM.put('ì', 'i');
            MAP_NORM.put('î', 'i');
            MAP_NORM.put('ị', 'i');
            MAP_NORM.put('ỉ', 'i');
            MAP_NORM.put('ĩ', 'i');
            MAP_NORM.put('ï', 'i');
            MAP_NORM.put('ù', 'u');
            MAP_NORM.put('ú', 'u');
            MAP_NORM.put('ứ', 'u');
            MAP_NORM.put('û', 'u');
            MAP_NORM.put('ũ', 'u');
            MAP_NORM.put('ư', 'u');
            MAP_NORM.put('ü', 'u');
            MAP_NORM.put('ữ', 'u');
            MAP_NORM.put('ò', 'o');
            MAP_NORM.put('ó', 'o');
            MAP_NORM.put('ô', 'o');
            MAP_NORM.put('ỗ', 'o');
            MAP_NORM.put('ố', 'o');
            MAP_NORM.put('ờ', 'o');
            MAP_NORM.put('ồ', 'o');
            MAP_NORM.put('õ', 'o');
            MAP_NORM.put('ö', 'o');
            MAP_NORM.put('ơ', 'o');
            MAP_NORM.put('ợ', 'o');
            MAP_NORM.put('ọ', 'o');
            MAP_NORM.put('ộ', 'o');
            MAP_NORM.put('ñ', 'n');
            MAP_NORM.put('ç', 'c');
            MAP_NORM.put('d', 'd');
            MAP_NORM.put('đ', 'd');
            MAP_NORM.put('h', 'h');
            MAP_NORM.put('ỳ', 'h');
            MAP_NORM.put('ỵ', 'h');
            MAP_NORM.put('ỷ', 'h');
            MAP_NORM.put('ỹ', 'h');
        }

        if (value == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder(value);

        for (int i = 0; i < value.length(); i++) {
            Character c = MAP_NORM.get(sb.charAt(i));
            if (c != null) {
                sb.setCharAt(i, c.charValue());
            }
        }

        return sb.toString();

    }

    public static void showKeyboard(View focusingView, Context context) {
        focusingView.requestFocus();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(focusingView, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void countlyEvent(String event, String compoment, String feature, String element, String value) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("compoment", compoment);
            map.put("feature", feature);
            if (!TextUtils.isEmpty(element)) {
                map.put("element", element);
            }
            if (!TextUtils.isEmpty(value)) {
                map.put("value", value);
            }
//            Countly.sharedInstance().events().recordEvent(event, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDecode(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32
            // chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String readHTMLerror(Context ctx) {

        return readFileContent(ctx, "ketnoi.html");
    }

    public static boolean gotoApps(Context ctx, String paka) {
        Intent intent;
        intent = new Intent(Intent.ACTION_MAIN);

        try {

            PackageManager manager = ctx.getPackageManager();
            intent = manager.getLaunchIntentForPackage(paka);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ctx.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xu ly khi muon vao URL
     *
     * @param ctx
     */
    public static void gotoUrl(Context ctx, String url) {
        Intent intent;

        Log.i("Utilities.gotoUrl()", "GiangLV----> " + url);
        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            ctx.startActivity(intent);
        } catch (Exception e) {

            Log.i("Utilities.gotoUrl()", "GiangLV----> " + e.getMessage());
        }
    }


    public static String getPrice(Context context, double price) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale(
                "vi-VN"));
        return format.format(price);

    }

    public static String getTimeFromHourMinute(int hour, int minute) {
        String sMinute = "";
        String sHour = "";
        String time = "";
        if (minute < 10) {
            sMinute = "0" + minute;
        } else {
            sMinute = "" + minute;
        }

        if (hour < 10) {
            sHour = "0" + hour;
        } else {
            sHour = "" + hour;
        }
        time = sHour + ":" + sMinute;
        return time;
    }

    public static String getTimeFromTimestamp(double timestamp) {
        String time = "";

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis((long) timestamp * 1000);
        time = Utilities.getTimeFromHourMinute(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        return time;
    }

    public String ReadFileContent(Context ctx) {
        String line;
        String result = "";

        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(ctx
                    .getAssets().open("ketnoi.html")));

            while ((line = in.readLine()) != null) {
                result = result + line;
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return result;
    }

    public static String readFileContent(Context ctx, String filename) {
        String line;
        String result = "";

        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(ctx
                    .getAssets().open(filename)));

            while ((line = in.readLine()) != null) {
                result = result + line;
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return result;
    }

    /**
     * Vao ypdate
     *
     * @param context
     */
    public static void gotoMarket(Context context) {
        String appName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("market://details?id=" + appName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("http://play.google.com/store/apps/details?id="
                            + appName)));
        }

    }

    public static Intent genIntentGoMarket(Context context) {
        String appName = context.getPackageName();
        return new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
                + appName));

    }

    /**
     * Vao ypdate
     *
     * @param context
     */
    public static void gotoMarket(Context context, String paka) {
        String appName = paka;
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("market://details?id=" + appName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("http://play.google.com/store/apps/details?id="
                            + appName)));
        }

    }

    /**
     * Function to change progress to timer
     *
     * @param progress      -
     * @param totalDuration returns current duration in milliseconds
     */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = totalDuration / Constants.ONE_SECOND;
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);
        return currentDuration * Constants.ONE_SECOND;
    }

    /**
     * Function to convert milliseconds time to Timer Format
     * Hours:Minutes:Seconds
     */
    public static String milliSecondsToTimer(long milliseconds) {

        // Convert total duration into time
        int hours = (int) (milliseconds / (Constants.ONE_SECOND * 60 * 60));
        int minutes = (int) (milliseconds % (Constants.ONE_SECOND * 60 * 60))
                / (Constants.ONE_SECOND * 60);
        int seconds = (int) ((milliseconds % (Constants.ONE_SECOND * 60 * 60))
                % (Constants.ONE_SECOND * 60) / Constants.ONE_SECOND);
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(twoDigit(hours)).append(':');
        }
        sb.append(twoDigit(minutes)).append(':');
        sb.append(twoDigit(seconds));
        return sb.toString();
    }

    static public String twoDigit(int d) {
        NumberFormat formatter = new DecimalFormat("#00");
        return formatter.format(d);
    }

    public static int getProgressPercentage(long currentDuration,
                                            long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / Constants.ONE_SECOND);
        long totalSeconds = (int) (totalDuration / Constants.ONE_SECOND);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

//    public static String getIMEI(Context context) {
//        try {
//            SharedPref pref = new SharedPref(context);
//            String tmDevice = pref.getString(Constants.GETIMEI, "");
//            if (!tmDevice.equals("")) {
//                return tmDevice;
//            }
//            TelephonyManager tm = (TelephonyManager) context
//                    .getSystemService(Context.TELEPHONY_SERVICE);
//
//            tmDevice = "" + tm.getDeviceId();
//            if (tmDevice == null || tmDevice.equals("000000000000000")) {
//                WifiManager wifiMan = (WifiManager) context
//                        .getSystemService(Context.WIFI_SERVICE);
//                WifiInfo wifiInf = wifiMan.getConnectionInfo();
//                String macAddr = wifiInf.getMacAddress();
//                Log.i("Utilities.getIMEI()", "gianglv3---->MAC " + macAddr);
//                return macAddr;
//            }
//
//            Log.i("Utilities.getIMEI()", "gianglv3---->Imei " + tmDevice);
//            pref.putString(Constants.GETIMEI, tmDevice);
//            return tmDevice;
//        } catch (Exception e) {
//            e.printStackTrace();
//            String androidId = ""
//                    + Settings.Secure.getString(
//                    context.getContentResolver(),
//                    Settings.Secure.ANDROID_ID);
//            Log.i("Utilities.getIMEI()", "gianglv3----> " + androidId);
//            return androidId;
//        }
//    }

    public static String getMac(Context context) {// ANDROID MAC WIFI
        String base = "com.viettel.tinngan";
        SharedPref pref = new SharedPref(context);
        String getMac = pref.getString(Constants.GETMAC, "");
        if (!getMac.equals("")) {

            return getMac;
        }
        try {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            String macAddr = "";
            if (wifiManager.isWifiEnabled()) {
                // WIFI ALREADY ENABLED. GRAB THE MAC ADDRESS HERE
                WifiInfo info = wifiManager.getConnectionInfo();
                macAddr = info.getMacAddress();

                // NOW DISABLE IT AGAIN
                // wifiManager.setWifiEnabled(true);
            } else {
                // ENABLE THE WIFI FIRST
                wifiManager.setWifiEnabled(true);

                // WIFI IS NOW ENABLED. GRAB THE MAC ADDRESS HERE
                WifiInfo info = wifiManager.getConnectionInfo();
                macAddr = info.getMacAddress();

                // NOW DISABLE IT AGAIN
                wifiManager.setWifiEnabled(false);
            }
            base = macAddr;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("Utilities.getDeviceID()", "MAC " + base);
        getMac = base;
        pref.putString(Constants.GETMAC, getMac);
        return getMac;
    }


    // lam cho sdt co dang 84xxxxxxxxx
    public static String fixPhoneNumbTo84(String str) {
        if (str == null || str.equals("") || str.length() < 3)
            return "";

        String x = "0123456789";
        for (int i = 0; i < str.length(); i++) {

            if (x.indexOf("" + str.charAt(i)) < 0) {

                str = str.replace("" + str.charAt(i), "");
                i--;
            }
        }

        if (str.startsWith("084")) {
            str = str.substring(1);
        } else if (str.startsWith("0")) {
            str = "84" + str.substring(1);
        } else if (!str.startsWith("84")) {
            str = "84" + str;
        }

        return str.trim();
    }

    public static String fixPhoneNumb(String str) {
        String fixPhoneNumbTo84 = fixPhoneNumbTo84(str);
        if (fixPhoneNumbTo84.length() < 3) {
            return "";
        }

        return fixPhoneNumbTo84.substring(2);
    }

    public static String fixPhoneNumbTo0(String str) {
        String fixPhoneNumb = fixPhoneNumb(str);

        return "0" + fixPhoneNumb;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static String toMD5(String str) {
        String hashtext = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            final byte[] resultByte = messageDigest.digest();
            BigInteger bigInt = new BigInteger(1, resultByte);
            hashtext = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32
            // chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashtext;
    }

    public static void saveFavoriteAndLike(String json, String filename) {
        String path = android.os.Environment.getExternalStorageDirectory()
                + "/" + Constants.ROOT_FOLDER + "/";

        File createFolder = new File(path);
        if (!createFolder.exists())
            createFolder.mkdirs();

        try {
            // Create file
            Log.i("Utilities.saveFavorite()", "GiangLV----> " + path);
            FileWriter fstream = new FileWriter(path + "/" + filename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(json);
            // Close the output stream
            out.close();
            fstream.close();
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static String getFaveoriteAndLike(String filename) {
        String path = android.os.Environment.getExternalStorageDirectory()
                + "/" + Constants.ROOT_FOLDER + "/" + filename;

        String line;
        String result = "";
        File f = new File(path);
        if (!f.exists()) {
            return result;
        }
        try {
            String name = path;
            Log.i("Utilities.getFaveorite()", "GiangLV----> " + name);
            FileReader file = new FileReader(name);
            BufferedReader in = new BufferedReader(file);

            while ((line = in.readLine()) != null) {
                result = result + line;
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return result;
    }

    /**
     * Luu cache
     *
     * @param str      : noi dung
     * @param filename : ten file
     */
    public static void saveCache(String str, String filename) {
        String path = android.os.Environment.getExternalStorageDirectory()
                + "/" + Constants.ROOT_FOLDER + "/" + Constants.CACHE_FOLDER
                + "/";

        File createFolder = new File(path);
        if (!createFolder.exists())
            createFolder.mkdirs();

        try {
            // Create file
            Log.i("Utilities.writeToCache()", "GiangLV----> " + path);
            FileWriter fstream = new FileWriter(path + "/" + filename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(str);
            // Close the output stream
            out.close();
            fstream.close();
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Doc cache
     *
     * @param filename
     * @return
     */
    public static String getCache(String filename) {
        String path = android.os.Environment.getExternalStorageDirectory()
                + "/" + Constants.ROOT_FOLDER + "/" + Constants.CACHE_FOLDER
                + "/" + filename;

        String line;
        String result = "";
        File f = new File(path);
        if (!f.exists()) {
            return result;
        }
        try {
            String name = path;
            Log.i("Utilities.readFileCache()", "GiangLV----> " + name);
            FileReader file = new FileReader(name);
            BufferedReader in = new BufferedReader(file);

            while ((line = in.readLine()) != null) {
                result = result + line;
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return result;
    }

    public boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

//
//	public static Map<String, String> splitQuery(URL url)
//			throws UnsupportedEncodingException {
//		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
//		String query = url.getQuery();
//		String[] pairs = query.split("&");
//		for (String pair : pairs) {
//			int idx = pair.indexOf("=");
//			query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
//					URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
//		}
//		return query_pairs;
//	}

//
//	private static String overUrl(String url) {
//
//		int start = url.indexOf("_");
//		int fin = url.indexOf(".html");
//		if (start == -1) {
//			return "";
//		}
//		if (fin == -1) {
//			return "";
//		}
//		String temp = url.substring(start + 1, fin);
//
//		return temp;
//
//	}

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static String getAppPackage(Context context) {

        return context.getPackageName();
    }

    public static void callPhone(Activity context, String sdt) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + sdt));
            if (!Utilities.hasPermissions(context, PERMISSIONS_CALL_PHONE)) {
                ActivityCompat.requestPermissions(context, PERMISSIONS_CALL_PHONE, 1);
            } else {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Có vấn đề xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String convert(String org) {
        // convert to VNese no sign. @haidh 2008
        char[] arrChar = org.toCharArray();
        char[] result = new char[arrChar.length];
        for (int i = 0; i < arrChar.length; i++) {
            switch (arrChar[i]) {
                case '\u00E1':
                case '\u00E0':
                case '\u1EA3':
                case '\u00E3':
                case '\u1EA1':
                case '\u0103':
                case '\u1EAF':
                case '\u1EB1':
                case '\u1EB3':
                case '\u1EB5':
                case '\u1EB7':
                case '\u00E2':
                case '\u1EA5':
                case '\u1EA7':
                case '\u1EA9':
                case '\u1EAB':
                case '\u1EAD':
                case '\u0203':
                case '\u01CE': {
                    result[i] = 'a';
                    break;
                }
                case '\u00E9':
                case '\u00E8':
                case '\u1EBB':
                case '\u1EBD':
                case '\u1EB9':
                case '\u00EA':
                case '\u1EBF':
                case '\u1EC1':
                case '\u1EC3':
                case '\u1EC5':
                case '\u1EC7':
                case '\u0207': {
                    result[i] = 'e';
                    break;
                }
                case '\u00ED':
                case '\u00EC':
                case '\u1EC9':
                case '\u0129':
                case '\u1ECB': {
                    result[i] = 'i';
                    break;
                }
                case '\u00F3':
                case '\u00F2':
                case '\u1ECF':
                case '\u00F5':
                case '\u1ECD':
                case '\u00F4':
                case '\u1ED1':
                case '\u1ED3':
                case '\u1ED5':
                case '\u1ED7':
                case '\u1ED9':
                case '\u01A1':
                case '\u1EDB':
                case '\u1EDD':
                case '\u1EDF':
                case '\u1EE1':
                case '\u1EE3':
                case '\u020F': {
                    result[i] = 'o';
                    break;
                }
                case '\u00FA':
                case '\u00F9':
                case '\u1EE7':
                case '\u0169':
                case '\u1EE5':
                case '\u01B0':
                case '\u1EE9':
                case '\u1EEB':
                case '\u1EED':
                case '\u1EEF':
                case '\u1EF1': {
                    result[i] = 'u';
                    break;
                }
                case '\u00FD':
                case '\u1EF3':
                case '\u1EF7':
                case '\u1EF9':
                case '\u1EF5': {
                    result[i] = 'y';
                    break;
                }
                case '\u0111': {
                    result[i] = 'd';
                    break;
                }
                case '\u00C1':
                case '\u00C0':
                case '\u1EA2':
                case '\u00C3':
                case '\u1EA0':
                case '\u0102':
                case '\u1EAE':
                case '\u1EB0':
                case '\u1EB2':
                case '\u1EB4':
                case '\u1EB6':
                case '\u00C2':
                case '\u1EA4':
                case '\u1EA6':
                case '\u1EA8':
                case '\u1EAA':
                case '\u1EAC':
                case '\u0202':
                case '\u01CD': {
                    result[i] = 'A';
                    break;
                }
                case '\u00C9':
                case '\u00C8':
                case '\u1EBA':
                case '\u1EBC':
                case '\u1EB8':
                case '\u00CA':
                case '\u1EBE':
                case '\u1EC0':
                case '\u1EC2':
                case '\u1EC4':
                case '\u1EC6':
                case '\u0206': {
                    result[i] = 'E';
                    break;
                }
                case '\u00CD':
                case '\u00CC':
                case '\u1EC8':
                case '\u0128':
                case '\u1ECA': {
                    result[i] = 'I';
                    break;
                }
                case '\u00D3':
                case '\u00D2':
                case '\u1ECE':
                case '\u00D5':
                case '\u1ECC':
                case '\u00D4':
                case '\u1ED0':
                case '\u1ED2':
                case '\u1ED4':
                case '\u1ED6':
                case '\u1ED8':
                case '\u01A0':
                case '\u1EDA':
                case '\u1EDC':
                case '\u1EDE':
                case '\u1EE0':
                case '\u1EE2':
                case '\u020E': {
                    result[i] = 'O';
                    break;
                }
                case '\u00DA':
                case '\u00D9':
                case '\u1EE6':
                case '\u0168':
                case '\u1EE4':
                case '\u01AF':
                case '\u1EE8':
                case '\u1EEA':
                case '\u1EEC':
                case '\u1EEE':
                case '\u1EF0': {
                    result[i] = 'U';
                    break;
                }

                case '\u00DD':
                case '\u1EF2':
                case '\u1EF6':
                case '\u1EF8':
                case '\u1EF4': {
                    result[i] = 'Y';
                    break;
                }
                case '\u0110':
                case '\u00D0':
                case '\u0089': {
                    result[i] = 'D';
                    break;
                }
                default:
                    result[i] = arrChar[i];
            }
        }
        return new String(result);
    }

    public static int countLine(TextView tv, final String text,
                                final int deviceWidth) {
        final String check = text.replace(" ", "_");
        float textWidth = tv.getPaint().measureText(check);

        int line = (int) (textWidth / deviceWidth);

        return line + 1;
    }


    public static void setUnderLine(TextView mTextView) {

        String mystring = mTextView.getText().toString();
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        mTextView.setText(content);
    }

    public static void setUnderLine(TextView mTextView, String text) {

        String mystring = text;
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        mTextView.setText(content);
    }

    public static boolean isVersionAndroidOver10() {
        return Build.VERSION.SDK_INT > 10;
    }

    /**
     * Returns the consumer friendly device name
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }

        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    public static int GetDipsFromPixel(Context context, float pixels) {
        // Get the screen's density scale
        final float scale = context.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public static void setListViewHeight(ExpandableListView listView,
                                         int group) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    public static String getCurrentLanguage(Context mContext) {
        String language = "";
        Locale current = null;
        if (mContext.getResources().getConfiguration().locale != null) {
            current = mContext.getResources().getConfiguration().locale;
        }
        android.util.Log.i("ngon ngu", "getCurrentLanguage: " + current);
        if (current != null && !current.getLanguage().equals("vi")) {
            language = "_EN";
        } else {
            language = "";
        }
        return language;
    }

}
