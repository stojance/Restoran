package mk.stole.restoran

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import mk.stole.restoran.data.to.NarackaGlava

class TabsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle, private var numberOfTabs: Int) : FragmentStateAdapter(fm, lifecycle) {
    var narackaGlava: NarackaGlava = NarackaGlava()
    var meniFragment: MeniFragment? = null
    var stavkiFragment: StavkiFragment? = null


    override fun createFragment(position: Int): Fragment {

        when (position) {
            0 -> {
                // # Music Fragment
                val bundle = Bundle()
                bundle.putString("fragmentName", "Наарачка")
                bundle.putSerializable("narackaGlava", narackaGlava)
                meniFragment = MeniFragment()
                meniFragment!!.arguments = bundle

                return meniFragment!!
            }
            1 -> {
                // # Movies Fragment
                val bundle = Bundle()
                bundle.putString("fragmentName", "Наарачка")
                bundle.putSerializable("narackaGlava", narackaGlava)
                stavkiFragment = StavkiFragment()
                stavkiFragment!!.arguments = bundle

                return stavkiFragment!!
            }

            else -> return Fragment()
        }
    }

    override fun getItemCount(): Int {
        return numberOfTabs
    }
}
