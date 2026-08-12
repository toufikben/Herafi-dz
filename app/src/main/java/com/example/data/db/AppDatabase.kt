package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [CraftsmanEntity::class, ReviewEntity::class, BookmarkEntity::class, UserEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun craftsmanDao(): CraftsmanDao
    abstract fun userDao(): UserDao

    companion object {
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Old local users contained plaintext passwords. They cannot be safely
                // converted without the original password, so require re-registration.
                db.execSQL("DROP TABLE IF EXISTS users")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS users (
                        id TEXT NOT NULL,
                        fullName TEXT NOT NULL,
                        email TEXT NOT NULL,
                        passwordHash TEXT NOT NULL,
                        passwordSalt TEXT NOT NULL,
                        userType TEXT NOT NULL,
                        phone TEXT NOT NULL,
                        wilayaCode INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "herafi_dz_database.db"
                )
                    .addMigrations(MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
