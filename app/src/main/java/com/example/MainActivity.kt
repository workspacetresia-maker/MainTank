package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AquariumDatabase
import com.example.data.AquariumProfileRepository
import com.example.ui.AquariumApp
import com.example.ui.theme.AquaVolumeTheme
import com.example.viewmodel.AquariumViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = AquariumDatabase.getInstance(applicationContext)
    val repository = AquariumProfileRepository(database.aquariumProfileDao())

    setContent {
      AquaVolumeTheme {
        val viewModel: AquariumViewModel =
          viewModel(factory = AquariumViewModel.Factory(repository))
        AquariumApp(viewModel = viewModel)
      }
    }
  }
}
