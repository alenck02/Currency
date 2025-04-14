package com.example.currency.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currency.API.RetrofitClient
import com.example.currency.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CurrencyViewModel : ViewModel() {
    var currencies = mutableStateOf(listOf("KRW", "USD"))
        private set

    private val _currencyValues = MutableStateFlow<Map<String, String>>(emptyMap())
    val currencyValues: MutableStateFlow<Map<String, String>> = _currencyValues

    var selectedCurrency = mutableStateOf(currencies.value.firstOrNull() ?: "KRW")
        private set

    fun updateCurrencyValue(currency: String, value: String) {
        _currencyValues.value = _currencyValues.value.toMutableMap().apply {
            this[currency] = value
        }
    }

    fun resetAllCurrencyValues() {
        _currencyValues.value = _currencyValues.value.keys.associateWith { "0" }
    }

    fun setSelectedCurrency(currency: String) {
        selectedCurrency.value = currency
    }

    fun updateCurrency(oldCurrency: String, newCurrency: String) {
        currencies.value = currencies.value.map { if (it == oldCurrency) newCurrency else it }
    }

    fun swapCurrencies(currency1: String, currency2: String) {
        currencies.value = currencies.value.map {
            when (it) {
                currency1 -> currency2
                currency2 -> currency1
                else -> it
            }
        }
    }

    fun canSwap(currency: String): Boolean {
        return currencies.value.contains(currency)
    }

    fun convertCurrency(fromCurrency: String, toCurrency: String) {
        val exchangeRate = currencyValues.value[toCurrency]?.toDoubleOrNull()

        exchangeRate?.let {
            val fromValue = currencyValues.value[fromCurrency]?.toDoubleOrNull() ?: 0.0
            val convertedValue = fromValue * it
            updateCurrencyValue(toCurrency, String.format("%.2f", convertedValue))
        }
    }

    val _availableCountries = MutableStateFlow<List<String>>(emptyList())
    val availableCountries: StateFlow<List<String>> = _availableCountries

    fun fetchLiveRates(base: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getRates(BuildConfig.KEY, base)
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        _currencyValues.value = data.conversion_rates.mapValues {
                            String.format("%.2f", it.value)
                        }

                        _availableCountries.value = data.conversion_rates.keys.toList()
                    }
                } else {
                    Log.e("API_ERROR", "API Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Exception: ${e.message}")
            }
        }
    }
}