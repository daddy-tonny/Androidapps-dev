package util

import model.Student
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.pdfbox.Loader                    // ✅ fixed import
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File

object FileImporter {

    fun import(file: File): Result<List<Student>> = runCatching {
        when (file.extension.lowercase()) {
            "xlsx" -> importExcel(file, xlsx = true)
            "xls"  -> importExcel(file, xlsx = false)
            "csv"  -> importCsv(file)
            "pdf"  -> importPdf(file)
            else   -> throw IllegalArgumentException("Unsupported format: .${file.extension}")
        }.also {
            if (it.isEmpty()) throw IllegalStateException("No student data found in file.")
        }
    }

    private fun importExcel(file: File, xlsx: Boolean): List<Student> {
        val wb    = file.inputStream().use { if (xlsx) XSSFWorkbook(it) else HSSFWorkbook(it) }
        val sheet = wb.getSheetAt(0)
        val list  = mutableListOf<Student>()
        for (ri in 1..sheet.lastRowNum) {
            val row  = sheet.getRow(ri) ?: continue
            val name = row.getCell(0)?.toString()?.trim()?.takeIf { it.isNotBlank() } ?: continue
            val scores = (1 until row.lastCellNum).map { ci ->
                row.getCell(ci)?.toString()?.trim()?.toDoubleOrNull() ?: -1.0
            }
            if (scores.isNotEmpty()) list += Student(name, scores)
        }
        wb.close()
        return list
    }

    private fun importCsv(file: File): List<Student> {
        val lines = file.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return emptyList()
        return lines.drop(1).mapNotNull { line ->
            val parts = line.split(",").map { it.trim() }
            if (parts.isEmpty() || parts[0].isBlank()) null
            else Student(parts[0], parts.drop(1).map { it.toDoubleOrNull() ?: -1.0 })
        }
    }

    private fun importPdf(file: File): List<Student> {
        val doc  = Loader.loadPDF(file)            // ✅ fixed API call
        val text = PDFTextStripper().getText(doc)
        doc.close()
        return text.lines().filter { it.contains(",") }.mapNotNull { line ->
            val parts = line.split(",").map { it.trim() }
            if (parts.size < 2 || parts[0].isBlank()) null
            else {
                val scores = parts.drop(1).map { it.toDoubleOrNull() ?: -1.0 }
                if (scores.any { it >= 0 }) Student(parts[0], scores) else null
            }
        }
    }

    fun sampleData(): List<Student> = listOf(
        Student("Alice Johnson",   listOf(92.0, 88.0, 95.0, 91.0)),
        Student("Bob Martinez",    listOf(75.0, 72.0, 68.0, 80.0)),
        Student("Carol Williams",  listOf(55.0, 60.0, 58.0, 52.0)),
        Student("David Chen",      listOf(98.0, 95.0, 99.0, 97.0)),
        Student("Eva Brown",       listOf(42.0, 38.0, 45.0, 40.0)),
        Student("Frank Davis",     listOf(65.0, 70.0, 68.0, 72.0)),
        Student("Grace Wilson",    listOf(85.0, 82.0, 88.0, 84.0)),
        Student("Henry Taylor",    listOf(30.0, 28.0, 35.0, 32.0)),
        Student("Irene Moore",     listOf(78.0, 75.0, 80.0, 77.0)),
        Student("Jack Anderson",   listOf(90.0, 92.0, 87.0, 93.0))
    )
}