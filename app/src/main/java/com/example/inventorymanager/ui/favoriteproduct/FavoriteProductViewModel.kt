package com.example.inventorymanager.ui.favoriteproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventorymanager.data.model.Product
import com.example.inventorymanager.data.repository.ProductRepository
import kotlinx.coroutines.launch

class FavoriteProductViewModel : ViewModel() {

    private lateinit var repository: ProductRepository

    val favoriteProducts: LiveData<List<Product>> get() =
        if (::repository.isInitialized) repository.getFavoriteProducts()
        else MutableLiveData()

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun initRepository(repo: ProductRepository) {
        repository = repo
    }

    fun removeFromFavorites(product: Product) {
        if (!::repository.isInitialized) return

        viewModelScope.launch {
            try {
                repository.removeFromFavorites(product.id)
            } catch (e: Exception) {
                _error.value = "Failed to remove from favorites: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
