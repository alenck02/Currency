package com.example.currency

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.currency.ViewModel.CurrencyViewModel
import com.example.currency.ui.theme.CurrencyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currencyViewModel: CurrencyViewModel = viewModel()
            CurrencyTheme {
                AppNavigation(currencyViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: CurrencyViewModel) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "main") {
        composable("main") { uiPreview(navController, viewModel) }
        composable("countrySelection/{currencyToReplace}") { backStackEntry ->
            val currencyToReplace = backStackEntry.arguments?.getString("currencyToReplace") ?: "KRW"
            CountrySelectionScreen(navController, viewModel, currencyToReplace)
        }
    }
}

@Composable
fun uiPreview(navController: NavHostController, viewModel: CurrencyViewModel) {
    val selectedCurrency by remember { viewModel.selectedCurrency }

    LaunchedEffect(Unit) {
        viewModel.fetchLiveRates(selectedCurrency)
    }

    Column {
        val currencies = viewModel.currencies.value

        if (currencies.isNotEmpty()) {
            viewModel.setSelectedCurrency(currencies.first())
        }

        currencies.forEachIndexed { index, currency ->
            CountryItem(currency, navController, viewModel, addTopPadding = index == 0)

            if (index < currencies.size - 1) {
                Divider()
            }
        }
        currencyInfo(viewModel)
        NumberButtons(viewModel)
    }
}

