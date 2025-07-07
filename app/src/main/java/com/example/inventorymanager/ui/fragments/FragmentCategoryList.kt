package com.example.inventorymanager.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventorymanager.R
import com.example.inventorymanager.databinding.FragmentCategoryListBinding
import com.example.inventorymanager.ui.adapters.CategoryAdapter
import com.example.inventorymanager.viewmodel.ProductViewModel

class FragmentCategoryList : Fragment() {

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!
    private val productViewModel: ProductViewModel by activityViewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            // Navigate to products by category
            findNavController().navigate(
                R.id.action_categoryList_to_productsByCategory,
                Bundle().apply {
                    putString("category", category)
                }
            )
        }
        binding.recyclerViewCategories.apply {
            // Use context from binding root instead of requireContext()
            layoutManager = LinearLayoutManager(binding.root.context)
            adapter = categoryAdapter
        }
    }

    private fun setupObservers() {
        productViewModel.categories.observe(viewLifecycleOwner) { categories ->
            binding.progressBar.visibility = View.GONE
            if (categories.isNotEmpty()) {
                binding.recyclerViewCategories.visibility = View.VISIBLE
                binding.textViewNoCategories.visibility = View.GONE
                categoryAdapter.submitList(categories)
            } else {
                binding.recyclerViewCategories.visibility = View.GONE
                binding.textViewNoCategories.visibility = View.VISIBLE
            }
        }

        productViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                binding.progressBar.visibility = View.GONE
                // Safe context check to prevent IllegalException
                context?.let { ctx ->
                    Toast.makeText(ctx, it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
