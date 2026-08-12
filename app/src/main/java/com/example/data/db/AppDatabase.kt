package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [CraftsmanEntity::class, ReviewEntity::class, BookmarkEntity::class, UserEntity::class, ServiceRequestEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun craftsmanDao(): CraftsmanDao
    abstract fun userDao(): UserDao
    abstract fun serviceRequestDao(): ServiceRequestDao

    companion object {
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS service_requests (
                        id TEXT NOT NULL,
                        remoteId TEXT,
                        customerId TEXT NOT NULL,
                        craftsmanId TEXT,
                        categoryKey TEXT NOT NULL,
                        wilayaCode TEXT NOT NULL,
                        commune TEXT NOT NULL,
                        description TEXT NOT NULL,
                        status TEXT NOT NULL,
                        syncState TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS service_requests_customer_idx ON service_requests(customerId, createdAt)")
            }
        }

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
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
