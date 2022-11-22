package mk.stole.restoran.data.to

import mk.stole.restoran.App
import mk.stole.restoran.data.SettingsHelper
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by s.nikolov on 11/08/2015.
 */
class PecatiObr {
    var uredID: Int
    var report: String
    var magacin_ID: Int
    var narackaBroj: Int
    var korisnik: String

    constructor(uredID: Int, report: String, narackaBroj: Int) {
        this.uredID = uredID
        this.report = report
        this.magacin_ID = SettingsHelper.magacin_id
        this.narackaBroj = narackaBroj
        this.korisnik = App.korisnik.korisnik
    }

    constructor(report: String, narackaBroj: Int) {
        this.uredID = SettingsHelper.uredID
        this.report = report
        this.magacin_ID = SettingsHelper.magacin_id
        this.narackaBroj = narackaBroj
        this.korisnik = App.korisnik.korisnik
    }

    constructor(narackaBroj: Int) {
        this.uredID = SettingsHelper.uredID
        this.report = "Punkt"
        this.magacin_ID = SettingsHelper.magacin_id
        this.narackaBroj = narackaBroj
        this.korisnik = App.korisnik.korisnik
    }

    val jSONObject: JSONObject
        get() {
            val obj = JSONObject()
            try {
                obj.put("UredID", this.uredID)
                obj.put("Report", this.report)
                obj.put("Magacin_ID", this.magacin_ID)
                obj.put("NarackaBroj", this.narackaBroj)
                obj.put("Korisnik", this.korisnik)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return obj
        }
}