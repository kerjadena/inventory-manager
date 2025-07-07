package com.example.inventorymanager.ui.productsearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventorymanager.R
import com.example.inventorymanager.data.local.ProductDatabase
import com.example.inventorymanager.data.network.NetworkModule
import com.example.inventorymanager.data.repository.ProductRepository
import com.example.inventorymanager.databinding.FragmentProductSearchBinding
import com.example.inventorymanager.ui.productlist.ProductListAdapter

class ProductSearchFragment : Fragment() {

    private var _binding: FragmentProductSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProductSearchViewModel
    private lateinit var adapter: ProductListAdapter

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

        setupViewModel()
        setupRecyclerView()
        setupSearchView()
        observeViewModel()
    }

    private fun setupViewModel() {
        val database = ProductDatabase.getDatabase(requireContext())
        val repository = ProductRepository(NetworkModule.apiService, database.productDao())

        // Direct ViewModelProvider without custom factory
        viewModel = ViewModelProvider(this)[ProductSearchViewModel::class.java]
        viewModel.initRepository(repository)
    }

    private fun setupRecyclerView() {
        adapter = ProductListAdapter(
            onProductClick = { product ->
                findNavController().navigate(
                    R.id.action_productSearch_to_productDetail,
                    Bundle().apply { putInt("productId", product.id) }
                )
            },
            onFavoriteClick = { product ->
                viewModel.toggleFavorite(product)
            }
        )

        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ProductSearchFragment.adapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchProducts(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    viewModel.clearSearch()
                }
                return true
            }
        })

        binding.buttonClear.setOnClickListener {
            binding.searchView.setQuery("", false)
            viewModel.clearSearch()
        }
    }

    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
            binding.textEmpty.visibility = if (products.isEmpty() &&
                !viewModel.searchQuery.value.isNullOrBlank()) View.VISIBLE else View.GONE
            binding.textHint.visibility = if (products.isEmpty() &&
                viewModel.searchQuery.value.isNullOrBlank()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
