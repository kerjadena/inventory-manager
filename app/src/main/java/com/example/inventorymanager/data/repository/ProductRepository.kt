package com.example.inventorymanager.data.repository

import androidx.lifecycle.LiveData
import com.example.inventorymanager.data.api.ApiService
import com.example.inventorymanager.data.local.ProductDao
import com.example.inventorymanager.data.model.AddProductRequest
import com.example.inventorymanager.data.model.Category
import com.example.inventorymanager.data.model.Product
import com.example.inventorymanager.data.model.ProductResponse
import com.example.inventorymanager.data.model.UpdateProductRequest
import retrofit2.Response

class ProductRepository(
    private val apiService: ApiService,
    private val productDao: ProductDao
) {

    suspend fun getAllProducts(limit: Int = 30, skip: Int = 0): Response<ProductResponse> {
        return apiService.getAllProducts(limit, skip)
    }

    suspend fun getProductById(id: Int): Response<Product> {
        return apiService.getProductById(id)
    }

    suspend fun getCategories(): Response<List<Category>> {
        return apiService.getCategories()
    }

    suspend fun getProductsByCategory(category: Category): Response<ProductResponse> {
        return apiService.getProductsByCategory(category.slug)
    }

    suspend fun searchProducts(query: String): Response<ProductResponse> {
        return apiService.searchProducts(query)
    }

    suspend fun addProduct(product: AddProductRequest): Response<Product> {
        return apiService.addProduct(product)
    }

    suspend fun updateProduct(id: Int, product: UpdateProductRequest): Response<Product> {
        return apiService.updateProduct(id, product)
    }

    // Local database operations for favorites
    fun getFavoriteProducts(): LiveData<List<Product>> {
        return productDao.getFavoriteProducts()
    }

    suspend fun addToFavorites(product: Product) {
        val favoriteProduct = product.copy(isFavorite = true)
        productDao.insertProduct(favoriteProduct)
    }

    suspend fun removeFromFavorites(productId: Int) {
        productDao.updateFavoriteStatus(productId, false)
        productDao.deleteProductById(productId)
    }

    suspend fun toggleFavorite(product: Product) {
        if (product.isFavorite) {
            removeFromFavorites(product.id)
        } else {
            addToFavorites(product)
        }
    }

    suspend fun isProductFavorite(productId: Int): Boolean {
        return productDao.getProductById(productId)?.isFavorite ?: false
    }
}
