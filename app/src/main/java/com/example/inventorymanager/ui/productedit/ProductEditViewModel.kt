package com.example.inventorymanager.ui.productedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventorymanager.data.model.Product
import com.example.inventorymanager.data.model.UpdateProductRequest
import com.example.inventorymanager.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductEditViewModel : ViewModel() {

    private lateinit var repository: ProductRepository

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product

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

    fun loadProduct(productId: Int) {
        if (!::repository.isInitialized) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = repository.getProductById(productId)
                if (response.isSuccessful) {
                    _product.value = response.body()
                } else {
                    _error.value = "Failed to load product: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
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
                // Silently fail for categories, not critical for edit product
            }
        }
    }

    fun updateProduct(
        productId: Int,
        title: String?,
        description: String?,
        price: String?,
        brand: String?,
        category: String?
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
                val updateRequest = UpdateProductRequest(
                    title = if (title.isNullOrBlank()) null else title.trim(),
                    description = if (description.isNullOrBlank()) null else description.trim(),
                    price = price?.toDoubleOrNull(),
                    brand = if (brand.isNullOrBlank()) null else brand.trim(),
                    category = if (category.isNullOrBlank()) null else category.trim()
                )

                val response = repository.updateProduct(productId, updateRequest)
                if (response.isSuccessful) {
                    _success.value = true
                    _product.value = response.body()
                } else {
                    _error.value = "Failed to update product: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun validateInputs(
        title: String?,
        description: String?,
        price: String?,
        brand: String?,
        category: String?
    ): Boolean {
        price?.let {
            if (it.isNotBlank() && (it.toDoubleOrNull() == null || it.toDouble() <= 0)) {
                _error.value = "Please enter a valid price"
                return false
            }
        }
        return true
    }

    fun clearError() {
        _error.value = null
    }
}
