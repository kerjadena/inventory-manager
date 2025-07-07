package com.example.inventorymanager.data.api

import com.example.inventorymanager.data.model.AddProductRequest
import com.example.inventorymanager.data.model.Product
import com.example.inventorymanager.data.model.ProductResponse
import com.example.inventorymanager.data.model.UpdateProductRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("products")
    suspend fun getAllProducts(
        @Query("limit") limit: Int = 30,
        @Query("skip") skip: Int = 0
    ): Response<ProductResponse>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<Product>

    @GET("products/categories")
    suspend fun getCategories(): Response<List<String>>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): Response<ProductResponse>

    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): Response<ProductResponse>

    @POST("products/add")
    suspend fun addProduct(@Body product: AddProductRequest): Response<Product>

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: UpdateProductRequest
    ): Response<Product>
}
