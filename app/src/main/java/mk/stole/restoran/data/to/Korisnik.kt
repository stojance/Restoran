package mk.stole.restoran.data.to

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by s.nikolov on 04/08/2015.
 */
data class Korisnik(
    @SerializedName(value = "korisnik", alternate = arrayOf("Korisnik"))
    var korisnik: String = "",
    @SerializedName(value = "ImePrezime", alternate = arrayOf("imePrezime"))
    var imePrezime: String = "",
    @SerializedName(value = "Sef", alternate = arrayOf("sef"))
    var sef: Boolean = false,
    @SerializedName(value = "Administracija", alternate = arrayOf("administracija"))
    var administracija: Boolean = false,
    @SerializedName(value = "Sifra", alternate = arrayOf("sifra"))
    var sifra: String = ""
) : Serializable