package com.example.calendario.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.calendario.data.local.database.AppDatabase
import com.example.calendario.data.local.entity.Evento
import com.example.calendario.ui.components.TimePicker
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarEventoScreen(dataSelecionada: LocalDate, onVoltar: () -> Unit) {
    var nomeEvento by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf(LocalTime.now().truncatedTo(ChronoUnit.MINUTES)) }
    var horaFim by remember { mutableStateOf(horaInicio.plusHours(1)) }
    val horarioInvalido = horaFim <= horaInicio
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getDatabase(context) }
    val eventoDao = db.eventoDao()
    val formattedData = dataSelecionada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Evento") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Data do Evento: $formattedData", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nomeEvento,
                onValueChange = { nomeEvento = it },
                label = { Text("Nome do Evento") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            TimePicker("Hora de Início", horaInicio) { horaInicio = it }
            Spacer(modifier = Modifier.height(16.dp))
            TimePicker("Hora de Fim", horaFim) { horaFim = it }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (horaFim.isAfter(horaInicio)) {
                    scope.launch {
                        eventoDao.inserir(
                            Evento(
                                nome = nomeEvento,
                                data = dataSelecionada,
                                horaInicio = horaInicio,
                                horaFim = horaFim
                            )
                        )
                        onVoltar()
                    }
                } else {
                    Toast.makeText(context, "Horário final deve ser após o inicial", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Evento")
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
