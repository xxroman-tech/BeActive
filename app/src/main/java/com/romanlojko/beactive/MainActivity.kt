package com.romanlojko.beactive

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

interface DrawerLocker{
    fun setDrawerLocked(shouldLock: Boolean)
}

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener , DrawerLocker{

    private lateinit var toolbar: Toolbar
    private lateinit var mDrawer: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolBar)
        setSupportActionBar(toolbar)

        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this);

        mDrawer = findViewById(R.id.drawerLayout)
        val drawerToggle = ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.open, R.string.close)
        mDrawer.addDrawerListener(drawerToggle)
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun setDrawerLocked(enabled: Boolean) {
        if (enabled) {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

}