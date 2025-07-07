package com.example.inventorymanager.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.inventorymanager.databinding.ActivityProductAddBinding
import com.example.inventorymanager.model.Product
import com.example.inventorymanager.viewmodel.ProductViewModel
import java.util.*

class FragmentProductAdd : Fragment() {

    private var _binding: ActivityProductAddBinding? = null
    private val binding get() = _binding!!
    private val productViewModel: ProductViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityProductAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}