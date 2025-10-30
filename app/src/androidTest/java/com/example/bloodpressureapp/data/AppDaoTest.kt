package com.example.bloodpressureapp.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AppDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.dao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insert_and_get_user() = runBlocking {
        val user = User(id = 0, name = "Ivan Test")
        val userId = dao.insertUser(user)
        val allUsers = dao.getAllUsersOnce()
        Assert.assertEquals(1, allUsers.size)
        Assert.assertEquals("Ivan Test", allUsers.first().name)
    }

    @Test
    fun insert_measurement_and_query_by_user() = runBlocking {
        val user = User(id = 0, name = "Ivan Test")
        val userId = dao.insertUser(user)
        val measurement = Measurement(
            id = 0,
            userId = userId.toInt(),
            systolic = 120,
            diastolic = 80,
            pulse = 70,
            arrhythmia = false,
            timestamp = System.currentTimeMillis()
        )
        dao.insertMeasurement(measurement)
        val list = dao.getMeasurementsForUser(userId.toInt()).first()
        Assert.assertEquals(1, list.size)
        Assert.assertEquals(120, list.first().systolic)
    }

    @Test
    fun delete_user_cascades_measurements() = runBlocking {
        val user = User(id = 0, name = "Delete Me")
        val userId = dao.insertUser(user)
        val measurement = Measurement(
            id = 0,
            userId = userId.toInt(),
            systolic = 100,
            diastolic = 60,
            pulse = 65,
            arrhythmia = false
        )
        dao.insertMeasurement(measurement)
        // Jetzt Nutzer löschen:
        dao.deleteUser(user.copy(id = userId.toInt()))
        // Messungen sollten ebenfalls weg sein (wegen ForeignKey CASCADE):
        val all = dao.getAllMeasurements()
        Assert.assertTrue("Messungen sollten nach User-Löschung gelöscht sein", all.isEmpty())
    }

    @Test
    fun insert_and_delete_reminder() = runBlocking {
        val user = User(id = 0, name = "ReminderUser")
        val userId = dao.insertUser(user)
        val reminder = Reminder(
            id = 0,
            userId = userId.toInt(),
            hour = 8,
            minute = 15,
            message = "Pillen nehmen",
            repeatDaily = true,
            days = "1,3,5"
        )
        val reminderId = dao.insertReminder(reminder)
        var list = dao.getRemindersForUser(userId.toInt()).first()
        Assert.assertEquals(1, list.size)
        dao.deleteReminder(list.first())
        list = dao.getRemindersForUser(userId.toInt()).first()
        Assert.assertTrue(list.isEmpty())
    }
}