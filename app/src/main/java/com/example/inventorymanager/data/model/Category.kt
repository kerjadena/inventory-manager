package com.example.inventorymanager.data.model

data class Category(
    val slug: String,
    val name: String,
    val url: String
)

data class CategoryResponse(
    val categories: List<Category>
)
