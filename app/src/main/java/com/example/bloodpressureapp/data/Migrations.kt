package com.example.bloodpressureapp.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Therapy (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                userId INTEGER NOT NULL,
                name TEXT NOT NULL,
                dosage TEXT NOT NULL,
                FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE
            )
        """.trimIndent())
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS reminders (
            
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                userId INTEGER NOT NULL,
                hour INTEGER NOT NULL,
                minute INTEGER NOT NULL,
                message TEXT NOT NULL,
                repeatDaily INTEGER NOT NULL,
                days TEXT NOT NULL,
                createdAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE INDEX index_measurement_userId ON measurements(userId)")
        db.execSQL("CREATE INDEX index_therapy_userId ON therapy(userId)")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE reminders_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                userId INTEGER NOT NULL,
                hour INTEGER NOT NULL,
                minute INTEGER NOT NULL,
                message TEXT NOT NULL,
                repeatDaily INTEGER NOT NULL,
                days TEXT NOT NULL,
                createdAt INTEGER NOT NULL,
                FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO reminders_new (
                id,
                userId,
                hour,
                minute,
                message,
                repeatDaily,
                days,
                createdAt
            )
            SELECT
                id,
                userId,
                hour,
                minute,
                message,
                repeatDaily,
                days,
                createdAt
            FROM reminders
            WHERE userId IN (SELECT id FROM users)
            """.trimIndent()
        )

        db.execSQL("DROP TABLE reminders")

        db.execSQL("ALTER TABLE reminders_new RENAME TO reminders")

        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_reminders_userId ON reminders(userId)"
        )
    }
}



