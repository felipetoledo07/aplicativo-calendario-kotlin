package com.example.calendario.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class Evento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val data: LocalDate,
    val horaInicio: LocalTime,
    val horaFim: LocalTime
)
