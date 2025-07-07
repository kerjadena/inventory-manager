package com.example.inventorymanager.ui.productdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.inventorymanager.R
import com.example.inventorymanager.data.local.ProductDatabase
import com.example.inventorymanager.data.network.NetworkModule
import com.example.inventorymanager.data.repository.ProductRepository
import com.example.inventorymanager.databinding.FragmentProductDetailBinding

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProductDetailViewModel
    private var productId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get productId from arguments with proper null checking
        productId = arguments?.getInt("productId", 0) ?: 0

        if (productId == 0) {
            Toast.makeText(context, "Invalid product ID", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        setupViewModel()
        setupClickListeners()
        observeViewModel()

        // Load product data
        viewModel.loadProduct(productId)
    }

    private fun setupViewModel() {
        try {
            val database = ProductDatabase.getDatabase(requireContext())
            val repository = ProductRepository(NetworkModule.apiService, database.productDao())

            // Direct ViewModelProvider without custom factory
            viewModel = ViewModelProvider(this)[ProductDetailViewModel::class.java]
            viewModel.initRepository(repository)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to initialize: ${e.message}", Toast.LENGTH_LONG).show()
            findNavController().navigateUp()
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().navigateUp()
            }

            buttonFavorite.setOnClickListener {
                viewModel.toggleFavorite()
            }

            buttonEdit.setOnClickListener {
                try {
                    findNavController().navigate(
                        R.id.action_productDetail_to_productEdit,
                        Bundle().apply { putInt("productId", productId) }
                    )
                } catch (e: Exception) {
                    Toast.makeText(context, "Cannot navigate to edit", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.product.observe(viewLifecycleOwner) { product ->
            if (product != null) {
                bindProductData(product)
                binding.contentLayout.visibility = View.VISIBLE
            }
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            binding.buttonFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_border
            )
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (!isLoading && viewModel.product.value != null) {
                binding.contentLayout.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                // If there's an error loading the product, navigate back
                if (viewModel.product.value == null) {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun bindProductData(product: com.example.inventorymanager.data.model.Product) {
        binding.apply {
            textTitle.text = product.title
            textDescription.text = product.description
            textPrice.text = "$${product.price}"
            textBrand.text = product.brand ?: "Unknown"
            textCategory.text = product.category
            textStock.text = "Stock: ${product.stock}"
            textRating.text = "⭐ ${product.rating}"

            // Handle discount display
            if (product.discountPercentage > 0) {
                textDiscount.text = "${product.discountPercentage}% OFF"
                textDiscount.visibility = View.VISIBLE
            } else {
                textDiscount.visibility = View.GONE
            }

            // Load image with Glide safely
            try {
                Glide.with(requireContext())
                    .load(product.imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(imageProduct)
            } catch (e: Exception) {
                imageProduct.setImageResource(R.drawable.ic_placeholder)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
