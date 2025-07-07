package com.example.inventorymanager.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.inventorymanager.R
import com.example.inventorymanager.ui.product.ProductActivity

class SplashScreenActivity : AppCompatActivity() {

    private companion object {
        const val SPLASH_DELAY = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, ProductActivity::class.java))
            finish()
        }, SPLASH_DELAY)
    }
}
