package com.example.inventorymanager.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.inventorymanager.databinding.ActivityProductAddBinding
import com.example.inventorymanager.model.Product
import com.example.inventorymanager.viewmodel.ProductViewModel
import java.util.*

class ProductAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductAddBinding
    private val productViewModel: ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSaveProduct.setOnClickListener {
            saveProduct()
        }
    }

    private fun saveProduct() {
        val product = Product(
            id = System.currentTimeMillis().toInt(), // Using timestamp as a temporary ID
            title = binding.edTitle.text.toString(),
            description = binding.edDescription.text.toString(),
            price = binding.edPrice.text.toString().toDoubleOrNull() ?: 0.0,
            brand = binding.edBrand.text.toString(),
            category = binding.edCategory.text.toString()
        )

        productViewModel.addToFavorites(product)
        finish()
    }
}
