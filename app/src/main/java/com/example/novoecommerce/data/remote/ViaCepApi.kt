package com.example.novoecommerce.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class ViaCepResponse(
    val cep: String? = null,
    val logradouro: String? = null,
    val bairro: String? = null,
    val localidade: String? = null,
    val uf: String? = null,
    val erro: Boolean? = null
)

interface ViaCepService {
    @GET("ws/{cep}/json/")
    suspend fun buscarCep(@Path("cep") cep: String): ViaCepResponse
}

object ViaCepApi {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://viacep.com.br/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: ViaCepService = retrofit.create(ViaCepService::class.java)
}
