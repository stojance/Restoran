package mk.stole.restoran.data.repo

import com.google.gson.Gson
import kotlinx.coroutines.*
import mk.stole.restoran.data.to.*
import mk.stole.restoran.data.webclients.RequestHandler
import mk.stole.restoran.data.webclients.UrlHelper
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class RestoranRepo : CoroutineScope by MainScope() {
    private val gson = Gson()
    private val urlHelper = UrlHelper()

    suspend fun getMeniFor(meni_id: Int): Array<Meni> {
        lateinit var ret: Array<Meni>
        val json = getMeniForJson(meni_id)
        withContext(Dispatchers.Main) {
            ret = gson.fromJson(json, Array<Meni>::class.java)
        }
        return ret
    }

    suspend fun getMeniForJson(meni_id: Int = 12): String {
        return withContext(Dispatchers.Default) {
            return@withContext URL("${urlHelper.meniUrl}/$meni_id").readText()
        }
    }

    suspend fun getKorisnik(sifra: String): Korisnik {
        var ret: Korisnik = Korisnik()
        val json = getKorisnikJson(sifra)
        if (json.isEmpty().not()) {
            withContext(Dispatchers.Main) {
                ret = gson.fromJson(json, Korisnik::class.java)
            }
        }
        return ret
    }

    suspend fun getKorisnikJson(sifra: String): String {
        return withContext(Dispatchers.Default) {
            try {
                return@withContext URL("${urlHelper.korinikUrl}/$sifra").readText()
            } catch (e: Exception) {
                return@withContext ""
            }
        }
    }

    suspend fun getArtikalOpis(artikal_id: Int): Array<ArtikalOpis> {
        var ret: Array<ArtikalOpis> = emptyArray()
        val json = getArtikalOpisJson(artikal_id)
        if (json.isEmpty().not()) {
            withContext(Dispatchers.Main) {
                ret = gson.fromJson(json, Array<ArtikalOpis>::class.java)
            }
        }
        return ret
    }

    suspend fun getArtikalOpisJson(artikal_id: Int): String {
        return withContext(Dispatchers.Default) {
            return@withContext URL("${urlHelper.artikalOpisUrl}/$artikal_id").readText()
        }
    }

    suspend fun getNarackiNaDatumNow(
        naracal: String = "",
        naplateni: Int = 0
    ): Array<NarackaGlava> {
        val now = SimpleDateFormat("yyyyMMdd").format(Date())
        return getNarackiNaDatum(now, now, naracal, naplateni)
    }

    suspend fun getNarackiNaDatum(
        odDatum: String,
        doDatum: String,
        naracal: String,
        naplateni: Int = 0
    ): Array<NarackaGlava> {
        var ret: Array<NarackaGlava> = emptyArray()
        val json = getNarackiNaDatumJson(odDatum, doDatum, naracal, naplateni)
        if (json.isEmpty().not()) {
            withContext(Dispatchers.Main) {
                ret = gson.fromJson(json, Array<NarackaGlava>::class.java)
            }
        }
        val marr = ret.toMutableList()
        marr.add(0, NarackaGlava())
        return marr.toTypedArray()
    }

    suspend fun getNarackiNaDatumJson(
        odDatum: String,
        doDatum: String,
        naracal: String,
        naplateni: Int = 0
    ): String {
        return withContext(Dispatchers.Default) {
            try {
                return@withContext URL(
                    "${
                        urlHelper.getNarackiNaDatumUrl(
                            odDatum,
                            doDatum,
                            naracal,
                            naplateni
                        )
                    }"
                ).readText()
            } catch (e: java.lang.Exception) {
                return@withContext ""
            }

        }
    }

    suspend fun getNarackaStavki(narackaBroj: Int): Array<Stavka> {
        var ret: Array<Stavka> = emptyArray()
        val json = getNarackaStavkiJson(narackaBroj)
        if (json.isEmpty().not()) {
            withContext(Dispatchers.Main) {
                ret = gson.fromJson(json, Array<Stavka>::class.java)
            }
        }
        return ret;
    }

    suspend fun getNarackaStavkiJson(narackaBroj: Int): String {
        return withContext(Dispatchers.Default) {
            return@withContext URL(urlHelper.getNarackaStavkiUrl(narackaBroj)).readText()
        }
    }

    suspend fun createNova(glava: NovaObr): NarackaGlava {
        var ret = NarackaGlava()
        val json = createNovaJson(glava)
        if (json.isNullOrEmpty().not()) {
            with(Dispatchers.Main) {
                ret = gson.fromJson(json, NarackaGlava::class.java)
            }
        }
        return ret
    }

    suspend fun createNovaJson(glava: NovaObr): String? {
        val serverUrl = urlHelper.narackaNovaUrl
        val postString = StringBuilder()
        postString.append(URLEncoder.encode("", "UTF-8"))
        postString.append("=")
        postString.append(URLEncoder.encode(glava.jSONObject.toString(), "UTF-8"))

        return withContext(Dispatchers.Default) {
            try {
                return@withContext RequestHandler.requestPOSTJson(
                    serverUrl,
                    postString.toString()
                )
            } catch (e: java.lang.Exception) {
                return@withContext ""
            }
        }
    }

    suspend fun saveStavkaSoNaracal(stavka: StavkaObrSoNaracal): Stavka {
        var ret = Stavka()
        val json = saveStavkaSoNaracalJson(stavka)
        if (json.isNullOrEmpty().not()) {
            with(Dispatchers.Main) {
                ret = gson.fromJson(json, Stavka::class.java)
            }
        }
        return ret
    }

    suspend fun saveStavkaSoNaracalJson(stavka: StavkaObrSoNaracal): String? {
        val serverUrl = urlHelper.narackaStavkaSoNaracalObrUrl
        val postString = StringBuilder()
        postString.append(URLEncoder.encode("", "UTF-8"))
        postString.append("=")
        postString.append(URLEncoder.encode(stavka.jSONObject.toString(), "UTF-8"))

        return withContext(Dispatchers.Default) {
            try {
                return@withContext RequestHandler.requestPOSTJson(
                    serverUrl,
                    postString.toString()
                )
            } catch (e: java.lang.Exception) {
                return@withContext ""
            }
        }
    }

    suspend fun pecati(pecati: PecatiObr): String? {
        val serverUrl = urlHelper.narackaPecatiUrl
        val postString = StringBuilder()
        postString.append(URLEncoder.encode("", "UTF-8"))
        postString.append("=")
        postString.append(URLEncoder.encode(pecati.jSONObject.toString(), "UTF-8"))

        return withContext(Dispatchers.Default) {
            try {
                return@withContext RequestHandler.requestPOSTJson(
                    serverUrl,
                    postString.toString()
                )
            } catch (e: java.lang.Exception) {
                return@withContext ""
            }
        }
    }

    fun sendPostRequestExample(userName: String, password: String) {

        var reqParam =
            URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8")
        reqParam += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(
            password,
            "UTF-8"
        )
        val mURL = URL("<Your API Link>")

        with(mURL.openConnection() as HttpURLConnection) {
            // optional default is GET
            requestMethod = "POST"

            val wr = OutputStreamWriter(getOutputStream());
            wr.write(reqParam);
            wr.flush();

            println("URL : $url")
            println("Response Code : $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                println("Response : $response")
            }
        }
    }
}