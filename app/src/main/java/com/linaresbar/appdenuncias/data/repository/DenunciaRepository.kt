package com.linaresbar.appdenuncias.data.repository

import com.linaresbar.appdenuncias.data.api.RetrofitClient
import com.linaresbar.appdenuncias.data.model.Denuncia
import com.linaresbar.appdenuncias.data.model.PageResponse

class DenunciaRepository {

    suspend fun registrarDenuncia(denuncia: Denuncia): Result<Denuncia> {
        return try {
            val response = RetrofitClient.getApiService().registrarDenuncia(denuncia)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al registrar denuncia: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión con la API: ${e.localizedMessage}"))
        }
    }

    suspend fun obtenerDenunciasPaginadas(page: Int, limit: Int = 5): Result<PageResponse<Denuncia>> {
        return try {
            val response = RetrofitClient.getApiService().obtenerDenunciasPaginadas(page = page, limit = limit)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error de la API (${response.code()}): ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun obtenerDenunciaPorId(id: Int): Result<Denuncia> {
        return try {
            val response = RetrofitClient.getApiService().obtenerDenunciaPorId(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Denuncia no encontrada (ID: $id)"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun obtenerDenunciasPorCiudadano(dni: String): Result<List<Denuncia>> {
        return try {
            // First try GET /denuncias/ciudadano/{dni}
            val responsePath = RetrofitClient.getApiService().obtenerDenunciasPorCiudadanoPath(dni)
            if (responsePath.isSuccessful && responsePath.body() != null) {
                return Result.success(responsePath.body()!!)
            }

            // Fallback to GET /denuncias?search={dni}
            val responseQuery = RetrofitClient.getApiService().obtenerDenunciasPorDniQuery(dni = dni)
            if (responseQuery.isSuccessful && responseQuery.body() != null) {
                return Result.success(responseQuery.body()!!.content)
            }

            Result.failure(Exception("No se encontraron denuncias para el DNI: $dni"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun atenderDenuncia(denuncia: Denuncia): Result<Denuncia> {
        val id = denuncia.id ?: return Result.failure(Exception("ID de denuncia no válido"))
        return try {
            val denunciaAtendida = denuncia.copy(estado = 0)
            val response = RetrofitClient.getApiService().atenderDenuncia(id, denunciaAtendida)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.isSuccessful) {
                Result.success(denunciaAtendida)
            } else {
                Result.failure(Exception("Error al atender denuncia (${response.code()}): ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.localizedMessage}"))
        }
    }
}
