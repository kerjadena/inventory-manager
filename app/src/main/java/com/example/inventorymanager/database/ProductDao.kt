package com.example.inventorymanager.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.inventorymanager.model.Product

@Dao
interface ProductDao {

    @Insert
    suspend fun insertProduct(product: Product)

    @Query("SELECT * FROM favorite_products")
    suspend fun getAllFavoriteProducts(): List<Product>

    @Delete
    suspend fun deleteProduct(product: Product)
}
