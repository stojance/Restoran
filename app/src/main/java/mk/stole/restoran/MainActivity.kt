package mk.stole.restoran

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.*
import mk.stole.restoran.data.*
import mk.stole.restoran.data.repo.RestoranRepo
import mk.stole.restoran.data.to.Korisnik
import mk.stole.restoran.data.to.NarackaGlava
import mk.stole.restoran.data.to.NovaObr
import mk.stole.restoran.data.webclients.UrlHelper
import mk.stole.restoran.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private val repo: RestoranRepo = RestoranRepo()
    private var narackiLista: Array<NarackaGlava>? = null
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
            }
            if(App.korisnik.korisnik.isNotBlank()) {
                fetchNaracki()
            }else{
                binding.layoutHeader.visibility = View.GONE
                binding.layoutLogin.visibility = View.VISIBLE
                binding.editSifraKorisnik.requestFocus()
            }
        }

    lateinit var batteryLevelReceiver: BroadcastReceiver
    private val networkMonitor = NetworkMonitorUtil(this)
    lateinit var downloadController: DownloadController

    companion object {
        const val PERMISSION_REQUEST_STORAGE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        Log.i(TAG,"onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)

        // get the package info instance
        val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        // get this app version name programmatically
        val versionName: String = packageInfo.versionName
        this.title = this.title.toString() + " " + versionName

        batteryLevelReceiver = registerBatteryLevelBroadcastReceiver()
        downloadController = DownloadController(this, UrlHelper().downloadUrl)

        binding.btnLogin.setOnClickListener {
            if (binding.editSifraKorisnik.text.isNotBlank()) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.editSifraKorisnik.windowToken, 0)
                binding.editSifraKorisnik.clearFocus()
                loginKorisnik(binding.editSifraKorisnik.text.toString())
            }
        }

        binding.txtHeader.text = SettingsHelper.nowDate

        binding.layoutHeader.setOnClickListener {
            if (App.korisnik.korisnik.isNotBlank()) {
                binding.layoutLogin.visibility = View.GONE
                binding.layoutHeader.visibility = View.VISIBLE
                binding.gridNaracki.adapter = null

                fetchNaracki()
            } else {
                binding.layoutHeader.visibility = View.GONE
                binding.layoutLogin.visibility = View.VISIBLE
            }
        }

        binding.editSifraKorisnik.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.editSifraKorisnik.text.isEmpty()) {
                    return@OnEditorActionListener false
                }
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.editSifraKorisnik.windowToken, 0)
                binding.editSifraKorisnik.clearFocus()

                loginKorisnik(binding.editSifraKorisnik.text.toString())
            }
            false
        })

        binding.editSifraKorisnik.setSelectAllOnFocus(true);

        networkMonitor.result = { isAvailable, type ->
            runOnUiThread {
                when (isAvailable) {
                    true -> {
                        when (type) {
                            ConnectionType.Wifi -> {
                                Log.i("NETWORK_MONITOR_STATUS", "Wifi Connection")
                                /*val toast = Toast.makeText(this@MainActivity, "Има Wifi МРЕЖА", Toast.LENGTH_SHORT)
                                toast.setGravity(Gravity.CENTER, 0, 0)
                                toast.show()*/
                            }
                            ConnectionType.Cellular -> {
                                Log.i("NETWORK_MONITOR_STATUS", "Cellular Connection")
                                val toast = Toast.makeText(this@MainActivity, "!!! НЕМА Wifi МРЕЖА !!!", Toast.LENGTH_SHORT)
                                toast.setGravity(Gravity.CENTER, 0, 0)
                                toast.show()
                            }
                            else -> { }
                        }
                    }
                    false -> {
                        Log.i("NETWORK_MONITOR_STATUS", "No Connection")
                        val toast = Toast.makeText(this@MainActivity, "!!! НЕМА МРЕЖА !!!", Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }
                }
            }
        }
        networkMonitor.register()

        if (App.korisnik.korisnik.isNotBlank()) {
            binding.layoutLogin.visibility = View.GONE
            binding.layoutHeader.visibility = View.VISIBLE
            binding.gridNaracki.adapter = null
            fetchNaracki()
        } else {
            binding.layoutHeader.visibility = View.GONE
            binding.layoutLogin.visibility = View.VISIBLE
            binding.editSifraKorisnik.requestFocus()
        }
    }

    override fun onResume() {
        super.onResume()

        if (App.korisnik.korisnik.isNotBlank()) {
            binding.layoutLogin.visibility = View.GONE
            binding.layoutHeader.visibility = View.VISIBLE
            binding.gridNaracki.adapter = null
            fetchNaracki()
        } else {
            binding.layoutHeader.visibility = View.GONE
            binding.layoutLogin.visibility = View.VISIBLE
            binding.editSifraKorisnik.requestFocus()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            val intent = Intent(this@MainActivity, MySettingsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            resultLauncher.launch(intent)
            return true
        }

        if(item.itemId == R.id.mnu_getNewApp){
            try {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage("Преземање на нова верзија.\nСигурно?")
                    .setCancelable(false)
                    .setPositiveButton("Да") { dialog, id ->
                        checkStoragePermission()
                    }
                    .setNegativeButton("Не") { dialog, id ->
                        // Dismiss the dialog
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            } catch (e: java.lang.Exception) {
                // TODO Auto-generated catch block
                Log.e("MainActivity", e.message, e)
                e.printStackTrace()
            }
            return true
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            // Request for camera permission.
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // start downloading
                downloadController.enqueueDownload()
            } else {
                // Permission request was denied.
                binding.layoutMain.showSnackbar(R.string.storage_permission_denied, Snackbar.LENGTH_SHORT)
            }
        }
    }

    private fun checkStoragePermission() {
        // Check if the storage permission has been granted
        if (checkSelfPermissionCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // start downloading
            downloadController.enqueueDownload()
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission() {

        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            binding.layoutMain.showSnackbar(
                R.string.storage_access_required,
                Snackbar.LENGTH_INDEFINITE, R.string.ok
            ) {
                requestPermissionsCompat(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_STORAGE
                )
            }

        } else {
            requestPermissionsCompat(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE
            )
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG,"onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.i(TAG,"onRestoreInstanceState")
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert).setTitle("Излез")
            .setMessage("Дали сте сигурни ?")
            .setPositiveButton(
                "Да"
            ) { dialog, id -> finish() }.setNegativeButton("Не", null).show()
    }

    private fun loginKorisnik(sifra: String) {
        showProgress(true)
        binding.btnLogin.isEnabled = false
        launch(Dispatchers.IO) {
            val korisnik: Korisnik = repo.getKorisnik(sifra)
            withContext(Dispatchers.Main) {
                if (korisnik.korisnik.isNotBlank()) {
                    App.korisnik = korisnik
                    binding.layoutLogin.visibility = View.GONE
                    binding.layoutHeader.visibility = View.VISIBLE
                    binding.txtHeader.text = "   ${SettingsHelper.nowDate} ${korisnik.imePrezime}"
                    fetchNaracki()
                } else {
                    showProgress(false)
                    App.korisnik = Korisnik("", "", false, false, "")
                    binding.layoutLogin.visibility = View.VISIBLE
                    binding.layoutHeader.visibility = View.GONE
                    binding.txtHeader.text = ""
                    Toast.makeText(this@MainActivity, "Проблем со најава !!!", Toast.LENGTH_LONG)
                        .show()
                    binding.editSifraKorisnik.requestFocus()
                    binding.editSifraKorisnik.selectAll()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.editSifraKorisnik, 0)
                }
                showProgress(false)
                binding.btnLogin.isEnabled = true
            }
        }
    }

    private fun fetchNaracki() {
        //Toast.makeText(this, "...", Toast.LENGTH_SHORT).show()
        val context = this
        narackiLista = null
        showProgress(true)
        launch(Dispatchers.IO) {
            narackiLista = if(SettingsHelper.samoLicniNaracki){
                repo.getNarackiNaDatumNow(App.korisnik.korisnik)
            }else{
                repo.getNarackiNaDatumNow()
            }

            if (narackiLista != null) {
                withContext(Dispatchers.Main) {
                    binding.gridNaracki.adapter = NarackiListViewAdapter(context, narackiLista!!)
                    binding.gridNaracki.onItemClickListener =
                        AdapterView.OnItemClickListener { parent, view, position, id ->
                            // Get the GridView selected/clicked item text
                            val selectedItem = parent.getItemAtPosition(position) as NarackaGlava
                            otvoriNaracka(selectedItem)
                        }
                    showProgress(false)
                }
            }
        }
    }

    private fun otvoriNaracka(naracka: NarackaGlava) {
        Toast.makeText(
            this@MainActivity,
            naracka.naziv,
            Toast.LENGTH_LONG
        ).show()

        if (naracka.narackaBroj == 0) {
            showDialogZaMasaBroj()
        } else {
            //startActivity(Intent(this,TabsActivity::class.java))
            val intent = Intent(this, TabsActivity::class.java)
            intent.putExtra("narackaGlava", Gson().toJson(naracka))
            resultLauncher.launch(intent)
        }
    }

    private fun showDialogZaMasaBroj() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Број на маса:")
        val input = EditText(this)
        input.setHint("Внеси број на маса ...")
        input.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.requestFocus()
        builder.setView(input)

        builder.setPositiveButton("Потврди", DialogInterface.OnClickListener { dialog, which ->
            // Here you get get input text from the Edittext
            val strMasa = input.text.toString()
            if (strMasa.isEmpty().not()) {
                kreirajNovaNaracka(strMasa.toInt())
            } else {
                fetchNaracki()
            }
        })
        builder.setNegativeButton(
            "Откажи",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        val d = builder.create()
        d.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        d.show()
        //builder.show()
    }

    private fun kreirajNovaNaracka(masaBroj: Int) {
        showProgress(true)
        val nova = NovaObr(masaBroj)
        launch(Dispatchers.IO) {
            val rez = repo.createNova(nova)
            withContext(Dispatchers.Main) {
                /*Toast.makeText(
                    this@MainActivity,
                    rez.toString() ?: "?",
                    Toast.LENGTH_LONG
                ).show()*/
                if(rez.narackaBroj==0){
                    fetchNaracki()
                }else{
                    otvoriNaracka(rez)
                }

                showProgress(false)
            }
        }
    }

    private fun showProgress(show: Boolean) {
        binding.progressBarMain.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun registerBatteryLevelBroadcastReceiver(): BroadcastReceiver {
        val batteryLevelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                //context.unregisterReceiver(this)
                val rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                var level = -1
                if (rawLevel >= 0 && scale > 0) {
                    level = rawLevel * 100 / scale
                }
                if (level < 40) {
                    val batteryLevel = "БАТЕРИЈАТА Е НА $level % !!!"
                    val toast = Toast.makeText(this@MainActivity, batteryLevel, Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
            }
        }
        val batteryLevelFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryLevelReceiver, batteryLevelFilter)
        return batteryLevelReceiver
    }



    override fun onDestroy() {
        super.onDestroy()
        cancel()
        try{
            unregisterReceiver(batteryLevelReceiver)
            networkMonitor.unregister()
        }catch (e: Exception){

        }
    }

}