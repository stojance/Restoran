package mk.stole.restoran.data.to

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by s.nikolov on 06/08/2015.
 */
data class NarackaGlava(
    @SerializedName(value = "narackaBroj", alternate = arrayOf("NarackaBroj"))
    var narackaBroj: Int = 0,
    @SerializedName(value = "masaBroj", alternate = arrayOf("MasaBroj"))
    var masaBroj: Int = 0,
    @SerializedName(value = "naziv", alternate = arrayOf("Naziv"))
    var naziv: String = "НОВА",
    @SerializedName(value = "naracal", alternate = arrayOf("Naracal"))
    var naracal: String = "",
    @SerializedName(value = "naplatil", alternate = arrayOf("Naplatil"))
    var naplatil: String = "",
    @SerializedName(value = "zabeleska", alternate = arrayOf("Zabeleska"))
    var zabeleska: String = "",
    @SerializedName(value = "iznos", alternate = arrayOf("Iznos"))
    var iznos: Double = 0.0
) : Serializable