package com.example.bloodpressureapp.util

import com.example.bloodpressureapp.data.Measurement
import com.example.bloodpressureapp.data.User
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun generateMeasurementPdf(
    context: Context,
    data: List<Measurement>,
    startDate: Date,
    endDate: Date,
    user: User
): File? {
    val filtered = data.filter {
        val ts = Date(it.timestamp)
        ts.after(startDate) && ts.before(endDate)
    }
    if (filtered.isEmpty()) return null

    val doc = PdfDocument()
    val paint = Paint()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
    var page = doc.startPage(pageInfo)
    var canvas = page.canvas
    var y = 50

    // Kopfzeile
    paint.textSize = 20f
    paint.isFakeBoldText = true
    canvas.drawText("Blutdruckbericht – ${user.name}", 40f, y.toFloat(), paint)
    y += 30
    paint.textSize = 14f
    paint.isFakeBoldText = false
    val headerFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    canvas.drawText("Zeitraum: ${headerFormat.format(startDate)} - ${headerFormat.format(endDate)}", 40f, y.toFloat(), paint)
    y += 25

    // Spaltenüberschriften
    paint.textSize = 13f
    paint.isFakeBoldText = true
    canvas.drawText("Datum/Uhrzeit", 40f, y.toFloat(), paint)
    canvas.drawText("Sys/Dia", 180f, y.toFloat(), paint)
    canvas.drawText("Puls", 300f, y.toFloat(), paint)
    canvas.drawText("Arrh.", 370f, y.toFloat(), paint)
    y += 15
    paint.isFakeBoldText = false
    canvas.drawLine(40f, y.toFloat(), 540f, y.toFloat(), paint)
    y += 15

    // Daten
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    filtered.forEach {
        if (y > 800) {
            doc.finishPage(page)
            page = doc.startPage(pageInfo)
            canvas = page.canvas
            y = 50
        }
        canvas.drawText(dateFormat.format(Date(it.timestamp)), 40f, y.toFloat(), paint)
        canvas.drawText("${it.systolic}/${it.diastolic}", 180f, y.toFloat(), paint)
        canvas.drawText(it.pulse.toString(), 300f, y.toFloat(), paint)
        canvas.drawText(if (it.arrhythmia) "Ja" else "Nein", 370f, y.toFloat(), paint)
        y += 20
    }

    doc.finishPage(page)

    val fileName = "Blutdruck_${user.name}_${headerFormat.format(Date())}.pdf"
    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    if (downloadDir != null && !downloadDir.exists()) {
        downloadDir.mkdirs()
    }
    val file = File(downloadDir, fileName)
    return try {
        doc.writeTo(FileOutputStream(file))
        doc.close()
        file
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}