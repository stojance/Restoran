package mk.stole.restoran.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import mk.stole.restoran.App
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by s.nikolov on 30/07/2015.
 */
object SettingsHelper {

    private val TAG = SettingsHelper::class.java.simpleName
    var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var dateFormat8 = SimpleDateFormat("yyyyMMdd")
    var dateFormat10 = SimpleDateFormat("dd/MM/yyyy")
    var vD = 0
    var magacin_id = 0
    var uredID = 0
    var meniRootID = 0
    var host_ip: String = ""
    var webAppName: String = ""
    var samoLicniNaracki = false
    var pref: SharedPreferences? = null
        get() = App.mainPreferences
    var cal = Calendar.getInstance()

    fun toDate(strDatum: String): Date? {
        var d: Date? = null
        try {
            when (strDatum.length) {
                8 -> d = dateFormat8.parse(strDatum)
                10 -> {
                    val arr = strDatum.split("/").toTypedArray()
                    val year = arr[2].toInt()
                    val month = arr[1].toInt()
                    val day = arr[0].toInt()
                    val cal = Calendar.getInstance()
                    cal[year, month - 1] = day
                    d = cal.time
                }
                else -> d = dateFormat.parse(strDatum)
            }
            //d= (strDatum.length()==8?dateFormat8.parse(strDatum):dateFormat.parse(strDatum));
        } catch (e: ParseException) {
            Log.e(TAG, e.message, e)
            e.printStackTrace()
        }
        return d
    }

    fun toDateString(datum: Date?): String {
        return dateFormat.format(datum)
    }

    fun toDateString8(datum: Date?): String {
        cal.time = datum
        return dateFormat8.format(cal.time)
    }

    fun toDateString10(datum: Date?): String {
        cal.time = datum
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]
        return StringBuilder() // Month is 0 based, so you have to add 1
            .append(pad(day)).append("/")
            .append(pad(month + 1)).append("/")
            .append(year).toString()
    }

    fun toDateString8to10(datum: String): String {
        return datum.substring(6, 8) + "/" + datum.substring(4, 6) + "/" + datum.substring(0, 4)
    }

    val nowDate: String
        get() {
            val nowDate: Date
            val cal = Calendar.getInstance()
            val timeNow = cal[Calendar.HOUR_OF_DAY]
            nowDate = if (timeNow > CAS_RABOTA) {
                cal.time
            } else {
                cal.add(Calendar.DATE, -1)
                cal.time
            }
            return dateFormat10.format(nowDate)
        }
    val nowDate8: String
        get() {
            val nowDate: Date
            val cal = Calendar.getInstance()
            val timeNow = cal[Calendar.HOUR_OF_DAY]
            nowDate = if (timeNow > CAS_RABOTA) {
                cal.time
            } else {
                cal.add(Calendar.DATE, -1)
                cal.time
            }
            return dateFormat8.format(nowDate)
        }
    val context: Context
        get() = App.applicationContext

    fun pad(i: Int): String {
        return if (i < 10) {
            "0$i"
        } else i.toString()
    }

    const val CAS_RABOTA = 4

    init {
        pref = App.mainPreferences
        vD = 40
        meniRootID = pref!!.getString("meni_root_id", "0")!!.toInt()
        magacin_id = pref!!.getString("magacin_id", "0")!!.toInt()
        uredID = pref!!.getString("ured_id", "0")!!.toInt()
        host_ip = pref!!.getString("host_ip", "")!!
        webAppName = pref!!.getString("web_app_name", "")!!
        samoLicniNaracki = pref!!.getBoolean("samo_licni_naracki", false)
    }
}