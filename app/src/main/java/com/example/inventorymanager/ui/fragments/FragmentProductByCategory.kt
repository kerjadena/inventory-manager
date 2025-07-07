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

class FragmentProductByCategory : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private val productViewModel: ProductViewModel by activityViewModels()
    private lateinit var productAdapter: ProductAdapter
    private var category: String? = null

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

        category = arguments?.getString("category")

        setupRecyclerView()
        setupObservers()

        category?.let {
            productViewModel.getProductsByCategory(it)
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onItemClick = { product ->
                productViewModel.setSelectedProduct(product)
                findNavController().navigate(R.id.action_productsByCategory_to_productDetail)
            },
            onFavoriteClick = { product ->
                productViewModel.addToFavorites(product)
                // Safe context check to prevent IllegalException
                context?.let { ctx ->
                    Toast.makeText(ctx, "Added to favorites", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.recyclerView.apply {
            // Use context from binding root instead of requireContext()
            layoutManager = LinearLayoutManager(binding.root.context)
            adapter = productAdapter
        }
    }

    private fun setupObservers() {
        productViewModel.products.observe(viewLifecycleOwner) { products ->
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
