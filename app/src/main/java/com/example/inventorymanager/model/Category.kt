package com.example.inventorymanager.model

data class Category(
    val slug: String,
    val name: String,
    val url: String
)

data class CategoryResponse(
    val value: List<Category>,
    val count: Int? = null
)
