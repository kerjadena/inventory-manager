package com.example.inventorymanager.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventorymanager.databinding.FragmentFavoriteProductBinding
import com.example.inventorymanager.ui.adapters.ProductAdapter
import com.example.inventorymanager.viewmodel.ProductViewModel

class FragmentFavoriteProduct : Fragment() {

    private var _binding: FragmentFavoriteProductBinding? = null
    private val binding get() = _binding!!
    private val productViewModel: ProductViewModel by activityViewModels()
    private lateinit var productAdapter: ProductAdapter

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
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter()
        binding.recyclerViewFavorites.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }
    }

    private fun observeViewModel() {
        productViewModel.getFavoriteProducts()
        productViewModel.favoriteProducts.observe(viewLifecycleOwner) { products ->
            if (products.isNullOrEmpty()) {
                binding.textViewNoFavorites.visibility = View.VISIBLE
                binding.recyclerViewFavorites.visibility = View.GONE
            } else {
                binding.textViewNoFavorites.visibility = View.GONE
                binding.recyclerViewFavorites.visibility = View.VISIBLE
                productAdapter.submitList(products)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FragmentFavoriteProduct()
    }
}