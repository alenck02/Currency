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

    val currencyToCountryName = mapOf(
        "AED" to "아랍에미리트",
        "AFN" to "아프가니스탄",
        "ALL" to "알바니아",
        "AMD" to "아르메니아",
        "ANG" to "네덜란드령 안틸레스",
        "AOA" to "앙골라",
        "ARS" to "아르헨티나",
        "AUD" to "호주",
        "AWG" to "아루바",
        "AZN" to "아제르바이잔",
        "BAM" to "보스니아 헤르체고비나",
        "BBD" to "바베이도스",
        "BDT" to "방글라데시",
        "BGN" to "불가리아",
        "BHD" to "바레인",
        "BIF" to "부룬디",
        "BMD" to "버뮤다",
        "BND" to "브루나이",
        "BOB" to "볼리비아",
        "BRL" to "브라질",
        "BSD" to "바하마",
        "BTN" to "부탄",
        "BWP" to "보츠와나",
        "BYN" to "벨라루스",
        "BZD" to "벨리즈",
        "CAD" to "캐나다",
        "CDF" to "콩고 민주공화국",
        "CHF" to "스위스",
        "CLP" to "칠레",
        "CNY" to "중국",
        "COP" to "콜롬비아",
        "CRC" to "코스타리카",
        "CUP" to "쿠바",
        "CVE" to "카보베르데",
        "CZK" to "체코",
        "DJF" to "지부티",
        "DKK" to "덴마크",
        "DOP" to "도미니카 공화국",
        "DZD" to "알제리",
        "EGP" to "이집트",
        "ERN" to "에리트레아",
        "ETB" to "에티오피아",
        "EUR" to "유럽연합",
        "FJD" to "피지",
        "FKP" to "포클랜드 제도",
        "FOK" to "페로 제도",
        "GBP" to "영국",
        "GEL" to "조지아",
        "GGP" to "건지 섬",
        "GHS" to "가나",
        "GIP" to "지브롤터",
        "GMD" to "감비아",
        "GNF" to "기니",
        "GTQ" to "과테말라",
        "GYD" to "가이아나",
        "HKD" to "홍콩",
        "HNL" to "온두라스",
        "HRK" to "크로아티아",
        "HTG" to "아이티",
        "HUF" to "헝가리",
        "IDR" to "인도네시아",
        "ILS" to "이스라엘",
        "IMP" to "맨섬",
        "INR" to "인도",
        "IQD" to "이라크",
        "IRR" to "이란",
        "ISK" to "아이슬란드",
        "JEP" to "저지 섬",
        "JMD" to "자메이카",
        "JOD" to "요르단",
        "JPY" to "일본",
        "KES" to "케냐",
        "KGS" to "키르기스스탄",
        "KHR" to "캄보디아",
        "KID" to "키리바시",
        "KMF" to "코모로",
        "KRW" to "대한민국",
        "KWD" to "쿠웨이트",
        "KYD" to "케이맨 제도",
        "KZT" to "카자흐스탄",
        "LAK" to "라오스",
        "LBP" to "레바논",
        "LKR" to "스리랑카",
        "LRD" to "라이베리아",
        "LSL" to "레소토",
        "LYD" to "리비아",
        "MAD" to "모로코",
        "MDL" to "몰도바",
        "MGA" to "마다가스카르",
        "MKD" to "북마케도니아",
        "MMK" to "미얀마",
        "MNT" to "몽골",
        "MOP" to "마카오",
        "MRU" to "모리타니",
        "MUR" to "모리셔스",
        "MVR" to "몰디브",
        "MWK" to "말라위",
        "MXN" to "멕시코",
        "MYR" to "말레이시아",
        "MZN" to "모잠비크",
        "NAD" to "나미비아",
        "NGN" to "나이지리아",
        "NIO" to "니카라과",
        "NOK" to "노르웨이",
        "NPR" to "네팔",
        "NZD" to "뉴질랜드",
        "OMR" to "오만",
        "PAB" to "파나마",
        "PEN" to "페루",
        "PGK" to "파푸아뉴기니",
        "PHP" to "필리핀",
        "PKR" to "파키스탄",
        "PLN" to "폴란드",
        "PYG" to "파라과이",
        "QAR" to "카타르",
        "RON" to "루마니아",
        "RSD" to "세르비아",
        "RUB" to "러시아",
        "RWF" to "르완다",
        "SAR" to "사우디아라비아",
        "SBD" to "솔로몬 제도",
        "SCR" to "세이셸",
        "SDG" to "수단",
        "SEK" to "스웨덴",
        "SGD" to "싱가포르",
        "SHP" to "세인트헬레나",
        "SLE" to "시에라리온",
        "SLL" to "시에라리온",
        "SOS" to "소말리아",
        "SRD" to "수리남",
        "SSP" to "남수단",
        "STN" to "상투메 프린시페",
        "SYP" to "시리아",
        "SZL" to "에스와티니",
        "THB" to "태국",
        "TJS" to "타지키스탄",
        "TMT" to "투르크메니스탄",
        "TND" to "튀니지",
        "TOP" to "통가",
        "TRY" to "튀르키예",
        "TTD" to "트리니다드 토바고",
        "TVD" to "투발루",
        "TWD" to "대만",
        "TZS" to "탄자니아",
        "UAH" to "우크라이나",
        "UGX" to "우간다",
        "USD" to "미국",
        "UYU" to "우루과이",
        "UZS" to "우즈베키스탄",
        "VES" to "베네수엘라",
        "VND" to "베트남",
        "VUV" to "바누아투",
        "WST" to "사모아",
        "XAF" to "중앙아프리카 CFA",
        "XCD" to "동카리브 달러",
        "XCG" to "카리브 길더",
        "XDR" to "특별인출권",
        "XOF" to "서아프리카 CFA",
        "XPF" to "CFP 프랑",
        "YER" to "예멘",
        "ZAR" to "남아프리카 공화국",
        "ZMW" to "잠비아",
        "ZWL" to "짐바브웨"
    )

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

                        _currencyValues.value = data.conversion_rates.mapValues { "0" }

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

                        val inputAmount = userInputValue.value.toDoubleOrNull() ?: 0.0
                        val exchangeRate = data.conversion_rates[toCurrency] ?: 0.0
                        val convertedValue = inputAmount * exchangeRate

                        updateCurrencyValue(toCurrency, String.format("%.5f", convertedValue))
                    }
                }
            } catch (e: Exception) {
                Log.e("CONVERT_ERR", "Conversion failed: ${e.message}")
            }
        }
    }
}