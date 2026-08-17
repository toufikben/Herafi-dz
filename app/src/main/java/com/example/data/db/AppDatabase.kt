package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [CraftsmanEntity::class, ReviewEntity::class, BookmarkEntity::class, UserEntity::class, ServiceRequestEntity::class],
    version = 10,
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

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE service_requests ADD COLUMN imageUrls TEXT NOT NULL DEFAULT '[]'")
            }
        }

        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE service_requests ADD COLUMN pendingPhotoPaths TEXT NOT NULL DEFAULT '[]'")
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE service_requests ADD COLUMN customerDisplayName TEXT")
                db.execSQL("ALTER TABLE service_requests ADD COLUMN craftsmanName TEXT")
                db.execSQL("ALTER TABLE service_requests ADD COLUMN craftsmanPhone TEXT")
                db.execSQL("ALTER TABLE service_requests ADD COLUMN craftsmanRating REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE service_requests ADD COLUMN craftsmanCategory TEXT")
                db.execSQL("ALTER TABLE service_requests ADD COLUMN craftsmanWilaya TEXT")
                db.execSQL("ALTER TABLE service_requests ADD COLUMN isMine INTEGER NOT NULL DEFAULT 0")
                db.execSQL("CREATE INDEX IF NOT EXISTS service_requests_craftsman_idx ON service_requests(craftsmanId, createdAt)")
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS craftsmen_category_idx ON craftsmen(categoryKey)")
                db.execSQL("CREATE INDEX IF NOT EXISTS craftsmen_wilaya_idx ON craftsmen(wilayaCode)")
                db.execSQL("CREATE INDEX IF NOT EXISTS craftsmen_rating_idx ON craftsmen(ratingScore DESC)")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE service_requests ADD COLUMN clientRequestId TEXT NOT NULL DEFAULT ''")
                db.execSQL("UPDATE service_requests SET clientRequestId = id WHERE clientRequestId = ''")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS service_requests_client_request_idx ON service_requests(customerId, clientRequestId)")
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
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
