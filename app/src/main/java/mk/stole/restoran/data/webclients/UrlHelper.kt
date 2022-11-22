package mk.stole.restoran.data.webclients

import mk.stole.restoran.App
import mk.stole.restoran.data.SettingsHelper
import java.lang.String

/**
 * Created by s.nikolov on 30/07/2015.
 */
class UrlHelper {
    private val hostIP: kotlin.String?
    var apiRoot: kotlin.String? = null
    val meniUrl: kotlin.String
    val korinikUrl: kotlin.String
    val artikalOpisUrl: kotlin.String
    val MATICNI_PART = "maticni"
    val MENI_PART = "meni/getfor"
    val NARACKI_PART = "naracki"
    val KORISNIK_PART = "getkorisnik"
    val ARTIKAL_OPIS_PART = "getartikalopis"

    fun getNarackiNaDatumUrl(
        odDatum: kotlin.String?,
        doDatum: kotlin.String?,
        naracal: kotlin.String?,
        naplateni: Int
    ): kotlin.String {
        return String.format(
            "%s/%s/nadatum/%s?oddatum=%s&dodatum=%s&naracal=%s&naplateni=%s",
            apiRoot,
            NARACKI_PART,
            SettingsHelper.magacin_id,
            odDatum,
            doDatum,
            naracal,
            naplateni
        )
    }

    fun getNarackaStavkiUrl(narackaBroj: Int): kotlin.String {
        return String.format(
            "%s/%s/get/%s?narackaBroj=%s",
            apiRoot,
            NARACKI_PART,
            SettingsHelper.magacin_id,
            narackaBroj
        )
    }

    val narackaStavkaObrUrl: kotlin.String
        get() = String.format("%s/%s/Save", apiRoot, NARACKI_PART)
    val narackaStavkaSoNaracalObrUrl: kotlin.String
        get() = String.format("%s/%s/SaveSoNaracal", apiRoot, NARACKI_PART)
    val narackaNovaUrl: kotlin.String
        get() = String.format("%s/%s/nova", apiRoot, NARACKI_PART)
    val narackaPecatiUrl: kotlin.String
        get() = String.format("%s/%s/pecati", apiRoot, NARACKI_PART)

    val downloadUrl2: kotlin.String
        get() {
            var url = ""
            url = if (SettingsHelper.webAppName == "") {
                String.format("http://%s/Home/GetFileFromDisk", hostIP)
            } else {
                String.format(
                    "http://%s/%s/Home/GetFileFromDisk",
                    hostIP,
                    SettingsHelper.webAppName
                )
            }
            return url
        }
    val downloadUrl: kotlin.String
        get() {
            var url = ""
            url = if (SettingsHelper.webAppName == "") {
                String.format("http://%s/wwwroot/content/app/%s", hostIP,App.appFileName )
            } else {
                String.format(
                    "http://%s/%s/wwwroot/content/app/%S",
                    hostIP,
                    SettingsHelper.webAppName,
                    App.appFileName
                )
            }
            return url
        }

    init {
        hostIP = SettingsHelper.host_ip //"192.168.222.43";
        apiRoot = if (SettingsHelper.webAppName == "") {
            String.format("http://%s/api", hostIP)
        } else {
            String.format("http://%s/%s/api", hostIP, SettingsHelper.webAppName)
        }
        meniUrl = String.format("%s/%s", apiRoot, MENI_PART)
        korinikUrl = String.format("%s/%s/%s", apiRoot, MATICNI_PART, KORISNIK_PART)
        artikalOpisUrl = String.format("%s/%s/%s", apiRoot, MATICNI_PART, ARTIKAL_OPIS_PART)
    }
}