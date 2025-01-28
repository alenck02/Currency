package com.example.currency.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CurrencyViewModel : ViewModel() {
    var currencies = mutableStateOf(listOf("KRW", "USD"))
        private set

    var currencyValues = mutableStateOf(mutableMapOf<String, String>().apply {
        currencies.value.forEach { this[it] = "0" }
    })
        private set

    var selectedCurrency = mutableStateOf(currencies.value.firstOrNull() ?: "KRW")
        private set

    fun updateCurrencyValue(currency: String, newValue: String) {
        currencyValues.value = currencyValues.value.toMutableMap().apply {
            this[currency] = newValue
        }
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
}