package com.example.currency

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.currency.ui.theme.CurrencyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CurrencyTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "main") {
        composable("main") { uiPreview(navController) }
        composable("countrySelection") { CountrySelectionScreen(navController) }
    }
}

@Composable
fun uiPreview(navController: NavHostController) {
    Column {
        CountryItem("KRW", navController)
        CountryItem("USD", navController)
        numberBtn()
    }
}

@Composable
fun CountryItem(currency: String, navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(10.dp)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .clickable { navController.navigate("countrySelection") },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = currency)
        Text(text = "0")
    }
}

@Composable
fun CountrySelectionScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
    ) {
        Text(text = "나라 선택 화면입니다.")

        Text(
            text = "나라 선택 완료",
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
    }
}

@Composable
fun numberBtn() {
    Column() {
        Row() {
            Button(onClick = {}, modifier = Modifier.wrapContentSize()) {
                Text(text = "7")
            }

            Button(onClick = {}, modifier = Modifier.wrapContentSize()) {
                Text(text = "8")
            }

            Button(onClick = {}, modifier = Modifier.wrapContentSize()) {
                Text(text = "9")
            }

            Button(onClick = {}, modifier = Modifier.wrapContentSize()) {
                Text(text = "C")
            }
        }

        Row() {
            Button(onClick = {}, modifier = Modifier.wrapContentSize()) {
                Text(text = "4")
            }

            Button(onClick = {}, modifier = Modifier.wrapContentSize()) {
                Text(text = "5")
            }

            Button(onClick = {}, modifier = Modifier.wrapContentSize()) {
                Text(text = "6")
            }

            Button(onClick = {}, modifier = Modifier.wrapContentSize()) {
                Image(
                    painter = painterResource(id = R.drawable.backspace),
                    contentDescription = "Backspace"
                )
            }
        }

        Row() {
            Button(onClick = {}, modifier = Modifier.wrapContentSize()) {
                Text(text = "1")
            }

            Button(onClick = {}, modifier = Modifier.wrapContentSize()) {
                Text(text = "2")
            }

            Button(onClick = {}, modifier = Modifier.wrapContentSize()) {
                Text(text = "3")
            }

            Button(onClick = {}, modifier = Modifier.wrapContentSize()) {
                Image(
                    painter = painterResource(id = R.drawable.swap),
                    contentDescription = "Swap"
                )
            }
        }

        Row() {
            Button(onClick = {}, modifier = Modifier.wrapContentSize()) {
                Text(text = "0")
            }

            Button(onClick = {}, modifier = Modifier.wrapContentSize()) {
                Text(text = "00")
            }

            Button(onClick = {}, modifier = Modifier.wrapContentSize()) {
                Text(text = ".")
            }

            Button(onClick = {}, modifier = Modifier.wrapContentSize()) {
                Text(text = "=")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCountryItems() {
    CurrencyTheme {
        Column {
            CountryItem("KRW", rememberNavController())
            CountryItem("USD", rememberNavController())
            numberBtn()
        }
    }
}