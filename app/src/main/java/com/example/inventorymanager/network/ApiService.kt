package com.example.inventorymanager.network

import com.example.inventorymanager.model.Product
import com.example.inventorymanager.model.Category
import retrofit2.Response
import retrofit2.http.*

data class ProductResponse(
    val products: List<Product>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

interface ApiService {

    @GET("products")
    suspend fun getAllProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): Response<ProductResponse>

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") id: Int
    ): Response<Product>

    @GET("products/categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category") category: String
    ): Response<ProductResponse>

    @GET("products/search")
    suspend fun searchProduct(@Query("q") query: String): Response<ProductResponse>

    @POST("products/add")
    suspend fun addProduct(@Body product: Product): Response<Product>

    @PUT("products/{id}")
    suspend fun editProduct(@Path("id") id: Int, @Body product: Product): Response<Product>
}
