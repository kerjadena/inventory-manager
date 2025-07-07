package com.example.inventorymanager.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.inventorymanager.R
import com.example.inventorymanager.databinding.FragmentProductDetailBinding
import com.example.inventorymanager.viewmodel.ProductViewModel

class FragmentProductDetail : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val productViewModel: ProductViewModel by activityViewModels()

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
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        productViewModel.selectedProduct.observe(viewLifecycleOwner) { product ->
            product?.let {
                binding.apply {
                    textProductTitle.text = it.title
                    textProductDescription.text = it.description
                    textProductPrice.text = "$ ${it.price}"
                    textProductBrand.text = "Brand: ${it.brand}"
                    textProductCategory.text = "Category: ${it.category}"
                    textProductRating.text = "Rating: ${it.rating}/5"
                    textProductStock.text = "Stock: ${it.stock}"

                    // Load product image with safe context check
                    if (it.getBestImageUrl().isNotEmpty()) {
                        context?.let { ctx ->
                            Glide.with(ctx)
                                .load(it.getBestImageUrl())
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .error(R.drawable.ic_launcher_foreground)
                                .into(imageProductDetail)
                        }
                    } else {
                        imageProductDetail.setImageResource(R.drawable.ic_launcher_foreground)
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAddToFavorites.setOnClickListener {
            productViewModel.selectedProduct.value?.let { product ->
                productViewModel.addToFavorites(product)
            }
        }

        binding.btnEditProduct.setOnClickListener {
            findNavController().navigate(R.id.action_productDetail_to_editProduct)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
