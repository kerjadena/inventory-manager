package com.example.inventorymanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventorymanager.database.AppDatabase
import com.example.inventorymanager.model.Product
import com.example.inventorymanager.network.RetrofitClient
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val productDao = AppDatabase.getDatabase(application).productDao()

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _selectedProduct = MutableLiveData<Product>()
    val selectedProduct: LiveData<Product> = _selectedProduct

    private val _favoriteProducts = MutableLiveData<List<Product>>()
    val favoriteProducts: LiveData<List<Product>> = _favoriteProducts

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        getAllProducts(20, 0)
        getFavoriteProducts()
    }

    fun getAllProducts(limit: Int, skip: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getAllProducts(limit, skip)
                if (response.isSuccessful) {
                    response.body()?.let { productResponse ->
                        _products.postValue(productResponse.products)
                    }
                } else {
                    _error.postValue("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Network error: ${e.message}")
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.addProduct(product)
                if (response.isSuccessful) {
                    getAllProducts(20, 0) // Refresh the list
                } else {
                    _error.postValue("Error adding product: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
            }
        }
    }

    fun addToFavorites(product: Product) {
        viewModelScope.launch {
            try {
                productDao.insertProduct(product)
                getFavoriteProducts()
            } catch (e: Exception) {
                _error.postValue("Error adding to favorites: ${e.message}")
            }
        }
    }

    fun removeFromFavorites(product: Product) {
        viewModelScope.launch {
            try {
                productDao.deleteProduct(product)
                getFavoriteProducts()
            } catch (e: Exception) {
                _error.postValue("Error removing from favorites: ${e.message}")
            }
        }
    }

    fun setSelectedProduct(product: Product) {
        _selectedProduct.value = product
    }

    private fun getFavoriteProducts() {
        viewModelScope.launch {
            try {
                val favorites = productDao.getAllFavoriteProducts()
                _favoriteProducts.postValue(favorites)
            } catch (e: Exception) {
                _error.postValue("Error fetching favorites: ${e.message}")
            }
        }
    }
}
