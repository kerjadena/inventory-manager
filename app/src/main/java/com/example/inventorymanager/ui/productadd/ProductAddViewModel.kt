package com.example.inventorymanager.ui.productadd

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventorymanager.data.model.AddProductRequest
import com.example.inventorymanager.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductAddViewModel : ViewModel() {

    private lateinit var repository: ProductRepository

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    fun initRepository(repo: ProductRepository) {
        repository = repo
        loadCategories()
    }

    private fun loadCategories() {
        if (!::repository.isInitialized) return

        viewModelScope.launch {
            try {
                val response = repository.getCategories()
                if (response.isSuccessful) {
                    _categories.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Silently fail for categories, not critical for add product
            }
        }
    }

    fun addProduct(
        title: String,
        description: String,
        price: String,
        brand: String,
        category: String
    ) {
        if (!::repository.isInitialized) return

        if (!validateInputs(title, description, price, brand, category)) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _success.value = false

            try {
                val priceDouble = price.toDoubleOrNull() ?: 0.0
                val productRequest = AddProductRequest(
                    title = title.trim(),
                    description = description.trim(),
                    price = priceDouble,
                    brand = brand.trim(),
                    category = category.trim()
                )

                val response = repository.addProduct(productRequest)
                if (response.isSuccessful) {
                    _success.value = true
                } else {
                    _error.value = "Failed to add product: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun validateInputs(
        title: String,
        description: String,
        price: String,
        brand: String,
        category: String
    ): Boolean {
        when {
            title.isBlank() -> {
                _error.value = "Title is required"
                return false
            }
            description.isBlank() -> {
                _error.value = "Description is required"
                return false
            }
            price.isBlank() -> {
                _error.value = "Price is required"
                return false
            }
            price.toDoubleOrNull() == null || price.toDouble() <= 0 -> {
                _error.value = "Please enter a valid price"
                return false
            }
            brand.isBlank() -> {
                _error.value = "Brand is required"
                return false
            }
            category.isBlank() -> {
                _error.value = "Category is required"
                return false
            }
        }
        return true
    }

    fun clearError() {
        _error.value = null
    }
}
