package com.example.inventorymanager.ui.productdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventorymanager.data.model.Product
import com.example.inventorymanager.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductDetailViewModel : ViewModel() {

    private lateinit var repository: ProductRepository

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun initRepository(repo: ProductRepository) {
        repository = repo
    }

    fun loadProduct(productId: Int) {
        if (!::repository.isInitialized) {
            _error.value = "Repository not initialized"
            return
        }

        if (productId <= 0) {
            _error.value = "Invalid product ID"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = repository.getProductById(productId)
                if (response.isSuccessful) {
                    val productData = response.body()
                    if (productData != null) {
                        _product.value = productData
                        checkFavoriteStatus(productData.id)
                    } else {
                        _error.value = "Product not found"
                    }
                } else {
                    _error.value = "Failed to load product: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.localizedMessage ?: e.message ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun checkFavoriteStatus(productId: Int) {
        if (!::repository.isInitialized) return

        viewModelScope.launch {
            try {
                _isFavorite.value = repository.isProductFavorite(productId)
            } catch (e: Exception) {
                // Don't show error for favorite check, just default to false
                _isFavorite.value = false
            }
        }
    }

    fun toggleFavorite() {
        if (!::repository.isInitialized) {
            _error.value = "Repository not initialized"
            return
        }

        _product.value?.let { product ->
            viewModelScope.launch {
                try {
                    repository.toggleFavorite(product)
                    _isFavorite.value = !(_isFavorite.value ?: false)
                } catch (e: Exception) {
                    _error.value = "Failed to update favorite: ${e.localizedMessage ?: e.message ?: "Unknown error"}"
                }
            }
        } ?: run {
            _error.value = "No product loaded"
        }
    }
}
