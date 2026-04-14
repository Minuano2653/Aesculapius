package com.example.aesculapius.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.aesculapius.ui.tests.MetricsItem
import com.example.aesculapius.ui.tests.RecommendationItem
import com.example.aesculapius.ui.tests.ScoreItem
import com.example.aesculapius.ui.therapy.DoseItem
import com.example.aesculapius.ui.therapy.MedicineItem

@Database(entities = [MedicineItem::class, ScoreItem::class, MetricsItem::class, DoseItem::class, RecommendationItem::class], version = 9, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AesculapiusDatabase: RoomDatabase() {
    abstract fun itemDao(): ItemDAO
    companion object {
        /**
         * [Volatile] значит, что база данных не будет кэшироваться, будет храниться только в главной памяти
         * Если один поток изменил database, это сразу увидят все остальные потоки
         * [Instance] ссылка на базу данных
         */
        @Volatile
        private var Instance: AesculapiusDatabase? = null

        fun getDatabase(context: Context): AesculapiusDatabase {
            // следующий код является критическим сегментом, поэтому доступ к нему реализуется через
            // synchronized, чтобы к сегменту не могли обратиться несколько процессов одновременно
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AesculapiusDatabase::class.java, "item_database")
                    // применяется "деструкттивная миграция", то есть бд полностью пересоздаётся при
                    // изменении своей структуры (добавление/удаление столбцов/строк)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}