@Composable
fun CountryItem(currency: String, navController: NavHostController, viewModel: CurrencyViewModel, addTopPadding: Boolean) {
    val currencyValues by viewModel.currencyValues.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency
    val userInputValue by viewModel.userInputValue

    val isSelected = selectedCurrency == currency
    val displayValue = if (isSelected) userInputValue else currencyValues[currency] ?: "0"

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val itemHeight = screenHeight * 0.2f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 10.dp, end = 10.dp)
            .then(if (addTopPadding) Modifier.padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            ) else Modifier)
            .height(itemHeight)
            .clickable { navController.navigate("countrySelection/$currency") },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currency,
            fontSize = 28.sp
        )
        Text(
            text = displayValue,
            fontSize = 28.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountrySelectionScreen(navController: NavHostController, viewModel: CurrencyViewModel, currencyToReplace: String) {
    val countryList by viewModel.availableCountries.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
    ) {
        Row {
            var lastClickTime by remember { mutableStateOf(0L) }

            Button(
                onClick = {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClickTime > 1000) {
                        lastClickTime = currentTime
                        navController.popBackStack()
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                modifier = Modifier.wrapContentSize()
            ) {
                Image(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "backToMain"
                )
            }

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("국가 검색", color = Color.White) },
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    containerColor = colorResource(id = R.color.skyBlue)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        val filteredCountries = countryList
            .filter { currencyCode ->
                val countryName = viewModel.currencyToCountryName[currencyCode] ?: currencyCode
                countryName.contains(searchQuery, ignoreCase = true) || currencyCode.contains(searchQuery, ignoreCase = true)
            }
            .sortedBy { currencyCode ->
                viewModel.currencyToCountryName[currencyCode] ?: currencyCode
            }

        LazyColumn {
            items(filteredCountries) { currencyCode ->
                val countryName = viewModel.currencyToCountryName[currencyCode] ?: currencyCode
                CountryRow(countryName, currencyCode) {
                    if (viewModel.canSwap(currencyCode)) {
                        viewModel.swapCurrencies(currencyToReplace, currencyCode)
                        viewModel.updateCurrencyValue(viewModel.currencies.value[0], "0")
                        navController.popBackStack()
                    } else {
                        viewModel.updateCurrency(currencyToReplace, currencyCode)
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

@Composable
fun CountryRow(countryName: String, currency: String, onClick: () -> Unit) {
    var lastClickTime by remember { mutableStateOf(0L) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime > 1000) {
                    lastClickTime = currentTime
                    onClick()
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = countryName,
            fontSize = 18.sp,
        )
        Text(
            text = currency,
            fontSize = 18.sp,
        )
    }
}

@Composable
fun currencyInfo(viewModel: CurrencyViewModel) {
    val currencies = viewModel.currencies.value

    val fromCurrency = currencies.getOrNull(0) ?: "KRW"
    val toCurrency = currencies.getOrNull(1) ?: "USD"

    val exchangeRates = viewModel.exchangeRates.collectAsState().value
    val exchangeRate = exchangeRates[toCurrency]

    val displayText = if (exchangeRate != null) {
        if (exchangeRate < 0.01) {
            val adjusted = exchangeRate * 1000
            "1000 $fromCurrency = ${String.format("%.5f", adjusted)} $toCurrency"
        } else {
            "1 $fromCurrency = ${String.format("%.5f", exchangeRate)} $toCurrency"
        }
    } else {
        "환율 정보를 불러올 수 없습니다"
    }

    Text(
        text = displayText,
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.snow))
    )
}

@Composable
fun NumberButtons(viewModel: CurrencyViewModel) {
    Column(
        modifier = Modifier.fillMaxHeight()
            .padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
    ) {
        val rowModifier = Modifier.weight(1f)

        @Composable
        fun createButton(text: String? = null, icon: Int? = null, onClick: () -> Unit) {
            Button(
                onClick = onClick,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
                    .border(0.5.dp, Color.White, shape = RectangleShape),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.skyBlue))
            ) {
                if (icon != null) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = text,
                        modifier = Modifier.size(24.dp)
                    )
                } else if (text != null) {
                    Text(text = text, fontSize = 24.sp)
                }
            }
        }

        val buttonRows = listOf(
            listOf("7", "8", "9", "C"),
            listOf("4", "5", "6", R.drawable.backspace),
            listOf("1", "2", "3", R.drawable.swap),
            listOf("0", "00", ".", "=")
        )

        buttonRows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth().then(rowModifier)) {
                row.forEach { item ->
                    when (item) {
                        is String -> createButton(text = item) {
                            val selectedCurrency = viewModel.selectedCurrency.value
                            when (item) {
                                "C" -> {
                                    viewModel.resetAllCurrencyValues()
                                    viewModel.setUserInputValue("0")

                                    if (viewModel.currencies.value.size >= 2) {
                                        val fromCurrency = viewModel.currencies.value[0]
                                        val toCurrency = viewModel.currencies.value[1]
                                        viewModel.convertCurrencyWithFetch(fromCurrency, toCurrency)
                                    }
                                } "=" -> if (viewModel.currencies.value.size >= 2) {
                                    val fromCurrency = viewModel.currencies.value[0]
                                    val toCurrency = viewModel.currencies.value[1]
                                    viewModel.convertCurrencyWithFetch(fromCurrency, toCurrency)
                                } else -> {
                                    val currentInput = viewModel.userInputValue.value
                                    val newInput = if (currentInput == "0") item else currentInput + item
                                    viewModel.setUserInputValue(newInput)
                                    viewModel.updateCurrencyValue(selectedCurrency, newInput)
                                }
                            }
                        }
                        is Int -> createButton(icon = item) {
                            val selectedCurrency = viewModel.selectedCurrency.value
                            when (item) {
                                R.drawable.backspace -> {
                                    val currentValue = viewModel.currencyValues.value[selectedCurrency] ?: "0"
                                    val newValue = if (currentValue.length > 1) currentValue.dropLast(1) else "0"
                                    viewModel.updateCurrencyValue(selectedCurrency, newValue)
                                }
                                R.drawable.swap -> if (viewModel.currencies.value.size >= 2) {
                                    viewModel.swapCurrencies(viewModel.currencies.value[0], viewModel.currencies.value[1])
                                    viewModel.updateCurrencyValue(viewModel.currencies.value[0], "0")

                                    if (viewModel.currencies.value.size >= 2) {
                                        val fromCurrency = viewModel.currencies.value[0]
                                        val toCurrency = viewModel.currencies.value[1]
                                        viewModel.convertCurrencyWithFetch(fromCurrency, toCurrency)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}