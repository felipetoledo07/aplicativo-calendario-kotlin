package com.example.calendario.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.calendario.data.local.entity.Evento
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface EventoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(evento: Evento)

    @Query("SELECT * FROM evento WHERE data = :data")
    fun getEventosPorData(data: LocalDate): Flow<List<Evento>>

    @Query("SELECT * FROM evento WHERE data BETWEEN :startDate AND :endDate")
    fun getEventosDoMes(startDate: LocalDate, endDate: LocalDate): Flow<List<Evento>>

    @Delete
    suspend fun deletar(evento: Evento)

    @Query("SELECT * FROM evento WHERE id = :eventoId")
    fun getEventoById(eventoId: Int): LiveData<Evento>

    @Update
    suspend fun updateEvento(evento: Evento)
}
