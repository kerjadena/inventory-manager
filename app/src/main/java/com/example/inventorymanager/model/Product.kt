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
    val stock: Int = 0
)
