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

    private val _exchangeRates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val exchangeRates: StateFlow<Map<String, Double>> = _exchangeRates

    private val _currencyValues = MutableStateFlow<Map<String, String>>(emptyMap())
    val currencyValues: StateFlow<Map<String, String>> = _currencyValues

    var selectedCurrency = mutableStateOf(currencies.value.firstOrNull() ?: "KRW")
        private set

    fun updateCurrencyValue(currency: String, value: String) {
        _currencyValues.value = _currencyValues.value.toMutableMap().apply {
            this[currency] = value
        }
    }

    var userInputValue = mutableStateOf("0")
        private set

    fun setUserInputValue(value: String) {
        userInputValue.value = value
    }

    fun resetAllCurrencyValues() {
        _currencyValues.value = _currencyValues.value.keys.associateWith { "0" }
        userInputValue.value = "0"
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

    val _availableCountries = MutableStateFlow<List<String>>(emptyList())
    val availableCountries: StateFlow<List<String>> = _availableCountries

    fun fetchLiveRates(base: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getRates(BuildConfig.KEY, base)
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        _exchangeRates.value = data.conversion_rates

                        _currencyValues.value = data.conversion_rates.mapValues {
                            String.format("%.5f", it.value)
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

    fun convertCurrencyWithFetch(fromCurrency: String, toCurrency: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getRates(BuildConfig.KEY, fromCurrency)
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        _exchangeRates.value = data.conversion_rates

                        _currencyValues.value = _currencyValues.value.mapKeys { it.key }.mapValues { (key, _) ->
                            data.conversion_rates[key]?.let { String.format("%.5f", it) } ?: "0"
                        }

                        val inputAmount = userInputValue.value.toDoubleOrNull() ?: 0.0
                        val exchangeRate = data.conversion_rates[toCurrency] ?: 0.0
                        val convertedValue = inputAmount * exchangeRate

                        Log.d("CONVERT", "From: $fromCurrency ($inputAmount) ➝ To: $toCurrency (Rate: $exchangeRate)")

                        updateCurrencyValue(toCurrency, String.format("%.5f", convertedValue))
                    }
                }
            } catch (e: Exception) {
                Log.e("CONVERT_ERR", "Conversion failed: ${e.message}")
            }
        }
    }
}