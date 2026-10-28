package com.linaresbar.appdenuncias.data.model

import com.google.gson.annotations.SerializedName

data class Denuncia(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("dni")
    val dni: String = "",

    @SerializedName("fecha")
    val fecha: String = "",

    @SerializedName("tipo")
    val tipo: String = "",

    @SerializedName("direccion")
    val direccion: String = "",

    @SerializedName("descripcion")
    val descripcion: String = "",

    @SerializedName("estado")
    val estado: Int = 1
)

data class PageResponse<T>(
    @SerializedName("content")
    val content: List<T> = emptyList(),

    @SerializedName("totalElements")
    val totalElements: Int = 0,

    @SerializedName("totalPages")
    val totalPages: Int = 0,

    @SerializedName("number")
    val number: Int = 0,

    @SerializedName("size")
    val size: Int = 0,

    @SerializedName("empty")
    val empty: Boolean = true
)
