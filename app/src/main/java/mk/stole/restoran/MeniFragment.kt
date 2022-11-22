package mk.stole.restoran

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import mk.stole.restoran.data.ArtikliListViewAdapter
import mk.stole.restoran.data.MeniListViewAdapter
import mk.stole.restoran.data.SettingsHelper
import mk.stole.restoran.data.repo.RestoranRepo
import mk.stole.restoran.data.to.*
import mk.stole.restoran.databinding.FragmentMeniBinding


private const val NARACKA_GLAVA = "narackaGlava"

/**
 * A simple [Fragment] subclass.
 * Use the [MeniFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MeniFragment : Fragment(), CoroutineScope by MainScope() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentMeniBinding? = null
    private val binding get() = _binding!!

    internal data class StavkaTag(var redID: Int, var artikal_ID: Int)
    private data class MeniIstorija(var meni_id: Int, var naziv: String)

    val rootMeniID: Int = SettingsHelper.meniRootID
    private lateinit var narackaGlava: NarackaGlava
    private var istorija: ArrayList<MeniIstorija> = ArrayList()
    private val repo: RestoranRepo = RestoranRepo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        arguments?.let {
            narackaGlava = it.getSerializable(NARACKA_GLAVA) as NarackaGlava
        }
        istorija.add(MeniIstorija(rootMeniID, "Мени"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMeniBinding.inflate(inflater, container, false)

        binding.btnKolicinaMinusFrag.setOnClickListener {
            if (binding.btnArtikalNazivFrag.tag == null) {
                prazniPolinja()
                return@setOnClickListener
            }
            val stavkaTag = binding.btnArtikalNazivFrag.tag as StavkaTag
            if (stavkaTag != null) {
                try {
                    var kolicina: Double = binding.editKolicinaFrag.text.toString().toDouble() - 1.0
                    val st = StavkaObrSoNaracal(
                        stavkaTag.redID.toLong(),
                        SettingsHelper.magacin_id,
                        narackaGlava.narackaBroj,
                        App.korisnik.korisnik,
                        0,
                        kolicina,
                        ""
                    )
                    saveStavka(st)
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.btnKolicinaPlusFrag.setOnClickListener {
            if (binding.btnArtikalNazivFrag.tag == null) {
                prazniPolinja()
                return@setOnClickListener
            }
            val stavkaTag = binding.btnArtikalNazivFrag.tag as StavkaTag
            if (stavkaTag != null) {
                try {
                    var kolicina: Double = binding.editKolicinaFrag.text.toString().toDouble() + 1.0
                    if (kolicina >= 0) {
                        val st = StavkaObrSoNaracal(
                            stavkaTag.redID.toLong(),
                            SettingsHelper.magacin_id,
                            narackaGlava.narackaBroj,
                            App.korisnik.korisnik,
                            0,
                            kolicina,
                            ""
                        )
                        saveStavka(st)
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.editKolicinaFrag.setOnEditorActionListener { v, actionId, event ->
            if (binding.btnArtikalNazivFrag.tag == null || binding.editKolicinaFrag.text.toString()
                    .isEmpty()
            ) {
                prazniPolinja()
                false
            }
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm =
                    activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.editKolicinaFrag.windowToken, 0)
                binding.editKolicinaFrag.clearFocus()
                try {
                    val stavkaTag = binding.btnArtikalNazivFrag.tag as StavkaTag
                    if (stavkaTag != null) {
                        try {
                            var kolicina: Double =
                                binding.editKolicinaFrag.text.toString().toDouble()
                            if (kolicina >= 0) {
                                val st = StavkaObrSoNaracal(
                                    stavkaTag.redID.toLong(),
                                    SettingsHelper.magacin_id,
                                    narackaGlava.narackaBroj,
                                    App.korisnik.korisnik,
                                    0,
                                    kolicina,
                                    ""
                                )
                                saveStavka(st)
                            }
                        } catch (e: Exception) {
                            //Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            prazniPolinja()
                        }
                    }
                } catch (e: Exception) {
                    //Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    prazniPolinja()
                }
                true
            } else {
                false
            }
        }
        binding.btnArtikalNazivFrag.setOnClickListener {
            if (binding.btnArtikalNazivFrag.tag == null) {
                prazniPolinja()
                return@setOnClickListener
            }
            try {
                val stavkaTag = binding.btnArtikalNazivFrag.tag as StavkaTag
                if (stavkaTag != null) {
                    fetchArtikalOpis(stavkaTag.artikal_ID)
                }
            } catch (e: Exception) {

            }
        }
        prazniPolinja()
        fetchMeni(rootMeniID)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        istorija.clear()
        istorija.add(MeniIstorija(rootMeniID, "Мени"))
        fetchMeni(rootMeniID)
        binding.txtMeniTekovno?.text = ""
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_naracka, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.item_meni_root -> {
                //Toast.makeText(activity, "Root", Toast.LENGTH_SHORT).show()
                istorija.clear()
                istorija.add(MeniIstorija(rootMeniID, "Мени"))
                fetchMeni(rootMeniID)
                binding.txtMeniTekovno?.text = ""
                return true
            }
            R.id.item_meni_up -> {
                //Toast.makeText(activity, "Up", Toast.LENGTH_SHORT).show()
                if (istorija.lastIndex > 0) {
                    istorija.removeAt(istorija.lastIndex)
                    fetchMeni(istorija[istorija.lastIndex].meni_id)
                    binding.txtMeniTekovno?.text =
                        if (istorija.lastIndex > 0) istorija[istorija.lastIndex].naziv else ""
                } else {
                    istorija.clear()
                    istorija.add(MeniIstorija(rootMeniID, "Мени"))
                    fetchMeni(rootMeniID)
                    binding.txtMeniTekovno?.text = ""
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchMeni(meni_id: Int, naziv: String = "") {
        val context = this.context
        showProgress(true)

        launch {
            val meniLista = repo.getMeniFor(meni_id)
            withContext(Dispatchers.Main) {
                showProgress(false)
                prazniPolinja()
                if(meniLista.isEmpty()) {
                    binding.gridArtikliFrag.adapter = null
                    return@withContext
                }
                if (meniLista[0].Tip == 1) {
                    if (naziv.isNotEmpty()) {
                        istorija.add(MeniIstorija(meni_id, naziv))
                    }
                    binding.gridArtikliFrag.adapter = null
                    binding.gridMeniFrag.adapter = MeniListViewAdapter(context!!, meniLista)
                    binding.gridMeniFrag.onItemClickListener =
                        AdapterView.OnItemClickListener { parent, view, position, id ->
                            // Get the GridView selected/clicked item text
                            val selectedItem = parent.getItemAtPosition(position) as Meni

                            binding.txtMeniTekovno?.text = selectedItem.Naziv
                            fetchMeni(selectedItem.Sifra_ID, selectedItem.Naziv)
                        }
                }
                if (meniLista[0].Tip == 2) {
                    //Toast.makeText(context, Gson().toJson(meniLista), Toast.LENGTH_SHORT).show()
                    binding.gridArtikliFrag.adapter = ArtikliListViewAdapter(context!!, meniLista)
                    binding.gridArtikliFrag.onItemClickListener =
                        AdapterView.OnItemClickListener { parent, view, position, id ->
                            // Get the GridView selected/clicked item text
                            val selectedItem = parent.getItemAtPosition(position) as Meni
                            if (selectedItem != null && selectedItem.Sifra_ID > 0) {
                                /*Toast.makeText(
                                    context,
                                    Gson().toJson(selectedItem),
                                    Toast.LENGTH_SHORT
                                ).show()*/
                                val st = StavkaObrSoNaracal(
                                    0,
                                    SettingsHelper.magacin_id,
                                    narackaGlava.narackaBroj,
                                    App.korisnik.korisnik,
                                    selectedItem.Sifra_ID,
                                    1.0,
                                    ""
                                )
                                saveStavka(st)
                            }
                        }
                }
            }
        }
    }

    private fun saveStavka(stavka: StavkaObrSoNaracal): Stavka {
        val context = this.context
        var ret = Stavka()
        showProgress(true)
        launch {
            ret = repo.saveStavkaSoNaracal(stavka)
            withContext(Dispatchers.Main) {
                showProgress(false)
                prazniPolinja()
                if (ret.redID > 0 && ret.artikal_ID > 0) {

                    var naziv = if (ret.artikalNaziv.length > 24) ret.artikalNaziv.substring(
                        0,
                        24
                    ) else ret.artikalNaziv
                    binding.btnArtikalNazivFrag.text =
                        Html.fromHtml("$naziv <b>${ret.cena} ден.</b>")
                    binding.btnArtikalNazivFrag.tag =
                        StavkaTag(ret.redID.toInt(), ret.artikal_ID)
                    binding.editKolicinaFrag.text =
                        Editable.Factory.getInstance().newEditable(ret.kolicina.toString())
                    binding.editKolicinaFrag.clearFocus()

                } else {
                    //Toast.makeText(context, Gson().toJson(ret), Toast.LENGTH_SHORT).show()
                }
            }
        }
        return ret
    }

    private fun fetchArtikalOpis(artikal_id: Int): Array<ArtikalOpis> {
        val context = this.context
        var ret: Array<ArtikalOpis> = emptyArray()
        showProgress(true)
        launch {
            ret = repo.getArtikalOpis(artikal_id)
            withContext(Dispatchers.Main) {
                showProgress(false)
                if (ret.isNullOrEmpty()) {
                    return@withContext
                }
                val inputText = EditText(activity)
                inputText.hint = "забелешка ..."
                val mSelectedItems: ArrayList<Int> = ArrayList() // Where we track the selected items
                val opisi_list: Array<CharSequence> = ret.map { it.opis }.toTypedArray()
                activity?.let {

                    val builder = AlertDialog.Builder(it)
                    // Set the dialog title
                    builder.setTitle(binding.btnArtikalNazivFrag.text)
                        // Specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive callbacks when items are selected
                        .setMultiChoiceItems(opisi_list, null,
                            DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which)
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(which)
                                }
                            })
                        // Set the action buttons
                        .setPositiveButton("OK",
                            DialogInterface.OnClickListener { dialog, id ->
                                // User clicked OK, so save the selectedItems results somewhere
                                // or return them to the component that opened the dialog
                                var msg: String = ""
                                for (i in mSelectedItems) {
                                    val st = StavkaObrSoNaracal(
                                        0,
                                        SettingsHelper.magacin_id,
                                        narackaGlava.narackaBroj,
                                        App.korisnik.korisnik,
                                        0,
                                        1.0,
                                        opisi_list[i].toString()
                                    )
                                    saveStavka(st)
                                    msg += opisi_list[i].toString() + "\n"
                                }
                                if (inputText.text.isNotEmpty()) {
                                    val txt = "** " + inputText.text
                                    val st = StavkaObrSoNaracal(
                                        0,
                                        SettingsHelper.magacin_id,
                                        narackaGlava.narackaBroj,
                                        App.korisnik.korisnik,
                                        0,
                                        1.0,
                                        txt
                                    )
                                    saveStavka(st)
                                    msg += txt
                                }
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            })
                        .setNegativeButton("Cancel",
                            DialogInterface.OnClickListener { dialog, id ->

                            })
                    builder.setView(inputText)
                    builder.create().show()
                }
            }
        }
        return ret
    }

    private fun showProgress(show: Boolean) {
        binding.progressBarMeniArtikli.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun prazniPolinja() {
        try {
            binding.btnArtikalNazivFrag.text = ""
            binding.btnArtikalNazivFrag.tag = null
            binding.editKolicinaFrag.text.clear()
            binding.editKolicinaFrag.clearFocus()
            //binding.txtMeniTekovno.text = ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(narackaGlava: NarackaGlava): MeniFragment =
            MeniFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(NARACKA_GLAVA, narackaGlava)
                }
            }
    }
}