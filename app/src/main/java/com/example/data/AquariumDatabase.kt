package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [AquariumProfileEntity::class], version = 3, exportSchema = false)
abstract class AquariumDatabase : RoomDatabase() {
  abstract fun aquariumProfileDao(): AquariumProfileDao

  companion object {
    @Volatile private var INSTANCE: AquariumDatabase? = null

    val MIGRATION_1_2 =
      object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
          db.execSQL(
            "ALTER TABLE aquarium_profiles ADD COLUMN stoneInputModeName TEXT NOT NULL DEFAULT 'LITER'"
          )
          db.execSQL(
            "ALTER TABLE aquarium_profiles ADD COLUMN stoneTypeName TEXT NOT NULL DEFAULT 'SEIRYU_KALI'"
          )
          db.execSQL(
            "ALTER TABLE aquarium_profiles ADD COLUMN stoneWeightGramsPerUnit TEXT NOT NULL DEFAULT '1500'"
          )
        }
      }

    val MIGRATION_2_3 =
      object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
          db.execSQL(
            "ALTER TABLE aquarium_profiles ADD COLUMN woodWeightGramsPerUnit TEXT NOT NULL DEFAULT '850'"
          )
        }
      }

    fun getInstance(context: Context): AquariumDatabase {
      return INSTANCE
        ?: synchronized(this) {
          INSTANCE
            ?: Room.databaseBuilder(
                context.applicationContext,
                AquariumDatabase::class.java,
                "aquavolume_db",
              )
              .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
              .build()
              .also { INSTANCE = it }
        }
    }
  }
}
