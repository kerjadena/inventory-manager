package com.example.inventorymanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val brand: String,
    val category: String,
    @SerializedName("thumbnail")
    val imageUrl: String? = null,
    val stock: Int = 0,
    val rating: Double = 0.0,
    val discountPercentage: Double = 0.0,
    var isFavorite: Boolean = false
)

data class ProductResponse(
    val products: List<Product>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

data class AddProductRequest(
    val title: String,
    val description: String,
    val price: Double,
    val brand: String,
    val category: String
)

data class UpdateProductRequest(
    val price: Double? = null,
    val title: String? = null,
    val description: String? = null,
    val brand: String? = null,
    val category: String? = null
)
