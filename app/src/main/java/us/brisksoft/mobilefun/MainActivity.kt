package us.brisksoft.mobilefun

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private var mDrawerLayout: DrawerLayout? = null

    // Array of strings...
    internal var gridItems = arrayOf(
        "Movies",
        "item 2",
        "item 3",
        "item 4"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // setup drawer navigation
        mDrawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        // setup App Bar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)

        // handle navigation drawer events
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(
            NavigationView.OnNavigationItemSelectedListener { menuItem ->
                // close drawer when item is tapped
                mDrawerLayout?.closeDrawers()
                val intent: Intent
                when (menuItem.itemId) {
                    R.id.nav_movies -> {
                        intent = Intent(this@MainActivity, MoviesActivity::class.java)
                        startActivity(intent)
                    }
//                    R.id.nav_map -> {
//                        intent = Intent(this@MainActivity, MapActivity::class.java)
//                        startActivity(intent)
//                    }
//                    R.id.nav_map_cams -> {
//                        intent = Intent(this@MainActivity, TrafficCamMap::class.java)
//                        startActivity(intent)
//                    }
                    R.id.nav_about -> {
                        intent = Intent(this@MainActivity, AboutActivity::class.java)
                        startActivity(intent)
                    }
                }

                true
            })

        val gridview = findViewById<GridView>(R.id.gridview)
        gridview.setAdapter(GridAdapter(this))

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout!!.openDrawer(GravityCompat.START)
                return true
            }
            R.id.action_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_settings -> return true
        }
        return super.onOptionsItemSelected(item)
    }

    inner class GridAdapter(private val mContext: Context) : BaseAdapter() {

        override fun getCount(): Int {
            return gridItems.size
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        // create a new ImageView for each item referenced by the Adapter
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val button: Button
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                button = Button(mContext)
            } else {
                button = convertView as Button
            }

            button.setText(gridItems[position])
            button.id = position
            button.setOnClickListener(BtnOnClickListener())
            return button
        }

    }

    internal inner class BtnOnClickListener : View.OnClickListener {

        override fun onClick(v: View) {

            val intent: Intent
            when (v.id) {
                0 -> {
                    intent = Intent(baseContext, MoviesActivity::class.java)
                    startActivity(intent)
                }
                else -> {
                    val b = v as Button
                    val label = b.text.toString()
                    Toast.makeText(
                        this@MainActivity, label,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

