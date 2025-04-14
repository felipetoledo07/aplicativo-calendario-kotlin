package com.example.calendario.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calendario.data.local.dao.EventoDao
import com.example.calendario.data.local.entity.Evento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate

class EventoViewModel(
    private val eventoDao: EventoDao
) : ViewModel() {

    fun eventosPorData(data: LocalDate): Flow<List<Evento>> {
        return eventoDao.getEventosPorData(data)
    }

    fun getEventoById(eventoId: Int): LiveData<Evento> {
        return eventoDao.getEventoById(eventoId)
    }

    fun atualizarEvento(evento: Evento) {
        viewModelScope.launch {
            eventoDao.updateEvento(evento)
        }
    }

    fun excluirEvento(evento: Evento) {
        viewModelScope.launch {
            eventoDao.deletar(evento)
        }
    }

    fun eventosDoMes(startDate: LocalDate, endDate: LocalDate): Flow<List<Evento>> {
        return eventoDao.getEventosDoMes(startDate, endDate)
    }
}

class EventoViewModelFactory(
    private val eventoDao: EventoDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventoViewModel(eventoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
