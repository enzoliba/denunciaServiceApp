package com.linaresbar.appdenuncias.data.api

import com.linaresbar.appdenuncias.data.model.Denuncia
import com.linaresbar.appdenuncias.data.model.PageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface DenunciaApiService {

    @POST("denuncias")
    suspend fun registrarDenuncia(@Body denuncia: Denuncia): Response<Denuncia>

    @GET("denuncias")
    suspend fun obtenerDenunciasPaginadas(
        @Query("offset") page: Int = 0,
        @Query("limit") limit: Int = 5,
        @Query("search") search: String? = null
    ): Response<PageResponse<Denuncia>>

    @GET("denuncias/{id}")
    suspend fun obtenerDenunciaPorId(@Path("id") id: Int): Response<Denuncia>

    @GET("denuncias/ciudadano/{dni}")
    suspend fun obtenerDenunciasPorCiudadanoPath(@Path("dni") dni: String): Response<List<Denuncia>>

    @GET("denuncias")
    suspend fun obtenerDenunciasPorDniQuery(
        @Query("search") dni: String,
        @Query("offset") page: Int = 0,
        @Query("limit") limit: Int = 100
    ): Response<PageResponse<Denuncia>>

    @PUT("denuncias/{id}")
    suspend fun atenderDenuncia(
        @Path("id") id: Int,
        @Body denuncia: Denuncia
    ): Response<Denuncia>
}
