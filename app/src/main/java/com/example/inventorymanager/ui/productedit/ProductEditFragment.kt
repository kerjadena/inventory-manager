package com.example.inventorymanager.ui.productedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.inventorymanager.data.local.ProductDatabase
import com.example.inventorymanager.data.network.NetworkModule
import com.example.inventorymanager.data.repository.ProductRepository
import com.example.inventorymanager.databinding.FragmentProductEditBinding

class ProductEditFragment : Fragment() {

    private var _binding: FragmentProductEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProductEditViewModel
    private var productId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get productId from arguments
        productId = arguments?.getInt("productId") ?: 0

        setupViewModel()
        setupClickListeners()
        observeViewModel()

        viewModel.loadProduct(productId)
    }

    private fun setupViewModel() {
        val database = ProductDatabase.getDatabase(requireContext())
        val repository = ProductRepository(NetworkModule.apiService, database.productDao())

        // Direct ViewModelProvider without custom factory
        viewModel = ViewModelProvider(this)[ProductEditViewModel::class.java]
        viewModel.initRepository(repository)
    }

    private fun setupClickListeners() {
        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().navigateUp()
            }

            buttonSave.setOnClickListener {
                updateProduct()
            }

            buttonReset.setOnClickListener {
                resetForm()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.product.observe(viewLifecycleOwner) { product ->
            product?.let { bindProductData(it) }
        }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            setupCategorySpinner(categories)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.apply {
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                buttonSave.isEnabled = !isLoading
                formLayout.alpha = if (isLoading) 0.5f else 1.0f
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.success.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Product updated successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun bindProductData(product: com.example.inventorymanager.data.model.Product) {
        binding.apply {
            editTitle.setText(product.title)
            editDescription.setText(product.description)
            editPrice.setText(product.price.toString())
            editBrand.setText(product.brand)

            // Set category in spinner
            val adapter = spinnerCategory.adapter as? ArrayAdapter<String>
            adapter?.let {
                val position = it.getPosition(product.category)
                if (position >= 0) {
                    spinnerCategory.setSelection(position)
                }
            }
        }
    }

    private fun setupCategorySpinner(categories: List<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

        // Re-bind product data after spinner is set up
        viewModel.product.value?.let { bindProductData(it) }
    }

    private fun updateProduct() {
        binding.apply {
            val title = editTitle.text.toString().takeIf { it.isNotBlank() }
            val description = editDescription.text.toString().takeIf { it.isNotBlank() }
            val price = editPrice.text.toString().takeIf { it.isNotBlank() }
            val brand = editBrand.text.toString().takeIf { it.isNotBlank() }
            val category = spinnerCategory.selectedItem?.toString()?.takeIf { it.isNotBlank() }

            viewModel.updateProduct(productId, title, description, price, brand, category)
        }
    }

    private fun resetForm() {
        viewModel.product.value?.let { bindProductData(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
