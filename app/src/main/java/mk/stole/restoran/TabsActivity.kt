package mk.stole.restoran

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import mk.stole.restoran.data.to.NarackaGlava
import mk.stole.restoran.databinding.ActivityTabsBinding


class TabsActivity : AppCompatActivity() {
    private var _binding: ActivityTabsBinding? = null
    private val binding get() = _binding!!
    private var narackaGlava: NarackaGlava = NarackaGlava()

    private lateinit var pageChangeCallback: ViewPager2.OnPageChangeCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityTabsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Нарачка"
            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        narackaGlava =
            Gson().fromJson(intent.getStringExtra("narackaGlava"), NarackaGlava::class.java)
        val tabsPagerAdapter = TabsPagerAdapter(supportFragmentManager, lifecycle, 2)
        tabsPagerAdapter.narackaGlava = narackaGlava
        binding.tabsViewpager.adapter = tabsPagerAdapter
        pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == 0) {
                    // you are on the first page
                } else if (position == 1) {
                    // you are on the second page
                    //tabsPagerAdapter.stavkiFragment?.fetchStavki()
                } else if (position == 2) {
                    // you are on the third page
                }
            }
        }
        binding.tabsViewpager.registerOnPageChangeCallback(pageChangeCallback)

        // Link the TabLayout and the ViewPager2 together and Set Text & Icons
        TabLayoutMediator(binding.tabLayout, binding.tabsViewpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Маса ${narackaGlava.naziv}"
                    //tab.setIcon(R.drawable.ic_music)
                }
                1 -> {
                    tab.text = "Ставки"
                    //tab.setIcon(R.drawable.ic_movie)
                }
            }
            // Change color of the icons
            tab.icon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    Color.WHITE,
                    BlendModeCompat.SRC_ATOP
                )
        }.attach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.tabsViewpager.unregisterOnPageChangeCallback(pageChangeCallback)
        _binding = null
    }
}