package com.example.inventorymanager.ui.productbycategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventorymanager.data.model.Category
import com.example.inventorymanager.data.model.Product
import com.example.inventorymanager.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductByCategoryViewModel : ViewModel() {

    private lateinit var repository: ProductRepository

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _currentCategory = MutableLiveData<Category>()
    val currentCategory: LiveData<Category> = _currentCategory

    fun initRepository(repo: ProductRepository) {
        repository = repo
    }

    fun loadProductsByCategory(category: Category) {
        if (!::repository.isInitialized) return

        _currentCategory.value = category

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = repository.getProductsByCategory(category)
                if (response.isSuccessful) {
                    _products.value = response.body()?.products ?: emptyList()
                } else {
                    _error.value = "Failed to load products: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(product: Product) {
        if (!::repository.isInitialized) return

        viewModelScope.launch {
            try {
                repository.toggleFavorite(product)
                // Update the product in the current list
                val updatedProducts = _products.value?.map {
                    if (it.id == product.id) it.copy(isFavorite = !it.isFavorite) else it
                }
                _products.value = updatedProducts ?: emptyList()
            } catch (e: Exception) {
                _error.value = "Failed to update favorite: ${e.message}"
            }
        }
    }

    fun refreshProducts() {
        _currentCategory.value?.let {
            if (::repository.isInitialized) {
                loadProductsByCategory(it)
            }
        }
    }
}
