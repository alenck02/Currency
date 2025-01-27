package com.example.currency

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.currency.ViewModel.CurrencyViewModel
import com.example.currency.ui.theme.CurrencyTheme
import kotlin.coroutines.coroutineContext

class MainActivity : ComponentActivity() {
    private val currencyViewModel = CurrencyViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
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
    Column {
        viewModel.currencies.value.forEachIndexed { index, currency ->
            CountryItem(currency, navController, viewModel, addTopPadding = index == 0)

            if (index < viewModel.currencies.value.size - 1) {
                Divider()
            }
        }
        currencyInfo()
        NumberButtons()
    }
}

@Composable
fun CountryItem(currency: String, navController: NavHostController, viewModel: CurrencyViewModel, addTopPadding: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(Color.White)
            .padding(start = 10.dp, end = 10.dp)
            .then(if (addTopPadding) Modifier.padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            ) else Modifier)
            .height(140.dp)
            .clickable { navController.navigate("countrySelection/$currency") },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currency,
            fontSize = 24.sp
        )
        Text(
            text = "0",
            fontSize = 24.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountrySelectionScreen(navController: NavHostController, viewModel: CurrencyViewModel, currencyToReplace: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
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

            var searchQuery by remember { mutableStateOf("") }

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

        val countries = listOf(
            Pair("대한민국", "KRW"),
            Pair("미국", "USD"),
            Pair("캐나다", "CAD"),
            Pair("일본", "JPY"),
            Pair("유로", "EUR")
        )

        LazyColumn {
            items(countries) { country ->
                CountryRow(country.first, country.second) {
                    if (viewModel.canSwap(country.second)) {
                        viewModel.swapCurrencies(currencyToReplace, country.second)
                        navController.popBackStack()
                    } else {
                        viewModel.updateCurrency(currencyToReplace, country.second)
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
fun currencyInfo() {
    Text(
        text = "1KRW = 0.00076USD",
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
            .background(colorResource(id = R.color.snow))
    )
}

@Composable
fun NumberButtons() {
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
                            when (item) {
                                "C" -> {
                                    println("Clear action")
                                }
                                else -> {
                                    println("$item clicked")
                                }
                            }
                        }
                        is Int -> createButton(icon = item) {
                            println("Icon clicked")
                        }
                    }
                }
            }
        }
    }
}