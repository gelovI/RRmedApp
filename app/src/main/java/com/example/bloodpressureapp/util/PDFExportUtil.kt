package com.example.bloodpressureapp.util

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.data.Measurement
import com.example.bloodpressureapp.data.User
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

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
    var y = 40

    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val headerFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    val safeUserName = user.name

    // Header
    paint.textSize = 18f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText(context.getString(R.string.pdf_report_title, user.name), 40f, y.toFloat(), paint)
    y += 30

    paint.textSize = 14f
    paint.typeface = Typeface.DEFAULT
    canvas.drawText(context.getString(R.string.pdf_date_range, headerFormat.format(startDate), headerFormat.format(endDate)), 40f, y.toFloat(), paint)
    y += 30

    paint.textSize = 12f
    paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
    canvas.drawText(context.getString(R.string.pdf_header), 40f, y.toFloat(), paint)
    y += 20
    canvas.drawLine(40f, y.toFloat(), 555f, y.toFloat(), paint)
    y += 15

    filtered.forEachIndexed { index, it ->
        val line = "${dateFormat.format(Date(it.timestamp))} | ${it.systolic}/${it.diastolic} mmHg | ${it.pulse} | ${if (it.arrhythmia) "Ja" else "Nein"}"
        canvas.drawText(line, 40f, y.toFloat(), paint)
        y += 20

        if (y > 800) {
            doc.finishPage(page)
            page = doc.startPage(pageInfo)
            canvas = page.canvas
            y = 40
        }
    }

    // Finish last page
    doc.finishPage(page)

    val safeDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val currentDate = safeDateFormat.format(Date())
    val fileName = "Blutdruck_${safeUserName}_$currentDate.pdf"

    // Speicherort prüfen
    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    if (!downloadDir.exists()) {
        downloadDir.mkdirs()
    }

    val file = File(downloadDir, fileName)

    return try {
        doc.writeTo(FileOutputStream(file))
        doc.close()
        Toast.makeText(context, "PDF gespeichert: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        file
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Fehler beim Speichern des PDFs", Toast.LENGTH_LONG).show()
        null
    }
}
