package com.example.calendario.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.calendario.ui.screens.CalendarioMensal
import com.example.calendario.ui.screens.CriarEventoScreen
import com.example.calendario.data.local.database.AppDatabase
import com.example.calendario.ui.screens.EditarEventoScreen
import com.example.calendario.ui.viewmodel.EventoViewModel
import com.example.calendario.ui.viewmodel.EventoViewModelFactory
import java.time.LocalDate

@Composable
fun AppNavigation(navController: NavHostController) {
    val context = LocalContext.current
    val eventoDao = remember {
        AppDatabase.getDatabase(context).eventoDao()
    }

    NavHost(navController = navController, startDestination = "calendario") {
        composable("calendario") {
            CalendarioMensal(
                eventoDao = eventoDao,
                onNovoEventoClick = { selectedDate ->
                    navController.navigate("criarEvento/${selectedDate}")
                },
                onDataSelecionada = { selectedDate ->
                    Log.d("Calendario", "Data selecionada: $selectedDate")
                },
                navController = navController
            )
        }

        composable("criarEvento/{dataSelecionada}") { backStackEntry ->
            val dataString = backStackEntry.arguments?.getString("dataSelecionada")
            val dataSelecionada = dataString?.let { LocalDate.parse(it) } ?: LocalDate.now()
            CriarEventoScreen(dataSelecionada = dataSelecionada, onVoltar = {
                navController.popBackStack()
            })
        }

        composable("editarEvento/{eventoId}") { backStackEntry ->
            val eventoId = backStackEntry.arguments?.getString("eventoId")?.toInt()
            eventoId?.let {
                val context = LocalContext.current
                val db = AppDatabase.getDatabase(context)
                val eventoDao = db.eventoDao()

                val viewModel: EventoViewModel = viewModel(
                    factory = EventoViewModelFactory(eventoDao)
                )

                EditarEventoScreen(eventoId = it, viewModel = viewModel, navController = navController)
            }
        }
    }
}
