package mk.stole.restoran.data

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import mk.stole.restoran.R
import mk.stole.restoran.data.to.Meni

class ArtikliListViewAdapter(context: Context, private val itemsArray: Array<Meni>):
    ArrayAdapter<Meni>(context, R.layout.grid_artikal, itemsArray) {

    val buttonColors: IntArray = intArrayOf(
        Color.parseColor("#90EE90"), Color.parseColor("#FFFF00"), Color.parseColor("#48D1CC"),
        Color.parseColor("#9370DB"), Color.parseColor("#6A5ACD"), Color.parseColor("#AFEEEE"),
        Color.parseColor("#C0C0C0"), Color.parseColor("#D8BFD8"), Color.parseColor("#7FFFD4"),
        Color.parseColor("#6495ED"), Color.parseColor("#BC8F8F"), Color.parseColor("#E9967A"),
        Color.parseColor("#3CB371"), Color.parseColor("#FFDAB9"), Color.parseColor("#BA55D3"),
        Color.parseColor("#F08080"), Color.parseColor("#FF69B4"), Color.parseColor("#8FBC8F"),
        Color.parseColor("#87CEFA"), Color.parseColor("#DDA0DD")
    )

    override fun getCount(): Int {
        return itemsArray.size
    }

    override fun getItem(position: Int): Meni {
        return itemsArray[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ItemHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_artikal, null, false)
            holder = ItemHolder()
            holder.button = view!!.findViewById(R.id.gridArtikalButton)

            view.tag = holder
        } else {
            holder = view.tag as ItemHolder
        }

        val meni = itemsArray[position]
        holder.button!!.tag = meni
        val naziv = if(meni.Naziv.length > 24) meni.Naziv.substring(0,24) else meni.Naziv

        holder.button!!.text = Html.fromHtml("$naziv<br/><b>${meni.Cena} ден.</b>")
        holder.button!!.setBackgroundColor(buttonColors[meni.Sifra_ID % 20])
        holder.button!!.setTextColor(Color.BLACK)

        return view
    }

    private data class ItemHolder(
        var button: Button? = null
    )
}