package com.example.inventorymanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventorymanager.database.AppDatabase
import com.example.inventorymanager.model.Product
import com.example.inventorymanager.network.RetrofitClient
import com.example.inventorymanager.util.NetworkUtil
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val productDao = AppDatabase.getDatabase(application).productDao()
    private val context = application.applicationContext

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _searchResults = MutableLiveData<List<Product>>()
    val searchResults: LiveData<List<Product>> = _searchResults

    private val _selectedProduct = MutableLiveData<Product>()
    val selectedProduct: LiveData<Product> = _selectedProduct

    private val _favoriteProducts = MutableLiveData<List<Product>>()
    val favoriteProducts: LiveData<List<Product>> = _favoriteProducts

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        getAllProducts(20, 0)
        getFavoriteProducts()
        getCategories()
    }

    fun getAllProducts(limit: Int, skip: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)

            if (!NetworkUtil.isNetworkAvailable(context)) {
                _error.postValue("Network error. Please check your connection.")
                _isLoading.postValue(false)
                return@launch
            }

            try {
                val response = RetrofitClient.apiService.getAllProducts(limit, skip)
                if (response.isSuccessful) {
                    response.body()?.let { productResponse ->
                        _products.postValue(productResponse.products)
                    }
                } else {
                    _error.postValue("Server error: ${response.message()}")
                }
            } catch (e: UnknownHostException) {
                _error.postValue("Network error. Please check your connection.")
            } catch (e: SocketTimeoutException) {
                _error.postValue("Connection timeout. Please try again.")
            } catch (e: Exception) {
                _error.postValue("Network error: ${e.message ?: "Unknown error"}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            if (!NetworkUtil.isNetworkAvailable(context)) {
                _error.postValue("Network error. Please check your connection.")
                return@launch
            }

            try {
                val response = RetrofitClient.apiService.addProduct(product)
                if (response.isSuccessful) {
                    getAllProducts(20, 0) // Refresh the list
                } else {
                    _error.postValue("Error adding product: ${response.message()}")
                }
            } catch (e: UnknownHostException) {
                _error.postValue("Network error. Please check your connection.")
            } catch (e: SocketTimeoutException) {
                _error.postValue("Connection timeout. Please try again.")
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message ?: "Unknown error"}")
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

    fun getFavoriteProducts() {
        viewModelScope.launch {
            try {
                val favorites = productDao.getAllFavoriteProducts()
                _favoriteProducts.postValue(favorites)
            } catch (e: Exception) {
                _error.postValue("Error fetching favorites: ${e.message}")
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.searchProduct(query)
                if (response.isSuccessful) {
                    response.body()?.let { searchResponse ->
                        _searchResults.postValue(searchResponse.products)
                    }
                } else {
                    _error.postValue("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Network error: ${e.message}")
            }
        }
    }

    fun getProductsByCategory(category: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getProductsByCategory(category)
                if (response.isSuccessful) {
                    response.body()?.let { categoryResponse ->
                        _products.postValue(categoryResponse.products)
                    }
                } else {
                    _error.postValue("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Network error: ${e.message}")
            }
        }
    }

    fun editProduct(id: Int, product: Product) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.editProduct(id, product)
                if (response.isSuccessful) {
                    getAllProducts(20, 0) // Refresh the list
                } else {
                    _error.postValue("Error editing product: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
            }
        }
    }

    private fun getCategories() {
        viewModelScope.launch {
            if (!NetworkUtil.isNetworkAvailable(context)) {
                _error.postValue("Network error. Please check your connection.")
                return@launch
            }

            try {
                val response = RetrofitClient.apiService.getCategories()
                if (response.isSuccessful) {
                    response.body()?.let { categories ->
                        // Extract category names from the direct array response
                        val categoryNames = categories.map { it.name }
                        _categories.postValue(categoryNames)
                    }
                } else {
                    _error.postValue("Error loading categories: ${response.message()}")
                }
            } catch (e: UnknownHostException) {
                _error.postValue("Network error. Please check your connection.")
            } catch (e: SocketTimeoutException) {
                _error.postValue("Connection timeout. Please try again.")
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message ?: "Unknown error"}")
            }
        }
    }
}
