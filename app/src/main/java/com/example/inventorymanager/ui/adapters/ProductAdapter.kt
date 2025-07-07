package com.example.inventorymanager.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.inventorymanager.R
import com.example.inventorymanager.databinding.ItemProductBinding
import com.example.inventorymanager.model.Product

class ProductAdapter(
    private val onItemClick: (Product) -> Unit = {},
    private val onFavoriteClick: (Product) -> Unit = {}
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                textProductName.text = product.title
                textProductPrice.text = "$ ${product.price}"
                textProductDescription.text = product.description

                // Load product image using the new helper function
                val imageUrl = product.getBestImageUrl()
                if (imageUrl.isNotEmpty()) {
                    Glide.with(itemView.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(imageProduct)
                } else {
                    imageProduct.setImageResource(R.drawable.ic_launcher_foreground)
                }

                // Set click listeners
                root.setOnClickListener { onItemClick(product) }
                btnFavorite.setOnClickListener { onFavoriteClick(product) }
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
