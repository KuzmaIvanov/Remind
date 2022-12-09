package com.example.remind

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.remind.databinding.ActivityMainBinding
import com.example.remind.screens.SettingsActivity
import com.example.remind.screens.CategoriesFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.toolbar.root
        toolbar.title = getToolbarTitle(0)
        setSupportActionBar(toolbar)
        val drawer: DrawerLayout = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.nav_open_drawer,
            R.string.nav_close_drawer
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView: NavigationView = binding.navigationView
        navigationView.setNavigationItemSelectedListener(this)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.rootForFragment, CategoriesFragment().also{
                val bundle = Bundle()
                bundle.putString("db_table_name", getDbTableName(0))
                it.arguments = bundle
            })
            .commit()

    }
    private fun getDbTableName(id: Int): String {
        return when(id) {
            R.id.main_first_item -> "daily_routine_table"
            R.id.main_second_item -> "sport_table"
            R.id.main_third_item -> "medicine_table"
            R.id.main_fourth_item -> "significant_date_table"
            else -> "daily_routine_table"
        }
    }
    private fun getToolbarTitle(id: Int): String {
        return when(id) {
            R.id.main_first_item -> "Daily routine"
            R.id.main_second_item -> "Sport"
            R.id.main_third_item -> "Medicine"
            R.id.main_fourth_item -> "Significant date"
            else -> "Daily routine"
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        var fragment: Fragment? = null
        var intent: Intent? = null
        when(id) {
            R.id.labels_first_item -> {
                intent = Intent(this, SettingsActivity::class.java)
            }
            else -> {
                fragment = CategoriesFragment()
                val bundle = Bundle()
                bundle.putString("db_table_name", getDbTableName(id))
                toolbar.title = getToolbarTitle(id)
                fragment.arguments = bundle
            }
        }
        if(fragment!=null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.rootForFragment, fragment)
                .commit()
        } else {
            startActivity(intent)
        }
        val drawer = binding.drawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        val drawer = binding.drawerLayout
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}