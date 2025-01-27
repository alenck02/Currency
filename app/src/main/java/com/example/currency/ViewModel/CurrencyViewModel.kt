package com.example.currency.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CurrencyViewModel : ViewModel() {
    var currencies = mutableStateOf(listOf("KRW", "USD"))
        private set

    var selectedCurrency = mutableStateOf("")
        private set

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