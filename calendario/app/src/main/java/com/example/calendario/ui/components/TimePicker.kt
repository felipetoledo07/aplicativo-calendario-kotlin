package com.example.calendario.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TimePicker(
    label: String,
    selectedTime: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    val timePickerDialogState = remember { mutableStateOf(false) }

    Column {
        Text(text = label)
        Text(
            text = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            modifier = Modifier
                .clickable { timePickerDialogState.value = true }
                .padding(8.dp)
        )

        if (timePickerDialogState.value) {
            TimePickerDialog(
                initialTime = selectedTime,
                onTimeSelected = {
                    onTimeChange(it)
                    timePickerDialogState.value = false
                },
                onDismiss = { timePickerDialogState.value = false }
            )
        }
    }
}

@Composable
fun TimePickerDialog(
    initialTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val time = remember { mutableStateOf(initialTime) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Selecione a hora:")
                Spacer(modifier = Modifier.height(8.dp))

                TimePickerWidget(
                    initialTime = time.value,
                    onTimeSelected = {
                        time.value = it
                        onTimeSelected(it)
                    }
                )
            }
        }
    }
}

@Composable
fun TimePickerWidget(initialTime: LocalTime, onTimeSelected: (LocalTime) -> Unit) {
    var hora by remember { mutableIntStateOf(initialTime.hour) }
    var minuto by remember { mutableIntStateOf(initialTime.minute) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        DropdownDeNumero(
            label = "Hora",
            valorAtual = hora,
            valores = (0..23).toList(),
            onValorSelecionado = { hora = it }
        )
        Spacer(modifier = Modifier.width(16.dp))
        DropdownDeNumero(
            label = "Minuto",
            valorAtual = minuto,
            valores = listOf(0, 15, 30, 45),
            onValorSelecionado = { minuto = it }
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Button(onClick = {
        onTimeSelected(LocalTime.of(hora, minuto))
    }) {
        Text("Confirmar horário")
    }
}

@Composable
fun DropdownDeNumero(
    label: String,
    valorAtual: Int,
    valores: List<Int>,
    onValorSelecionado: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label)
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(text = valorAtual.toString().padStart(2, '0'))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                valores.forEach { valor ->
                    DropdownMenuItem(
                        text = { Text(valor.toString().padStart(2, '0')) },
                        onClick = {
                            onValorSelecionado(valor)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
