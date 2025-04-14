package com.example.calendario.data.local.database

import android.content.Context
import androidx.room.*
import androidx.room.Room
import androidx.room.TypeConverters
import com.example.calendario.data.local.dao.EventoDao
import com.example.calendario.data.local.entity.Evento
import com.example.calendario.data.local.utils.Converters

@Database(entities = [Evento::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventoDao(): EventoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calendario_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
