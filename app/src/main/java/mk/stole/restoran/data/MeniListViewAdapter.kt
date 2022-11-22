package mk.stole.restoran.data

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import mk.stole.restoran.R
import mk.stole.restoran.data.to.Meni

class MeniListViewAdapter(context: Context, private val itemsArray: Array<Meni>):
    ArrayAdapter<Meni>(context, R.layout.grid_meni, itemsArray) {
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
            view = LayoutInflater.from(context).inflate(R.layout.grid_meni, null, false)
            holder = ItemHolder()
            holder.button = view!!.findViewById(R.id.gridMeniButton)
            view.tag = holder
        } else {
            holder = view.tag as ItemHolder
        }
        val meni = itemsArray[position]
        holder.button!!.tag = meni
        //val spaned: Spanned = Html.fromHtml("<b>${meni.Naziv}</b>")
        holder.button!!.text = meni.Naziv //spaned
        holder.button!!.setBackgroundColor(Color.rgb(0,76,153))
        //holder.button!!.setBackgroundResource(R.drawable.round_button_dark_blue)
        //holder.button!!.setTextColor(Color.WHITE)

        return view
    }

    private data class ItemHolder(
        var button: Button? = null
    )
}