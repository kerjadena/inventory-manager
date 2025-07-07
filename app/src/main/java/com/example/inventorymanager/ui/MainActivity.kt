package com.example.inventorymanager.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.inventorymanager.R
import com.example.inventorymanager.databinding.ActivityMainBinding
import com.example.inventorymanager.viewmodel.ProductViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val productViewModel: ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        try {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment

            if (navHostFragment != null) {
                val navController = navHostFragment.navController
                binding.bottomNavigation.setupWithNavController(navController)
            } else {
                // Log error or handle case where NavHostFragment is not found
                // You could also try to find it after a delay or in onResume
                throw IllegalStateException("NavHostFragment not found")
            }
        } catch (e: Exception) {
            // Handle the exception gracefully
            e.printStackTrace()
            // Optionally, you could retry after a short delay or in onResume
        }
    }
}
