package com.example.currency.ViewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CurrencyViewModel : ViewModel() {
    var currencies = mutableStateOf(listOf("KRW", "USD"))
        private set

    private val _currencyValues = mutableStateOf(
        currencies.value.associateWith { "0" }
    )
    val currencyValues: State<Map<String, String>> = _currencyValues

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
        val exchangeRate = when (fromCurrency to toCurrency) {
            "KRW" to "USD" -> 0.00075
            "KRW" to "CAD" -> 0.00092
            "KRW" to "JPY" -> 0.084
            "KRW" to "EUR" -> 0.0007
            "USD" to "KRW" -> 1330.0
            "USD" to "CAD" -> 1.21
            "USD" to "JPY" -> 110.0
            "USD" to "EUR" -> 0.85
            "CAD" to "KRW" -> 1086.0
            "CAD" to "USD" -> 0.83
            "CAD" to "JPY" -> 90.0
            "CAD" to "EUR" -> 0.69
            "JPY" to "KRW" -> 11.9
            "JPY" to "USD" -> 0.0091
            "JPY" to "CAD" -> 0.011
            "JPY" to "EUR" -> 0.0076
            "EUR" to "KRW" -> 1428.0
            "EUR" to "USD" -> 1.18
            "EUR" to "CAD" -> 1.45
            "EUR" to "JPY" -> 131.0
            else -> null
        }

        exchangeRate?.let {
            updateCurrencyValue(fromCurrency, "0")
            val fromValue = currencyValues.value.getOrDefault(fromCurrency, "0").toDoubleOrNull() ?: 0.0
            val convertedValue = fromValue * it
            updateCurrencyValue(toCurrency, String.format("%.2f", convertedValue))
        }
    }
}