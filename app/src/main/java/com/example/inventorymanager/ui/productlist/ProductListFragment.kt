package com.example.inventorymanager.ui.productlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.inventorymanager.R
import com.example.inventorymanager.data.local.ProductDatabase
import com.example.inventorymanager.data.network.NetworkModule
import com.example.inventorymanager.data.repository.ProductRepository
import com.example.inventorymanager.databinding.FragmentProductListBinding

class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProductListViewModel
    private lateinit var adapter: ProductListAdapter

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

        setupViewModel()
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()
        setupClickListeners()
    }

    private fun setupViewModel() {
        val database = ProductDatabase.getDatabase(requireContext())
        val repository = ProductRepository(NetworkModule.apiService, database.productDao())

        // Direct ViewModelProvider without custom factory
        viewModel = ViewModelProvider(this)[ProductListViewModel::class.java]
        viewModel.initRepository(repository)
    }

    private fun setupRecyclerView() {
        adapter = ProductListAdapter(
            onProductClick = { product ->
                try {
                    if (product.id > 0) {
                        findNavController().navigate(
                            R.id.action_productList_to_productDetail,
                            Bundle().apply { putInt("productId", product.id) }
                        )
                    } else {
                        Toast.makeText(context, "Invalid product", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Cannot open product details", Toast.LENGTH_SHORT).show()
                }
            },
            onFavoriteClick = { product ->
                viewModel.toggleFavorite(product)
            }
        )

        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@ProductListFragment.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshProducts()
        }
    }

    private fun setupClickListeners() {
        binding.fabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_productList_to_productAdd)
        }
    }

    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
            binding.textEmpty.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
            binding.progressBar.visibility = if (isLoading && adapter.itemCount == 0) View.VISIBLE else View.GONE
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
