package com.mobile.app_iara

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mobile.app_iara.databinding.ActivityMainBinding
import com.mobile.app_iara.ui.camera.CameraOverlay

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_gestao, R.id.navigation_perfil)
        )

        val bottomNav: BottomNavigationView = binding.navView

        val tipoUser = "comum"

        if (tipoUser == "comum") {
            bottomNav.menu.findItem(R.id.navigation_gestao)?.isVisible = false
            bottomNav.menu.findItem(R.id.navigation_dashboard)?.isVisible = false
        } else {
            for (i in 0 until bottomNav.menu.size()) {
                bottomNav.menu.getItem(i).isVisible = true
            }
        }

        bottomNav.setupWithNavController(navController)
    }
}