package com.lavyshyk.photoapp


import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lavyshyk.photoapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var view: View
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        setupBottomNavMenu(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.fullScreenFragment) {
              binding.bottomNavigationView.visibility = View.GONE
                binding.iconCommon.visibility = View.GONE
                WindowInsetsControllerCompat(window,  window.decorView).hide(  WindowInsetsCompat.Type.systemBars())
            } else {
               binding.bottomNavigationView.visibility = View.VISIBLE
                binding.iconCommon.visibility = View.VISIBLE
                WindowInsetsControllerCompat(window,  window.decorView).show( WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    private fun setupBottomNavMenu(navController: NavController) {
         bottomNav= binding.bottomNavigationView
        bottomNav.setupWithNavController(navController)
    }
}