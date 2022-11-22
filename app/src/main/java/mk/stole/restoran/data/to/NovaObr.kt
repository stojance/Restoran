package mk.stole.restoran.data.to

import com.google.gson.annotations.SerializedName
import mk.stole.restoran.App
import mk.stole.restoran.data.SettingsHelper
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

/**
 * Created by s.nikolov on 11/08/2015.
 */
class NovaObr : Serializable {
    @SerializedName(value = "Magacin_ID", alternate = arrayOf("magacin_ID"))
    var magacin_ID = 0
    @SerializedName(value = "Datum", alternate = arrayOf("datum"))
    var datum: String? = null
    @SerializedName(value = "MasaBroj", alternate = arrayOf("masaBroj"))
    var masaBroj = 0
    @SerializedName(value = "Naracal", alternate = arrayOf("naracal"))
    var naracal: String? = null

    constructor(magacin_ID: Int, datum: String?, masaBroj: Int, naracal: String?) {
        this.magacin_ID = magacin_ID
        this.datum = datum ?: SettingsHelper.nowDate8
        this.masaBroj = masaBroj
        this.naracal = naracal ?: App.korisnik.korisnik
    }

    constructor(magacin_ID: Int, datum: String?, masaBroj: Int) {
        this.magacin_ID = magacin_ID
        this.datum = datum ?: SettingsHelper.nowDate8
        this.masaBroj = masaBroj
        this.naracal = App.korisnik.korisnik
    }

    constructor(masaBroj: Int) {
        this.magacin_ID = SettingsHelper.magacin_id
        this.datum = SettingsHelper.nowDate8
        this.masaBroj = masaBroj
        this.naracal = App.korisnik.korisnik
    }

    val jSONObject: JSONObject
        get() {
            val obj = JSONObject()
            try {
                obj.put("Magacin_ID", magacin_ID)
                obj.put("Datum", datum)
                obj.put("MasaBroj", masaBroj)
                obj.put("Naracal", naracal)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return obj
        }
}