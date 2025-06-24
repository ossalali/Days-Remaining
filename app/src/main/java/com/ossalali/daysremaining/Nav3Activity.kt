package com.ossalali.daysremaining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ossalali.daysremaining.presentation.ui.nav3example.NavExample

class Nav3Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent { MyAppTheme { NavExample() } }
    }
}
