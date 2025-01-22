package com.example.currency

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
        CountryItem("KRW", navController, addTopPadding = true)
        Divider()
        CountryItem("USD", navController, addTopPadding = false)
        currencyInfo()
        NumberButtons()
    }
}

@Composable
fun CountryItem(currency: String, navController: NavHostController, addTopPadding: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(Color.White)
            .padding(start = 10.dp, end = 10.dp)
            .then(if (addTopPadding) Modifier.padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            ) else Modifier)
            .height(140.dp)
            .clickable { navController.navigate("countrySelection") },
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
fun currencyInfo() {
    Text(
        text = "1KRW = 0.00076USD",
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
            .background(Color.LightGray)
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
                modifier = Modifier.weight(1f).fillMaxHeight(),
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

@Preview(showBackground = true)
@Composable
fun PreviewCountryItems() {
    CurrencyTheme {
        Column {
            CountryItem("KRW", rememberNavController(), addTopPadding = true)
            Divider()
            CountryItem("USD", rememberNavController(), addTopPadding = false)
            currencyInfo()
            NumberButtons()
        }
    }
}