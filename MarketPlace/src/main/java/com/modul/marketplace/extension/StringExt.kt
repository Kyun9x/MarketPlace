package com.modul.marketplace.extension

import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.modul.marketplace.app.ApplicationMarketPlace
import com.modul.marketplace.util.FormatNumberUtil
import com.modul.marketplace.util.Utilities
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter
import java.text.DecimalFormat
import java.text.Normalizer
import java.text.NumberFormat
import java.util.regex.Pattern

object StringExt {

    private var mDecimalNormal: DecimalFormat? = null

    fun checkTextEmpty(activity: AppCompatActivity, text: String, textError: String): Boolean {
        if (TextUtils.isEmpty(text)) {
            activity.showToast(textError)
            return false
        }
        return true
    }

    fun checkTextLength(
            activity: AppCompatActivity,
            view: EditText,
            length: Int,
            textError: String
    ): Boolean {
        if (view.text?.toString()?.length!! < length) {
            activity.showToast(textError)
            return false
        }
        return true
    }

    fun checkEmailValid(activity: AppCompatActivity, view: EditText, textError: String): Boolean {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(view.text.toString()).matches()) {
            activity.showToast(textError)
            return false
        }
        return true
    }

    fun checkTextCompare(
            activity: AppCompatActivity,
            view1: EditText,
            view2: EditText,
            textError: String
    ): Boolean {
        if (view1.text.toString().trim() != view2.text.toString().trim()) {
            activity.showToast(textError)
            return false
        }
        return true
    }

    fun isTextEmpty(text: Any?): String {
        text?.run {
            return toString()
        } ?: run {
            return ""
        }
    }

    fun isTrimText(text: String): String {
        return text.trim()
    }

    fun isCompareEmail(text: String, message: String): Boolean {
        if (!emailValid(text.trim())) {
            Toast.makeText(ApplicationMarketPlace.instance, message, Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }

    fun emailValid(text: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }


    fun isTextEmptyOrNull(text: String?): Boolean {
        return text == null || text.isEmpty()
    }

    // detect special character
    fun hasSpecialChars(text: String?): Boolean {
        val regex =
                Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]")
        return regex.matcher(text).find()
    }

    fun convertToMoney(value: Double?): String {
        if (value != null) {
            val mNumberFormat = NumberFormat.getNumberInstance(FormatNumberUtil.getLocale())
            mDecimalNormal = mNumberFormat as DecimalFormat?
            if (1000000000 < value) {
                mDecimalNormal?.applyPattern("###.00")
                return mDecimalNormal?.format(value / 1000000000) + " tỉ"
            } else {
                mDecimalNormal?.applyPattern("#,###.###")
                return mDecimalNormal?.format(value.toLong() / 100 * 100) + " đ"
            }
//            if(value.toString().length == 10 ){
//                return mDecimalNormal?.format(value)?.substring(0,3) + " tỷ"
//            }else if(value.toString().length == 11 ){
//                return mDecimalNormal?.format(value)?.substring(0,4) + " tỷ"
//            }else if(value.toString().length == 12 ){
//                return mDecimalNormal?.format(value)?.substring(0,5) + " tỷ"
//            }else if(value.toString().length == 13 ){
//                return mDecimalNormal?.format(value)?.substring(0,5) + " tỷ"
//            }else{
//                return mDecimalNormal?.format(value) + " đ"
//            }
        } else {
            return "0 đ"
        }
    }


    fun extractInt(s: String): Int {
        val num = s.replace("\\D".toRegex(), "")
        // return 0 if no digits found
        return if (num.isEmpty()) 0 else Integer.parseInt(num)
    }

    fun convertNumberToString(value: Any?): String {
        value?.run {
            return toString().replace(".0", "")
        } ?: run {
            return ""
        }
    }

    fun convertNumberMonthToChar(value: Any): String {
        var convert = ""
        Timber.e("value: " + value.toString())
        convert = when (value.toString()) {
            "1.0" -> return "a"
            "2.0" -> return "b"
            "3.0" -> return "c"
            "4.0" -> return "d"
            "5.0" -> return "e"
            "6.0" -> return "f"
            "7.0" -> return "g"
            "8.0" -> return "h"
            "9.0" -> return "i"
            "10.0" -> return "k"
            "11.0" -> return "l"
            "12.0" -> return "m"
            else -> ""
        }
        return convert
    }

    fun convertCharToMonth(value: Any): String {
        var convert = ""
        convert = when (value.toString()) {
            "a" -> return "Tháng 1"
            "b" -> return "Tháng 2"
            "c" -> return "Tháng 3"
            "d" -> return "Tháng 4"
            "e" -> return "Tháng 5"
            "f" -> return "Tháng 6"
            "g" -> return "Tháng 7"
            "h" -> return "Tháng 8"
            "i" -> return "Tháng 9"
            "k" -> return "Tháng 10"
            "l" -> return "Tháng 11"
            "m" -> return "Tháng 12"
            else -> ""
        }
        return convert
    }

    fun removeAccent(s: String): String {
        val sb = StringBuilder()
        for (c in s.split("(?<=\\G.)").toTypedArray()) {
            if ("đ".contains(c)) {
                sb.append("d")
            } else {
                sb.append(c)
            }
        }
        val temp = Normalizer.normalize(sb, Normalizer.Form.NFD)
        val pattern =
                Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(temp).replaceAll("")
    }

    fun contains(strS1: String?, strS2: String?): Boolean {
        val s1 = Utilities.removeAccents(strS1)
        val s2 = Utilities.removeAccents(strS2)
        return s2.contains(s1)
    }

    fun convertVietnameseToASCII(data: String): String {
        if (!TextUtils.isEmpty(data)) {
            val strALower = "áàạãảăắằẵặẳâầấẩậẫ"
            val strAUpper = "ÁÀẠÃẢĂẮẰẴẶẲÂẦẤẨẬẪ"
            val strDLower = "đ"
            val strDUpper = "Đ"
            val strELower = "éẹèẽẻêếềệễể"
            val strEUpper = "ÉẸÈẼẺÊẾỀỆỄỂ"
            val strILower = "íìịĩỉ"
            val strIUpper = "ÍÌỊĨỈ"
            val strOLower = "óòọõỏôốồộỗổơớờởợỡ"
            val strOUpper = "ÓÒỌÕỎÔỐỒỘỖỔƠỚỜỞỢỠ"
            val strULower = "úùụũủưứừựữử"
            val strUUpper = "ÚÙỤŨỦƯỨỪỰỮỬ"
            val strYLower = "ýỷỳỹỵ"
            val strYUpper = "ÝỶỲỸỴ"
            for (i in 0 until data.length) {
                val letter = data[i]
                if (letter.toString().contains(strALower)) {
                    data.replace("" + letter, "a")
                } else if (letter.toString().contains(strAUpper)) {
                    data.replace("" + letter, "A")
                } else if (letter.toString().contains(strDLower)) {
                    data.replace("" + letter, "d")
                } else if (letter.toString().contains(strDUpper)) {
                    data.replace("" + letter, "D")
                } else if (letter.toString().contains(strELower)) {
                    data.replace("" + letter, "e")
                } else if (letter.toString().contains(strEUpper)) {
                    data.replace("" + letter, "E")
                } else if (letter.toString().contains(strILower)) {
                    data.replace("" + letter, "i")
                } else if (letter.toString().contains(strIUpper)) {
                    data.replace("" + letter, "I")
                } else if (letter.toString().contains(strOLower)) {
                    data.replace("" + letter, "o")
                } else if (letter.toString().contains(strOUpper)) {
                    data.replace("" + letter, "O")
                } else if (letter.toString().contains(strULower)) {
                    data.replace("" + letter, "u")
                } else if (letter.toString().contains(strUUpper)) {
                    data.replace("" + letter, "U")
                } else if (letter.toString().contains(strYLower)) {
                    data.replace("" + letter, "y")
                } else if (letter.toString().contains(strYUpper)) {
                    data.replace("" + letter, "Y")
                } else {
                    data.replace("" + letter, "" + letter)
                }
            }
        }
        return data
    }

    // Format price
    fun formatPrice(priceStr: String): String {
        val result = StringBuilder()
        val value: String = priceStr.toDouble().toString()
        val valueLength = value.length
        if (valueLength <= 3) {
            result.append(value)
        } else {
            val limit: Int
            limit = if (valueLength % 3 == 0) {
                valueLength / 3 - 1
            } else {
                valueLength / 3
            }
            for (i in 0..limit) {
                if (i == 0) {
                    if (valueLength % 3 == 0) {
                        result.append(value.substring(0, 3)).append(".")
                    } else {
                        result.append(value.substring(0, valueLength % 3)).append(".")
                    }
                } else {
                    if (i == limit) {
                        result.append(value.substring(result.length - i, result.length - i + 3))
                    } else {
                        result.append(value.substring(result.length - i, result.length - i + 3))
                                .append(".")
                    }
                }
            }
        }
        return result.toString()
    }

    fun getNumberText(number: Int): String {
        return if (number >= 0 && number <= 9) {
            "0$number"
        } else {
            "" + number
        }
    }

    fun isEqualString(from: String?, to: String?): Boolean {
        if (from == null && to == null) {
            return true
        }
        return if (from != null && to != null) {
            from.trim { it <= ' ' } == to.trim { it <= ' ' }
        } else false
    }

    fun getStackTrace(throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw, true)
        throwable.printStackTrace(pw)
        return sw.buffer.toString()
    }

    fun convertJsonToArray(data: String): ArrayList<String> {
        val arrayTutorialType = object : TypeToken<ArrayList<String>>() {}.type
        var tutorials: ArrayList<String> = ArrayList()

        try {
            data.run {
                tutorials = Gson().fromJson(toString(), arrayTutorialType)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tutorials
    }


}