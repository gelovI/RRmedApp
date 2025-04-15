package com.example.bloodpressureapp.util

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.bloodpressureapp.data.Measurement
import com.example.bloodpressureapp.data.User
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun generateMeasurementPdf(
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
    val page = doc.startPage(pageInfo)
    val canvas = page.canvas

    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val headerFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    var y = 40

    paint.textSize = 18f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("Blutdruckreport für: ${user.name}", 40f, y.toFloat(), paint)
    y += 30

    paint.textSize = 14f
    paint.typeface = Typeface.DEFAULT
    canvas.drawText("Zeitraum: ${headerFormat.format(startDate)} - ${headerFormat.format(endDate)}", 40f, y.toFloat(), paint)
    y += 30

    paint.textSize = 12f
    paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
    canvas.drawText("Zeit            | Syst./Diast. | Puls | Arrhythmie", 40f, y.toFloat(), paint)
    y += 20
    canvas.drawLine(40f, y.toFloat(), 555f, y.toFloat(), paint)
    y += 15

    filtered.forEach {
        val line = "${dateFormat.format(Date(it.timestamp))} | ${it.systolic}/${it.diastolic} mmHg | ${it.pulse}  | ${if (it.arrhythmia) "Ja" else "Nein"}"
        canvas.drawText(line, 40f, y.toFloat(), paint)
        y += 20

        if (y > 800) {
            doc.finishPage(page)
            y = 40
        }
    }

    doc.finishPage(page)

    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "Blutdruck_${System.currentTimeMillis()}.pdf"
    )

    doc.writeTo(FileOutputStream(file))
    doc.close()

    return file
}
