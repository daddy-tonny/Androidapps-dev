package ui

import model.Student
import util.Colours
import util.ConsoleUtils

/**
 * Preview Screen — shows all imported student data before computation.
 *
 * Demonstrates:
 *  - Class receiving data via constructor
 *  - Lambda used to format scores
 */
class PreviewScreen(private val students: List<Student>) {

    fun show(): Boolean {
        ConsoleUtils.header("GRADE CALCULATOR  ─  Data Preview")
        ConsoleUtils.info("${students.size} student(s) loaded. Review data below before computing grades.\n")

        printTable()

        println()
        ConsoleUtils.subHeader("Options")
        println("  [1] Proceed to calculate grades")
        println("  [2] Go back to Home Screen")
        println("  [0] Exit")
        println()

        while (true) {
            val choice = ConsoleUtils.readLine("Your choice: ")
            return when (choice) {
                "1"  -> true
                "2"  -> false
                "0"  -> { ConsoleUtils.info("Goodbye! 👋"); kotlin.system.exitProcess(0) }
                else -> { ConsoleUtils.error("Please enter 1, 2, or 0."); continue }
            }
        }
    }

    private fun printTable() {
        // Lambda: converts a score (nullable Double) to a display string
        val scoreStr: (Double?) -> String = { score ->
            score?.let { "%6.1f".format(it) } ?: "  N/A "
        }

        val widths = listOf(4, 22, 6, 6, 6, 6, 6, 9)
        val labels = listOf("ID", "Name", "Sc 1", "Sc 2", "Sc 3", "Sc 4", "Sc 5", "# Valid")
        ConsoleUtils.tableHeader(labels, widths)

        students.forEachIndexed { _, student ->
            // Pad or trim scores list to always show 5 columns
            val padded = (student.scores + List(5) { null }).take(5)
            val cells  = listOf(
                student.id.toString(),
                student.name,
                scoreStr(padded[0]),
                scoreStr(padded[1]),
                scoreStr(padded[2]),
                scoreStr(padded[3]),
                scoreStr(padded[4]),
                student.validScoreCount.toString()
            )

            // Colour row yellow if student has missing scores
            val rowColour = if (student.validScoreCount < student.scores.size)
                Colours.YELLOW else ""
            ConsoleUtils.tableRow(cells, widths, rowColour)
        }

        ConsoleUtils.tableFooter(widths)

        // Warn about students with all missing scores using a lambda filter
        val missingAll = students.filter { it.validScores.isEmpty() }
        if (missingAll.isNotEmpty()) {
            println()
            ConsoleUtils.error("Students with NO valid scores (will receive 0 average):")
            missingAll.forEach { ConsoleUtils.error("  • ${it.name}") }
        }
    }
}
