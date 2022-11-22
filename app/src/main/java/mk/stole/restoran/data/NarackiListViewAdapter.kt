package mk.stole.restoran.data

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import mk.stole.restoran.App
import mk.stole.restoran.R
import mk.stole.restoran.data.to.NarackaGlava


class NarackiListViewAdapter(context: Context, private val itemsArray: Array<NarackaGlava>) :
    ArrayAdapter<NarackaGlava>(context, R.layout.grid_naracka, itemsArray) {
    override fun getCount(): Int {
        return itemsArray.size
    }

    override fun getItem(position: Int): NarackaGlava {
        return itemsArray[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ItemHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_naracka, null, false)
            holder = ItemHolder()
            holder.button = view!!.findViewById(R.id.gridNarackaButton)

            view.tag = holder
        } else {
            holder = view.tag as ItemHolder
        }

        val naracka = itemsArray[position]
        holder.button!!.tag = naracka
        if (naracka.narackaBroj == 0) {
            holder.button!!.text = "НОВА"
            holder.button!!.setBackgroundColor(Color.rgb(0,102,0))
            //holder.button!!.setBackgroundResource(R.drawable.round_button_green)
        } else {
            val spaned: Spanned = Html.fromHtml("<b><u>${naracka.naziv}</u></b><br/>${naracka.iznos}<br/>ден.")
            holder.button!!.text = spaned
            holder.button!!.setBackgroundColor(Color.rgb(0,51,102))
            if(naracka.naracal == App.korisnik.korisnik) holder.button!!.setBackgroundColor(Color.rgb(0,25,51))
            //holder.button!!.setBackgroundResource(R.drawable.round_button_blue)
        }
        holder.button!!.setTextColor(Color.WHITE)

        return view
    }

    private data class ItemHolder(
        var button: Button? = null
    )
}