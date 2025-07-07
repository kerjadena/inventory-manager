package com.example.inventorymanager.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.inventorymanager.data.model.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM products WHERE isFavorite = 1")
    fun getFavoriteProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Int)

    @Query("UPDATE products SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)
}
