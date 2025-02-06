package com.ossalali.daysremaining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.ossalali.daysremaining.presentation.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // You can use your composables here.
            MainScreen()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen()
}