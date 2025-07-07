package com.example.inventorymanager.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventorymanager.R
import com.example.inventorymanager.databinding.FragmentProductSearchBinding
import com.example.inventorymanager.ui.adapters.ProductAdapter
import com.example.inventorymanager.viewmodel.ProductViewModel

class FragmentProductSearch : Fragment() {

    private var _binding: FragmentProductSearchBinding? = null
    private val binding get() = _binding!!
    private val productViewModel: ProductViewModel by activityViewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onItemClick = { product ->
                productViewModel.setSelectedProduct(product)
                findNavController().navigate(R.id.action_search_to_productDetail)
            },
            onFavoriteClick = { product ->
                productViewModel.addToFavorites(product)
                // Safe context check to prevent IllegalException
                context?.let { ctx ->
                    Toast.makeText(ctx, "Added to favorites", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.recyclerViewSearch.apply {
            // Use context from binding root instead of requireContext()
            layoutManager = LinearLayoutManager(binding.root.context)
            adapter = productAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        productViewModel.searchProducts(it)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun setupObservers() {
        productViewModel.searchResults.observe(viewLifecycleOwner) { products ->
            binding.progressBar.visibility = View.GONE
            if (products.isNotEmpty()) {
                binding.recyclerViewSearch.visibility = View.VISIBLE
                binding.textViewNoResults.visibility = View.GONE
                productAdapter.submitList(products)
            } else {
                binding.recyclerViewSearch.visibility = View.GONE
                binding.textViewNoResults.visibility = View.VISIBLE
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
