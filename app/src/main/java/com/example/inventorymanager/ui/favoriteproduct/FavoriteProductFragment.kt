package com.example.inventorymanager.ui.favoriteproduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventorymanager.R
import com.example.inventorymanager.data.local.ProductDatabase
import com.example.inventorymanager.data.network.NetworkModule
import com.example.inventorymanager.data.repository.ProductRepository
import com.example.inventorymanager.databinding.FragmentFavoriteProductBinding
import com.example.inventorymanager.ui.productlist.ProductListAdapter

class FavoriteProductFragment : Fragment() {

    private var _binding: FragmentFavoriteProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FavoriteProductViewModel
    private lateinit var adapter: ProductListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupViewModel() {
        val database = ProductDatabase.getDatabase(requireContext())
        val repository = ProductRepository(NetworkModule.apiService, database.productDao())

        // Direct ViewModelProvider without custom factory
        viewModel = ViewModelProvider(this)[FavoriteProductViewModel::class.java]
        viewModel.initRepository(repository)
    }

    private fun setupRecyclerView() {
        adapter = ProductListAdapter(
            onProductClick = { product ->
                findNavController().navigate(
                    R.id.action_favoriteProduct_to_productDetail,
                    Bundle().apply { putInt("productId", product.id) }
                )
            },
            onFavoriteClick = { product ->
                viewModel.removeFromFavorites(product)
            }
        )

        binding.recyclerViewFavorites.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@FavoriteProductFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.favoriteProducts.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
            binding.textEmpty.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
