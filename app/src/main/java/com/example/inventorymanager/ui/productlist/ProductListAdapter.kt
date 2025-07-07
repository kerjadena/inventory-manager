package com.example.inventorymanager.ui.productlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.inventorymanager.R
import com.example.inventorymanager.data.model.Product
import com.example.inventorymanager.databinding.ItemProductBinding

class ProductListAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onFavoriteClick: (Product) -> Unit
) : ListAdapter<Product, ProductListAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                textTitle.text = product.title
                textPrice.text = "$${product.price}"
                textCategory.text = product.category
                textRating.text = "⭐ ${product.rating}"

                // Load product image
                Glide.with(imageProduct.context)
                    .load(product.imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(imageProduct)

                // Set favorite icon
                buttonFavorite.setImageResource(
                    if (product.isFavorite) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_border
                )

                // Set click listeners
                root.setOnClickListener { onProductClick(product) }
                buttonFavorite.setOnClickListener { onFavoriteClick(product) }
            }
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
