package com.example.inventorymanager.ui.productadd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.inventorymanager.R
import com.example.inventorymanager.data.local.ProductDatabase
import com.example.inventorymanager.data.model.Category
import com.example.inventorymanager.data.network.NetworkModule
import com.example.inventorymanager.data.repository.ProductRepository
import com.example.inventorymanager.databinding.FragmentProductAddBinding

class ProductAddFragment : Fragment() {

    private var _binding: FragmentProductAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProductAddViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupViewModel() {
        val database = ProductDatabase.getDatabase(requireContext())
        val repository = ProductRepository(NetworkModule.apiService, database.productDao())

        // Direct ViewModelProvider without custom factory
        viewModel = ViewModelProvider(this)[ProductAddViewModel::class.java]
        viewModel.initRepository(repository)
    }

    private fun setupClickListeners() {
        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().navigateUp()
            }

            buttonSave.setOnClickListener {
                saveProduct()
            }

            buttonClear.setOnClickListener {
                clearForm()
            }
        }
    }

    private fun observeViewModel() {
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
                Toast.makeText(context, "Product added successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun setupCategorySpinner(categories: List<Category>) {
        val categoryNames = categories.map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categoryNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun saveProduct() {
        binding.apply {
            val title = editTitle.text.toString()
            val description = editDescription.text.toString()
            val price = editPrice.text.toString()
            val brand = editBrand.text.toString()
            val selectedPosition = spinnerCategory.selectedItemPosition
            val category = if (selectedPosition >= 0 && viewModel.categories.value != null) {
                viewModel.categories.value!![selectedPosition].slug
            } else {
                ""
            }

            viewModel.addProduct(title, description, price, brand, category)
        }
    }

    private fun clearForm() {
        binding.apply {
            editTitle.text?.clear()
            editDescription.text?.clear()
            editPrice.text?.clear()
            editBrand.text?.clear()
            spinnerCategory.setSelection(0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
