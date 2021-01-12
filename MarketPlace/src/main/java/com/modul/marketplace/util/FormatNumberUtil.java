package com.modul.marketplace.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class FormatNumberUtil {
    private static final String TAG = FormatNumberUtil.class.getSimpleName();
    private static NumberFormat mFormatNumberAndCurrency;
    private static DecimalFormat mDecimalNumberFormat;

    public static void initInStance() {

        mFormatNumberAndCurrency = NumberFormat.getCurrencyInstance(getLocale());
        NumberFormat mNumberFormat = NumberFormat.getNumberInstance(getLocale());
        mDecimalNumberFormat = (DecimalFormat) mNumberFormat;
        mDecimalNumberFormat.applyPattern("#,###.##");

        Log.d(TAG, "DECIMAL " + mFormatNumberAndCurrency.getMaximumFractionDigits());

    }

    public static String getSymbolCurrentcy() {
        //	Log.d(TAG,"MAX "+mFormatNumberAndCurrency.getRoundingMode()+"/ MIN "+mFormatNumberAndCurrency.getCurrency());
        return mFormatNumberAndCurrency.getCurrency().getSymbol();
    }

    public static String getCurrencyCode() {
        //	Log.d(TAG,"MAX "+mFormatNumberAndCurrency.getRoundingMode()+"/ MIN "+mFormatNumberAndCurrency.getCurrency());
        return mFormatNumberAndCurrency.getCurrency().getCurrencyCode();
    }

    public static Currency getmFormatNumberAndCurrency() {
        return mFormatNumberAndCurrency.getCurrency();
    }

    public static NumberFormat getNumberFormat() {
        return mFormatNumberAndCurrency;
    }

    public static Locale getLocale() {
//		String locale = ApplicationIpos.getInstance().get().getLocaleid();
//		Log.d(TAG, "getLocale " + locale);
//		if (TextUtils.isEmpty(locale)) {
//			locale = "vi_VN";
//		}
//
//		String[] arr = locale.split("_");
//		try {
//
//			return new Locale(arr[0], arr[1]);
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.d(TAG, "LOCALE ERR" + e.getMessage());
//		}
        return new Locale("vi", "VN");
    }

    public static String format(double value) {
        return mFormatNumberAndCurrency.format(value);
    }

    public static String getDecimalSeparator() {
        return "" + ((DecimalFormat) mFormatNumberAndCurrency).getDecimalFormatSymbols().getDecimalSeparator();
    }

    public static String formatCurrency(Double value) {
        //	return mDecimalNumberFormat.format(value)+" ₫";
        return mFormatNumberAndCurrency.format(value);
    }

    public static String getStringFromDecimalNumber(double value) {
        String text = mDecimalNumberFormat.format(value);
        Log.d(TAG, " getStringFromDecimalNumber " + value + " = " + text);

        return text;
    }

    public static double getDecimalNumberFromString(String str) {
        double value = 0.0;
        try {

            String v = str.replace(String.valueOf(mDecimalNumberFormat.getDecimalFormatSymbols().getGroupingSeparator()), "");
            value = mDecimalNumberFormat.parse(v).doubleValue();


        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getDecimalNumberFromString " + value + " = PASER " + str);
        return value;
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "K");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String formatTwoDigit(int number) {

        return String.format("%2d", number);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);


        return (double) tmp / factor;
    }

    public static double round(double value) {
        int places = mFormatNumberAndCurrency.getMaximumFractionDigits();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        //Log.d(TAG,"VALUE "+value+"/ "+places);
        return (double) tmp / factor;
    }

    public static boolean isHaveDot() {
        return mFormatNumberAndCurrency.getMaximumFractionDigits() > 0;
    }

    public static String fmt(double d) {
        if (d == (long) d)
            return String.format("%d", (long) d);
        else
            return String.format("%s", d);
    }
}
