package com.example.bloodpressureapp.util

import com.example.bloodpressureapp.data.Measurement
import com.example.bloodpressureapp.data.User
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.ByteArrayOutputStream

fun generateMeasurementPdfBytes(
    data: List<Measurement>,
    startDate: Date,
    endDate: Date,
    user: User
): ByteArray? {
    val filtered = data.filter {
        val ts = Date(it.timestamp)
        !ts.before(startDate) && !ts.after(endDate)
    }

    if (filtered.isEmpty()) return null

    val doc = PdfDocument()
    val paint = Paint()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    var page = doc.startPage(pageInfo)
    var canvas = page.canvas
    var y = 50

    paint.textSize = 20f
    paint.isFakeBoldText = true
    canvas.drawText("Blutdruckbericht - ${user.name}", 40f, y.toFloat(), paint)
    y += 30

    paint.textSize = 14f
    paint.isFakeBoldText = false
    val headerFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    canvas.drawText(
        "Zeitraum: ${headerFormat.format(startDate)} - ${headerFormat.format(endDate)}",
        40f,
        y.toFloat(),
        paint
    )
    y += 25

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

    return try {
        val output = ByteArrayOutputStream()
        doc.writeTo(output)
        doc.close()
        output.toByteArray()
    } catch (e: Exception) {
        doc.close()
        null
    }
}
