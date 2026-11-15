package com.example.novoecommerce.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

data class DeliveryLocationResult(
    val latitude: Double,
    val longitude: Double,
    val street: String,
    val cep: String,
    val summary: String
)

object LocationHelper {
    @SuppressLint("MissingPermission")
    fun obterLocalizacaoEntrega(
        context: Context,
        onSuccess: (DeliveryLocationResult) -> Unit,
        onError: (String) -> Unit
    ) {
        val client = LocationServices.getFusedLocationProviderClient(context)
        client.lastLocation
            .addOnSuccessListener { lastLocation ->
                if (lastLocation != null) {
                    converterLocalizacao(context, lastLocation, onSuccess)
                } else {
                    client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                        .addOnSuccessListener { currentLocation ->
                            if (currentLocation != null) {
                                converterLocalizacao(context, currentLocation, onSuccess)
                            } else {
                                onError("Não foi possível obter a localização atual. Ative o GPS do aparelho.")
                            }
                        }
                        .addOnFailureListener {
                            onError("Erro ao obter localização: ${it.localizedMessage ?: "desconhecido"}")
                        }
                }
            }
            .addOnFailureListener {
                onError("Erro ao acessar o serviço de localização: ${it.localizedMessage ?: "desconhecido"}")
            }
    }

    private fun converterLocalizacao(
        context: Context,
        location: Location,
        onSuccess: (DeliveryLocationResult) -> Unit
    ) {
        val lat = location.latitude
        val lon = location.longitude
        val coords = String.format(Locale.US, "%.5f, %.5f", lat, lon)

        if (!Geocoder.isPresent()) {
            onSuccess(
                DeliveryLocationResult(
                    latitude = lat,
                    longitude = lon,
                    street = "Localização GPS ($coords)",
                    cep = "",
                    summary = "Coordenadas: $coords"
                )
            )
            return
        }

        val geocoder = Geocoder(context, Locale("pt", "BR"))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lon, 1) { addresses ->
                val addr = addresses.firstOrNull()
                onSuccess(montarResultado(lat, lon, coords, addr?.thoroughfare, addr?.subThoroughfare, addr?.locality, addr?.postalCode))
            }
        } else {
            @Suppress("DEPRECATION")
            val addresses = runCatching { geocoder.getFromLocation(lat, lon, 1) }.getOrNull()
            val addr = addresses?.firstOrNull()
            onSuccess(montarResultado(lat, lon, coords, addr?.thoroughfare, addr?.subThoroughfare, addr?.locality, addr?.postalCode))
        }
    }

    private fun montarResultado(
        lat: Double,
        lon: Double,
        coords: String,
        rua: String?,
        numero: String?,
        cidade: String?,
        cep: String?
    ): DeliveryLocationResult {
        val logradouro = listOfNotNull(rua, numero).joinToString(", ").ifBlank { "Localização atual ($coords)" }
        val cepLimpo = cep?.filter { it.isDigit() }.orEmpty()
        val resumo = listOfNotNull(cidade, "GPS: $coords").joinToString(" · ")
        return DeliveryLocationResult(
            latitude = lat,
            longitude = lon,
            street = logradouro,
            cep = cepLimpo,
            summary = resumo
        )
    }
}
