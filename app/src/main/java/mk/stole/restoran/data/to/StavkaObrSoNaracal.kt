package mk.stole.restoran.data.to

import org.json.JSONObject
import org.json.JSONException
import java.io.Serializable

/**
 * Created by s.nikolov on 10/08/2015.
 */
class StavkaObrSoNaracal(
    var redID: Long,
    var magacin_ID: Int,
    var narackaBroj: Int,
    var naracal: String,
    var artikal_ID: Int,
    var kolicina: Double,
    var text: String
) : Serializable {
    val jSONObject: JSONObject
        get() {
            val obj = JSONObject()
            try {
                obj.put("RedID", redID)
                obj.put("Magacin_ID", magacin_ID)
                obj.put("NarackaBroj", narackaBroj)
                obj.put("Naracal", naracal)
                obj.put("Artikal_ID", artikal_ID)
                obj.put("Kolicina", kolicina)
                obj.put("Text", text)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return obj
        }
}