package com.example.inventorymanager.ui.categorylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventorymanager.data.repository.ProductRepository
import kotlinx.coroutines.launch

class CategoryListViewModel : ViewModel() {

    private lateinit var repository: ProductRepository

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun initRepository(repo: ProductRepository) {
        repository = repo
        loadCategories()
    }

    private fun loadCategories() {
        if (!::repository.isInitialized) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = repository.getCategories()
                if (response.isSuccessful) {
                    _categories.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to load categories: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshCategories() {
        if (::repository.isInitialized) {
            loadCategories()
        }
    }
}
