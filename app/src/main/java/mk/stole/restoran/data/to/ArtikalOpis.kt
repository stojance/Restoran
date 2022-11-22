package mk.stole.restoran.data.to

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ArtikalOpis(var artikal_ID: Int,
                  @SerializedName(value = "Opis", alternate = arrayOf("opis"))
                  var opis: String)
    : Serializable {

    override fun toString(): String {
        return opis
    }
}
