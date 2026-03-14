package export

import com.lowagie.text.*
import com.lowagie.text.pdf.*
import model.GradeResult
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Exporter {

    private val timestamp: String
        get() = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

    // ── Excel ─────────────────────────────────────────────────────────────────
    fun toExcel(results: List<GradeResult>, file: File) {
        val wb    = XSSFWorkbook()
        val sheet = wb.createSheet("Grade Results")
        val hs    = wb.createCellStyle().apply {
            fillForegroundColor = IndexedColors.DARK_BLUE.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            setFont(wb.createFont().apply { bold = true; color = IndexedColors.WHITE.index; fontHeightInPoints = 12 })
        }
        val hRow = sheet.createRow(0)
        listOf("Rank", "Name", "Scores", "Average", "Grade", "Status").forEachIndexed { i, h ->
            hRow.createCell(i).apply { setCellValue(h); cellStyle = hs }
        }
        results.forEach { r ->
            val row = sheet.createRow(r.rank)
            row.createCell(0).setCellValue(r.rank.toDouble())
            row.createCell(1).setCellValue(r.student.name)
            row.createCell(2).setCellValue(r.student.scores.joinToString(", ") { if (it < 0) "—" else "%.1f".format(it) })
            row.createCell(3).setCellValue("%.2f".format(r.average))
            row.createCell(4).setCellValue(r.letter)
            row.createCell(5).setCellValue(if (r.isPassed) "PASS" else "FAIL")
        }
        (0..5).forEach { sheet.autoSizeColumn(it) }
        FileOutputStream(file).use { wb.write(it) }
        wb.close()
    }

    // ── PDF ───────────────────────────────────────────────────────────────────
    fun toPdf(results: List<GradeResult>, file: File) {
        val doc = Document(PageSize.A4.rotate())
        PdfWriter.getInstance(doc, FileOutputStream(file))
        doc.open()
        val bf = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11f)
        val nf = FontFactory.getFont(FontFactory.HELVETICA, 11f)
        doc.add(Paragraph("Grade Calculator — Results Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18f)))
        doc.add(Paragraph("Generated: $timestamp", nf))
        doc.add(Chunk.NEWLINE)
        val table = PdfPTable(6).apply { widthPercentage = 100f }
        listOf("Rank", "Name", "Scores", "Average", "Grade", "Status").forEach { h ->
            table.addCell(PdfPCell(Phrase(h, bf)).apply {
                backgroundColor = java.awt.Color(0x07, 0x11, 0x30)
            })
        }
        results.forEach { r ->
            table.addCell(Phrase("${r.rank}", nf))
            table.addCell(Phrase(r.student.name, nf))
            table.addCell(Phrase(r.student.scores.joinToString(", ") { if (it < 0) "—" else "%.1f".format(it) }, nf))
            table.addCell(Phrase("%.2f".format(r.average), nf))
            table.addCell(Phrase(r.letter, bf))
            table.addCell(PdfPCell(Phrase(if (r.isPassed) "PASS" else "FAIL", bf)).apply {
                backgroundColor = if (r.isPassed) java.awt.Color(0x00, 0xCC, 0x77) else java.awt.Color(0xFF, 0x4E, 0x72)
            })
        }
        doc.add(table)
        doc.close()
    }

    // ── HTML ──────────────────────────────────────────────────────────────────
    fun toHtml(results: List<GradeResult>, file: File) {
        val pass = results.count { it.isPassed }
        val avg  = if (results.isEmpty()) 0.0 else results.sumOf { it.average } / results.size
        val rows = results.joinToString("\n") { r ->
            val sc = if (r.isPassed) "#00e590" else "#ff4e72"
            val gc = gradeHex(r.letter)
            "<tr>" +
            "<td class=c>${r.rank}</td>" +
            "<td><b>${r.student.name}</b></td>" +
            "<td class=m>${r.student.scores.joinToString(", ") { if (it < 0) "—" else "%.1f".format(it) }}</td>" +
            "<td class=c>${"%.2f".format(r.average)}</td>" +
            "<td class=c><span class=badge style='background:${gc}22;color:$gc'>${r.letter}</span></td>" +
            "<td class=c><span class=badge style='background:${sc}22;color:$sc'>${if (r.isPassed) "PASS" else "FAIL"}</span></td>" +
            "</tr>"
        }
        file.writeText("""<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><title>Grade Results</title>
<style>
*{box-sizing:border-box;margin:0;padding:0}
body{font-family:'Segoe UI',sans-serif;background:#050d1a;color:#c2dcf8;padding:32px}
h1{font-size:22px;color:#00d4ff;margin-bottom:4px}
.meta{color:#527494;font-size:13px;margin-bottom:20px}
.stats{display:flex;gap:12px;margin-bottom:20px}
.stat{background:#0a162b;border:1px solid #1a3860;border-radius:10px;padding:14px 22px}
.sv{font-size:26px;font-weight:700;color:#1e72ff}
.sl{font-size:11px;color:#527494;margin-top:2px}
table{width:100%;border-collapse:collapse;background:#0a162b;border-radius:10px;overflow:hidden}
th{background:#07111e;color:#527494;font-size:11px;text-transform:uppercase;letter-spacing:.06em;padding:11px 14px;text-align:left}
td{padding:11px 14px;border-bottom:1px solid #12284a;font-size:13px}
tr:last-child td{border-bottom:none}
tr:hover td{background:#0d1c38}
.c{text-align:center}
.m{font-family:Consolas,monospace;font-size:12px;color:#2e4268}
.badge{display:inline-block;padding:3px 10px;border-radius:20px;font-size:11px;font-weight:700}
</style></head><body>
<h1>📊 Grade Calculator — Results</h1>
<p class="meta">Generated: $timestamp &nbsp;·&nbsp; ${results.size} students</p>
<div class="stats">
  <div class="stat"><div class="sv">${"%.1f".format(avg)}</div><div class="sl">Class Average</div></div>
  <div class="stat"><div class="sv">$pass</div><div class="sl">Passed</div></div>
  <div class="stat"><div class="sv">${results.size - pass}</div><div class="sl">Failed</div></div>
  <div class="stat"><div class="sv">${"%.1f".format(results.maxOfOrNull { it.average } ?: 0.0)}</div><div class="sl">Highest</div></div>
</div>
<table>
<thead><tr><th>#</th><th>Name</th><th>Scores</th><th>Average</th><th>Grade</th><th>Status</th></tr></thead>
<tbody>$rows</tbody>
</table>
</body></html>""")
    }

    // ── XML ───────────────────────────────────────────────────────────────────
    fun toXml(results: List<GradeResult>, file: File) {
        val sb = StringBuilder()
        sb.appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
        sb.appendLine("""<gradeReport generated="$timestamp" students="${results.size}">""")
        results.forEach { r ->
            sb.appendLine("""  <student rank="${r.rank}">""")
            sb.appendLine("""    <name>${r.student.name.xmlEscape()}</name>""")
            sb.appendLine("""    <scores>${r.student.scores.joinToString(",") { if (it < 0) "" else "%.1f".format(it) }}</scores>""")
            sb.appendLine("""    <average>${"%.2f".format(r.average)}</average>""")
            sb.appendLine("""    <grade>${r.letter}</grade>""")
            sb.appendLine("""    <status>${if (r.isPassed) "PASS" else "FAIL"}</status>""")
            sb.appendLine("""  </student>""")
        }
        sb.appendLine("</gradeReport>")
        file.writeText(sb.toString())
    }

    // ── CSV ───────────────────────────────────────────────────────────────────
    fun toCsv(results: List<GradeResult>, file: File) {
        val sb = StringBuilder("Rank,Name,Scores,Average,Grade,Status\n")
        results.forEach { r ->
            val scores = r.student.scores.joinToString("|") { if (it < 0) "" else "%.1f".format(it) }
            sb.appendLine("${r.rank},\"${r.student.name}\",\"$scores\",${
                "%.2f".format(r.average)},${r.letter},${if (r.isPassed) "PASS" else "FAIL"}")
        }
        file.writeText(sb.toString())
    }

    private fun gradeHex(g: String): String = when (g) {
        "A+" -> "#00e590"; "A" -> "#00cc80"; "B+" -> "#00d4ff"
        "B"  -> "#1e72ff"; "C" -> "#ffb800"; "D"  -> "#ff7c2a"; else -> "#ff4e72"
    }

    private fun String.xmlEscape() =
        replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
}
