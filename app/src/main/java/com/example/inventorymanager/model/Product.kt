package com.example.inventorymanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "favorite_products")
data class Product(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val brand: String,
    val category: String,
    @SerializedName("thumbnail")
    val imageUrl: String = "",
    val rating: Double = 0.0,
    val stock: Int = 0,
    // Handle the images array - we'll take the first image or use thumbnail
    @SerializedName("images")
    val images: List<String>? = null,
    // Handle tags array
    val tags: List<String>? = null,
    // Handle additional fields that might cause parsing issues
    val sku: String? = null,
    val weight: Int? = null,
    val discountPercentage: Double? = null
) {
    // Helper function to get the best image URL
    fun getBestImageUrl(): String {
        return when {
            !images.isNullOrEmpty() -> images.first()
            imageUrl.isNotEmpty() -> imageUrl
            else -> ""
        }
    }
}
