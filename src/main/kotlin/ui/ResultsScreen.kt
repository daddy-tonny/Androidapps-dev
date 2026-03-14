package ui

import calculator.BaseCalculator
import calculator.StandardCalculator
import calculator.StrictCalculator
import model.GradeResult
import util.Colours
import util.ConsoleUtils

/**
 * Results Screen — displays computed grades and offers export options.
 *
 * Demonstrates:
 *  - Class with constructor parameters
 *  - Lambdas for filtering and sorting
 *  - Usage of inherited methods from BaseCalculator
 */
class ResultsScreen(private val results: List<GradeResult>,
                    private val calculator: BaseCalculator) {

    fun show() {
        ConsoleUtils.header("GRADE CALCULATOR  ─  Results")

        printResultsTable()
        printStatistics()
        exportMenu()
    }

    private fun printResultsTable() {
        ConsoleUtils.subHeader("Grade Results")
        println()

        val widths = listOf(4, 22, 8, 4, 6)
        val labels = listOf("ID", "Name", "Average", "Grd", "Status")
        ConsoleUtils.tableHeader(labels, widths)

        results.forEach { result ->
            val cells = listOf(
                result.student.id.toString(),
                result.student.name,
                "%6.2f".format(result.average),
                result.letter,
                result.status
            )

            // Lambda decides row colour based on pass/fail
            val rowColour: (GradeResult) -> String = { r ->
                if (r.passed) Colours.GREEN else Colours.RED
            }

            ConsoleUtils.tableRow(cells, widths, rowColour(result))
        }

        ConsoleUtils.tableFooter(widths)
        println()
    }

    private fun printStatistics() {
        val stats = calculator.statistics(results)
        if (stats.isEmpty()) return

        ConsoleUtils.subHeader("Class Statistics")
        println()
        println("  Highest Average : ${"%.2f".format(stats["highest"])}")
        println("  Lowest  Average : ${"%.2f".format(stats["lowest"])}")
        println("  Class   Average : ${"%.2f".format(stats["classAvg"])}")
        println("  Pass    Rate    : ${"%.1f".format(stats["passRate"])}%")
        println()

        // Lambda-based filtering to show breakdown by grade letter
        ConsoleUtils.subHeader("Grade Distribution")
        println()
        val byGrade = results.groupBy { it.letter }                         // lambda
                             .toSortedMap(compareBy { gradeOrder(it) })    // lambda comparator
        byGrade.forEach { (letter, list) ->
            val bar  = "█".repeat(list.size)
            val coloured = ConsoleUtils.colourGrade(letter)
            println("  %2s │ %-20s │ ${list.size} student(s)".format(coloured, bar))
        }
        println()

        // Show only failing students using a lambda predicate + filter (inherited HOF)
        val failed = calculator.filter(results) { !it.passed }   // passing lambda
        if (failed.isNotEmpty()) {
            ConsoleUtils.subHeader("Students Who Did Not Pass")
            println()
            failed.forEach { r ->
                println("  • %-22s  Avg: %5.2f  Grade: %s".format(
                    r.student.name, r.average, ConsoleUtils.colourGrade(r.letter)
                ))
            }
            println()
        }
    }

    private fun exportMenu() {
        ConsoleUtils.subHeader("Export Options")
        println()
        println("  [1] Export to CSV  (plain text, spreadsheet-compatible)")
        println("  [2] Export to HTML (web-viewable report)")
        println("  [3] Export to XML  (structured data)")
        println("  [4] Export to PDF  (note: requires Apache PDFBox in real project)")
        println("  [5] Show full results again")
        println("  [0] Back to Home")
        println()

        while (true) {
            val choice = ConsoleUtils.readLine("Your choice: ")
            when (choice) {
                "1" -> exportCsv()
                "2" -> exportHtml()
                "3" -> exportXml()
                "4" -> ConsoleUtils.info("PDF export requires Apache PDFBox / OpenPDF library. " +
                                          "In a real project, ExportManager.toPdf() handles this.")
                "5" -> show()
                "0" -> return
                else -> ConsoleUtils.error("Invalid option.")
            }
        }
    }

    // ── Export helpers ───────────────────────────────────────────────────────

    private fun exportCsv() {
        val path = ConsoleUtils.readLine("Save file as (e.g. results.csv): ")
        val out  = buildString {
            appendLine("ID,Name,Average,Letter,Status")
            results.forEach { r ->
                appendLine("${r.student.id},${r.student.name},${"%.2f".format(r.average)},${r.letter},${r.status}")
            }
        }
        java.io.File(path).writeText(out)
        ConsoleUtils.success("CSV saved to: $path")
    }

    private fun exportHtml() {
        val path = ConsoleUtils.readLine("Save file as (e.g. results.html): ")

        // Lambda builds each table row
        val rows = results.joinToString("\n") { r ->
            val colour = if (r.passed) "#d4edda" else "#f8d7da"
            "    <tr style='background:$colour'><td>${r.student.id}</td><td>${r.student.name}</td>" +
            "<td>${"%.2f".format(r.average)}</td><td>${r.letter}</td><td>${r.status}</td></tr>"
        }

        val html = """
<!DOCTYPE html>
<html lang="en">
<head><meta charset="UTF-8"><title>Grade Results</title>
<style>
  body { font-family: Arial, sans-serif; margin: 30px; }
  h1   { color: #333; }
  table{ border-collapse: collapse; width: 100%; }
  th,td{ border: 1px solid #ccc; padding: 8px 12px; text-align: left; }
  th   { background: #4a90d9; color: #fff; }
</style></head>
<body>
<h1>Grade Calculator — Results</h1>
<p>Generated: ${java.time.LocalDateTime.now()}</p>
<table>
  <tr><th>ID</th><th>Name</th><th>Average</th><th>Grade</th><th>Status</th></tr>
$rows
</table>
</body></html>""".trimIndent()

        java.io.File(path).writeText(html)
        ConsoleUtils.success("HTML saved to: $path")
    }

    private fun exportXml() {
        val path = ConsoleUtils.readLine("Save file as (e.g. results.xml): ")

        // Lambda maps result → XML element string
        val elements = results.joinToString("\n") { r ->
            """  <student id="${r.student.id}">
    <name>${r.student.name}</name>
    <average>${"%.2f".format(r.average)}</average>
    <letter>${r.letter}</letter>
    <status>${r.status}</status>
  </student>"""
        }

        val xml = """<?xml version="1.0" encoding="UTF-8"?>
<results generated="${java.time.LocalDateTime.now()}">
$elements
</results>""".trimIndent()

        java.io.File(path).writeText(xml)
        ConsoleUtils.success("XML saved to: $path")
    }

    // Helper: sort grade letters in logical order
    private fun gradeOrder(letter: String) = when (letter) {
        "A+" -> 0; "A"  -> 1; "B+" -> 2
        "B"  -> 3; "C"  -> 4; "D"  -> 5
        else -> 6
    }
}
