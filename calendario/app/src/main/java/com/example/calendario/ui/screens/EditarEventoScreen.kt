package com.example.calendario.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.calendario.ui.components.TimePicker
import com.example.calendario.ui.viewmodel.EventoViewModel
import kotlinx.coroutines.launch
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarEventoScreen(eventoId: Int, viewModel: EventoViewModel, navController: NavHostController) {

    val evento by viewModel.getEventoById(eventoId).observeAsState()
    val scope = rememberCoroutineScope()

    evento?.let {
        var nome by remember { mutableStateOf(it.nome) }
        var horaInicio by remember { mutableStateOf(it.horaInicio) }
        var horaFim by remember { mutableStateOf(it.horaFim) }
        var horarioInvalido by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Editar Evento") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(top = paddingValues.calculateTopPadding()),
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do Evento") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TimePicker(
                    label = "Hora de Início",
                    selectedTime = horaInicio,
                    onTimeChange = { horaInicio = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                TimePicker(
                    label = "Hora de Fim",
                    selectedTime = horaFim,
                    onTimeChange = { horaFim = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (horaFim.isAfter(horaInicio)) {
                            scope.launch {
                                val eventoAtualizado = it.copy(
                                    nome = nome,
                                    horaInicio = horaInicio,
                                    horaFim = horaFim
                                )
                                viewModel.atualizarEvento(eventoAtualizado)
                                navController.popBackStack()
                            }
                        } else {
                            horarioInvalido = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Salvar Alterações")
                }

                if (horarioInvalido) {
                    Text(
                        "A hora de fim deve ser posterior à hora de início",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
