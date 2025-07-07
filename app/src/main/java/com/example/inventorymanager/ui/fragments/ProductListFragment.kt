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
import com.example.inventorymanager.databinding.FragmentProductListBinding
import com.example.inventorymanager.ui.adapters.ProductAdapter
import com.example.inventorymanager.viewmodel.ProductViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private val productViewModel: ProductViewModel by activityViewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        loadProducts()

        view.findViewById<FloatingActionButton>(R.id.fabAddProduct).setOnClickListener {
            findNavController().navigate(R.id.action_productList_to_addProduct)
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }
    }

    private fun setupObservers() {
        // Observe remote products from API
        productViewModel.remoteProducts.observe(viewLifecycleOwner) { products ->
            binding.progressBar.visibility = View.GONE
            if (products.isNotEmpty()) {
                binding.recyclerView.visibility = View.VISIBLE
                binding.textViewNoProducts.visibility = View.GONE
                productAdapter.submitList(products)
            } else {
                binding.recyclerView.visibility = View.GONE
                binding.textViewNoProducts.visibility = View.VISIBLE
            }
        }

        // Observe errors
        productViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadProducts() {
        binding.progressBar.visibility = View.VISIBLE
        productViewModel.getAllProducts(limit = 20, skip = 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
