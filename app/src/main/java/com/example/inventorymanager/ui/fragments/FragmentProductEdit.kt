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

class FragmentProductEdit : Fragment() {

    private var _binding: ActivityProductAddBinding? = null
    private val binding get() = _binding!!
    private val productViewModel: ProductViewModel by activityViewModels()
    private var productToEdit: Product? = null

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
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        productViewModel.selectedProduct.observe(viewLifecycleOwner) { product ->
            product?.let {
                productToEdit = it
                populateFields(it)
            }
        }
    }

    private fun populateFields(product: Product) {
        binding.apply {
            edTitle.setText(product.title)
            edDescription.setText(product.description)
            edPrice.setText(product.price.toString())
            edBrand.setText(product.brand)
            edCategory.setText(product.category)
        }
    }

    private fun setupClickListeners() {
        binding.btnSaveProduct.apply {
            text = "Update Product"
            setOnClickListener {
                updateProduct()
            }
        }
    }

    private fun updateProduct() {
        productToEdit?.let { original ->
            val updatedProduct = Product(
                id = original.id,
                title = binding.edTitle.text.toString(),
                description = binding.edDescription.text.toString(),
                price = binding.edPrice.text.toString().toDoubleOrNull() ?: original.price,
                brand = binding.edBrand.text.toString(),
                category = binding.edCategory.text.toString(),
                imageUrl = original.imageUrl,
                rating = original.rating,
                stock = original.stock
            )

            productViewModel.editProduct(original.id, updatedProduct)
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
