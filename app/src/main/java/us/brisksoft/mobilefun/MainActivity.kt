package us.brisksoft.mobilefun

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private var mDrawerLayout: DrawerLayout? = null

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
//                    R.id.nav_movies -> {
//                        intent = Intent(this@MainActivity, RecyclerActivity::class.java)
//                        startActivity(intent)
//                    }
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
}

