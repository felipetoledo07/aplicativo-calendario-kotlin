package com.example.calendario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.calendario.data.local.dao.EventoDao
import com.example.calendario.ui.viewmodel.EventoViewModel
import com.example.calendario.ui.viewmodel.EventoViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun CalendarioMensal(
    eventoDao: EventoDao,
    onNovoEventoClick: (LocalDate) -> Unit,
    onDataSelecionada: (LocalDate) -> Unit,
    navController: NavHostController
) {
    val viewModel: EventoViewModel = viewModel(
        factory = EventoViewModelFactory(eventoDao)
    )

    var dataSelecionada by remember { mutableStateOf(LocalDate.now()) }
    var mesAtual by remember { mutableStateOf(YearMonth.now()) }
    var eventos by remember { mutableStateOf(emptyList<com.example.calendario.data.local.entity.Evento>()) }
    var eventosDoMes by remember { mutableStateOf(emptyList<com.example.calendario.data.local.entity.Evento>()) }

    LaunchedEffect(dataSelecionada) {
        onDataSelecionada(dataSelecionada)
        viewModel.eventosPorData(dataSelecionada).collectLatest {
            eventos = it
        }
    }

    LaunchedEffect(mesAtual) {
        val primeiroDiaDoMes = mesAtual.atDay(1)
        val ultimoDiaDoMes = mesAtual.atEndOfMonth()
        viewModel.eventosDoMes(primeiroDiaDoMes, ultimoDiaDoMes).collectLatest { eventosMes ->
            eventosDoMes = eventosMes
        }
    }

    val hoje = LocalDate.now()
    val diasNoMes = mesAtual.lengthOfMonth()
    val primeiroDiaDaSemana = mesAtual.atDay(1).dayOfWeek.value % 7
    val dias = (1..diasNoMes).map { dia -> mesAtual.atDay(dia) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { mesAtual = mesAtual.minusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mês anterior")
            }
            Text(
                text = dataSelecionada.format(
                    DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
                ).replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineSmall
            )
            IconButton(onClick = { mesAtual = mesAtual.plusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Próximo mês")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mesAtual.format(
                    DateTimeFormatter.ofPattern("MMMM '-' yyyy", Locale("pt", "BR"))
                ).replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val diasSemana = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            diasSemana.forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val totalDias = dias.size + primeiroDiaDaSemana
        val linhas = (totalDias + 6) / 7

        Column {
            for (linha in 0 until linhas) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (coluna in 0..6) {
                        val index = linha * 7 + coluna
                        val dia = if (index >= primeiroDiaDaSemana && index < primeiroDiaDaSemana + dias.size) {
                            dias[index - primeiroDiaDaSemana]
                        } else null

                        val temEvento = dia != null && eventosDoMes.any { it.data == dia }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        dia == dataSelecionada -> MaterialTheme.colorScheme.primary
                                        dia == hoje -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                                        temEvento -> Color.Yellow.copy(alpha = 0.8f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable(enabled = dia != null) {
                                    dia?.let { dataSelecionada = it }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dia?.dayOfMonth?.toString() ?: "",
                                color = if (dia == dataSelecionada) Color.White else Color.Unspecified
                            )
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Eventos do dia:",
                style = MaterialTheme.typography.titleMedium
            )

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(eventos) { evento ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = evento.nome, style = MaterialTheme.typography.bodyLarge)
                                    Text(text = "Início: ${evento.horaInicio}", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "Fim: ${evento.horaFim}", style = MaterialTheme.typography.bodyMedium)
                                }

                                Row {
                                    IconButton(onClick = {
                                        navController.navigate("editarEvento/${evento.id}")
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar evento",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    IconButton(onClick = { viewModel.excluirEvento(evento) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Excluir evento",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { onNovoEventoClick(dataSelecionada) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Criar novo evento")
                }
            }
        }
    }
}
