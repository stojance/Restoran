package mk.stole.restoran

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import mk.stole.restoran.data.SettingsHelper
import mk.stole.restoran.data.StavkiListViewAdapter
import mk.stole.restoran.data.repo.RestoranRepo
import mk.stole.restoran.data.to.NarackaGlava
import mk.stole.restoran.data.to.PecatiObr
import mk.stole.restoran.data.to.Stavka
import mk.stole.restoran.data.to.StavkaObrSoNaracal
import mk.stole.restoran.databinding.FragmentStavkiBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val NARACKA_GLAVA = "narackaGlava"

/**
 * A simple [Fragment] subclass.
 * Use the [StavkiFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StavkiFragment : Fragment(), CoroutineScope by MainScope() {
    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentStavkiBinding? = null
    private val binding get() = _binding!!
    private var narackaGlava: NarackaGlava? = null
    private val repo: RestoranRepo = RestoranRepo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            narackaGlava = it.getSerializable(NARACKA_GLAVA) as NarackaGlava
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStavkiBinding.inflate(inflater, container, false)
        binding.tvStavkiHeader.text = narackaGlava!!.naziv
        binding.btnPecatiPunkt.setOnClickListener {
            pecati()
        }
        /*if(savedInstanceState == null) {
            // is first time
            fetchStavki()
        }*/

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("fetchStavki", true)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState?.getBoolean("fetchStavki") == true){
            //fetchStavki()
            savedInstanceState?.putBoolean("fetchStavki",false)
        }
    }

    override fun onResume() {
        super.onResume()
        fetchStavki()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun fetchStavki() {
        val context = this.context
        showProgress(true)
        if(narackaGlava == null){
            narackaGlava = arguments?.getSerializable(NARACKA_GLAVA) as NarackaGlava
        }
        lateinit var stavki: Array<Stavka>
        launch {
            stavki = repo.getNarackaStavki(narackaGlava!!.narackaBroj)
            withContext(Dispatchers.Main) {
                showProgress(false)
                binding.tvStavkiNarackaIznos.text = ""
                if (stavki.lastIndex >= 0) {
                    binding.tvStavkiNarackaIznos.text = "${stavki[0].iznos} ден."
                }
                binding.listStavki.adapter = null
                binding.listStavki.adapter = StavkiListViewAdapter(context!!, stavki)
                binding.listStavki.refreshDrawableState()
                binding.listStavki.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        // Get the GridView selected/clicked item text
                        val selectedItem = parent.getItemAtPosition(position) as Stavka
                        if (selectedItem != null && selectedItem.redID > 0) {
                            /*Toast.makeText(
                                context,
                                Gson().toJson(selectedItem),
                                Toast.LENGTH_SHORT
                            ).show()*/
                            if (selectedItem.pecateno == 0L) {
                                showDialogZaKolicina(selectedItem)
                            }
                        }
                    }
            }
        }
    }

    private fun showDialogZaKolicina(stavka: Stavka) {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(activity)
        builder.setTitle(stavka.artikalNaziv)
        val input = EditText(activity)
        input.setHint("Количина ...")
        input.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.text = Editable.Factory.getInstance().newEditable(stavka.kolicina.toString())
        input.selectAll()
        input.requestFocus()
        builder.setView(input)

        builder.setPositiveButton("Потврди", DialogInterface.OnClickListener { dialog, which ->
            // Here you get get input text from the Edittext
            val strKolicina = input.text.toString()
            var kolicina: Double = 0.0
            if (strKolicina.isEmpty().not()) {
                kolicina = strKolicina.toDouble()
            }
            val st = StavkaObrSoNaracal(
                stavka.redID,
                SettingsHelper.magacin_id,
                narackaGlava!!.narackaBroj,
                App.korisnik.korisnik,
                stavka.artikal_ID,
                kolicina,
                ""
            )
            saveStavka(st)
        })
        builder.setNegativeButton(
            "Откажи",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        val d = builder.create()
        d.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        d.show()
        //builder.show()
    }

    private fun saveStavka(stavka: StavkaObrSoNaracal): Stavka {
        val context = this.context
        var ret = Stavka()
        showProgress(true)
        launch {
            ret = repo.saveStavkaSoNaracal(stavka)
            withContext(Dispatchers.Main) {
                showProgress(false)
                fetchStavki()
            }
        }
        return ret
    }

    private fun pecati() {
        showProgress(true)
        launch {
            repo.pecati(PecatiObr(narackaGlava!!.narackaBroj))
            withContext(Dispatchers.Main) {
                showProgress(false)
                fetchStavki()
            }
        }
    }

    private fun showProgress(show: Boolean) {
        try {
            binding.progressBarStavki.visibility = if (show) View.VISIBLE else View.GONE
        }catch (e: Exception){

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StavkiFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(narackaGlava: NarackaGlava): StavkiFragment =
            StavkiFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(NARACKA_GLAVA, narackaGlava)
                }
            }
    }
}