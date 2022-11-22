package mk.stole.restoran.data

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import mk.stole.restoran.R
import mk.stole.restoran.data.to.Stavka

class StavkiListViewAdapter(context: Context, private val itemsArray:
    Array<Stavka>):ArrayAdapter<Stavka>(context, R.layout.row_stavka,itemsArray) {
    override fun getCount(): Int {
        return itemsArray.size
    }

    override fun getItem(position: Int): Stavka {
        return itemsArray[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ItemHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.row_stavka, null, false)
            holder = ItemHolder()

            holder.tvRedIDRow = view!!.findViewById(R.id.tvRedIDRow)
            holder.tvStavkaRow= view!!.findViewById(R.id.tvStavkaRow)
            holder.tvArtikalNazivRow= view!!.findViewById(R.id.tvArtikalNazivRow)
            holder.tvKolicinaRow= view!!.findViewById(R.id.tvKolicinaRow)
            holder.tvCenaRow= view!!.findViewById(R.id.tvCenaRow)
            holder.tvIznosPoStavkaRow= view!!.findViewById(R.id.tvIznosPoStavkaRow)

            view.tag = holder
        } else {
            holder = view.tag as ItemHolder
        }

        val stavka = itemsArray[position]
        holder.apply {
            tvRedIDRow!!.text = stavka.redID.toString()
            tvStavkaRow!!.text = stavka.stavka.toString()
            tvArtikalNazivRow!!.text = stavka.artikalNaziv
            when(stavka.pecateno.toInt()){
                0 -> tvArtikalNazivRow!!.setTextColor(Color.RED)
                -1 -> tvArtikalNazivRow!!.setTextColor(Color.BLUE)
                else -> tvArtikalNazivRow!!.setTextColor(Color.BLACK)
            }
            tvKolicinaRow!!.text = stavka.kolicina.toString()
            tvCenaRow!!.text = stavka.cena.toString()
            tvIznosPoStavkaRow!!.text = stavka.iznosPoStavka.toString()
        }


        return view
    }

    private data class ItemHolder(
        var tvRedIDRow: TextView? = null,
        var tvStavkaRow: TextView? = null,
        var tvArtikalNazivRow: TextView? = null,
        var tvKolicinaRow: TextView? = null,
        var tvCenaRow: TextView? = null,
        var tvIznosPoStavkaRow: TextView? = null
    )
}