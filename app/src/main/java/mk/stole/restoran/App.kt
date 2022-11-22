package mk.stole.restoran

/**
 * Created by s.nikolov on 30/07/2015.
 */
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import mk.stole.restoran.data.to.Korisnik

class App : Application() {

    init {
        instance = this
    }

    companion object {
        lateinit var instance: App
            private set

        val applicationContext : Context
            get() = instance.applicationContext

        var korisnik: Korisnik = Korisnik()
        var appFileName = "restoran2.apk"

        val mainPreferences: SharedPreferences
            get() = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }
}