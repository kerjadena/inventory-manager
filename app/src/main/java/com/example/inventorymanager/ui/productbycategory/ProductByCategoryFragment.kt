package com.example.inventorymanager.ui.productbycategory

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
import com.example.inventorymanager.databinding.FragmentProductByCategoryBinding
import com.example.inventorymanager.ui.productlist.ProductListAdapter

class ProductByCategoryFragment : Fragment() {

    private var _binding: FragmentProductByCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProductByCategoryViewModel
    private lateinit var adapter: ProductListAdapter
    private var category: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductByCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get category from arguments
        category = arguments?.getString("category") ?: ""

        setupViewModel()
        setupRecyclerView()
        setupSwipeRefresh()
        setupClickListeners()
        observeViewModel()

        viewModel.loadProductsByCategory(category)
    }

    private fun setupViewModel() {
        val database = ProductDatabase.getDatabase(requireContext())
        val repository = ProductRepository(NetworkModule.apiService, database.productDao())

        // Direct ViewModelProvider without custom factory
        viewModel = ViewModelProvider(this)[ProductByCategoryViewModel::class.java]
        viewModel.initRepository(repository)
    }

    private fun setupRecyclerView() {
        adapter = ProductListAdapter(
            onProductClick = { product ->
                findNavController().navigate(
                    R.id.action_productByCategory_to_productDetail,
                    Bundle().apply { putInt("productId", product.id) }
                )
            },
            onFavoriteClick = { product ->
                viewModel.toggleFavorite(product)
            }
        )

        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@ProductByCategoryFragment.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshProducts()
        }
    }

    private fun setupClickListeners() {
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.currentCategory.observe(viewLifecycleOwner) { category ->
            binding.textCategoryTitle.text = category.replaceFirstChar { it.uppercase() }
        }

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
