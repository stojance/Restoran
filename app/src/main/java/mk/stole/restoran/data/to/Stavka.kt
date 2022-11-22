package mk.stole.restoran.data.to

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by s.nikolov on 06/08/2015.
 */
class Stavka(
    @SerializedName(value = "RedID", alternate = arrayOf("redID"))
    var redID: Long=0,
    @SerializedName(value = "Magacin_ID", alternate = arrayOf("magacin_ID"))
    var magacin_ID: Int=0,
    @SerializedName(value = "NarackaBroj", alternate = arrayOf("narackaBroj"))
    var narackaBroj: Int=0,
    @SerializedName(value = "Datum", alternate = arrayOf("datum"))
    var datum: String="",
    @SerializedName(value = "MasaBroj", alternate = arrayOf("masaBroj"))
    var masaBroj: Int=0,
    @SerializedName(value = "Grupa", alternate = arrayOf("grupa"))
    var grupa: String="",
    @SerializedName(value = "Naracal", alternate = arrayOf("naracal"))
    var naracal: String="",
    @SerializedName(value = "Naplatil", alternate = arrayOf("naplatil"))
    var naplatil: String="",
    @SerializedName(value = "Artikal_ID", alternate = arrayOf("artikal_ID"))
    var artikal_ID: Int=0,
    @SerializedName(value = "Kolicina", alternate = arrayOf("kolicina"))
    var kolicina: Double=0.0,
    @SerializedName(value = "RabatProcent", alternate = arrayOf("rabatProcent"))
    var rabatProcent: Double=0.0,
    @SerializedName(value = "Cena", alternate = arrayOf("cena"))
    var cena: Double=0.0,
    @SerializedName(value = "DanokProcent", alternate = arrayOf("danokProcent"))
    var danokProcent: Double=0.0,
    @SerializedName(value = "Pecateno", alternate = arrayOf("pecateno"))
    var pecateno: Long=0,
    @SerializedName(value = "Stavka", alternate = arrayOf("stavka"))
    var stavka: Int=0,
    @SerializedName(value = "ArtikalNaziv", alternate = arrayOf("artikalNaziv"))
    var artikalNaziv: String="",
    @SerializedName(value = "IznosPoStavka", alternate = arrayOf("iznosPoStavka"))
    var iznosPoStavka: Double=0.0,
    @SerializedName(value = "Iznos", alternate = arrayOf("iznos"))
    var iznos: Double=0.0
) : Serializable