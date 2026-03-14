package ui

import model.Student
import util.ConsoleUtils
import util.ExcelReader

/**
 * Home Screen — entry point of the app.
 * Lets the user load student data from a file or use the sample dataset.
 *
 * Demonstrates:
 *  - Class with a method that returns data
 *  - Lambda used for menu dispatch
 */
class HomeScreen {

    fun show(): List<Student> {
        ConsoleUtils.header("GRADE CALCULATOR  ─  Home")

        println("""
  Welcome! This app calculates student grades from score data.

  Expected data format (CSV or Excel):
  ┌──────────────┬─────────┬─────────┬─────────┐
  │ Name         │ Score 1 │ Score 2 │ Score 3 │
  ├──────────────┼─────────┼─────────┼─────────┤
  │ Alice        │ 85      │ 90      │ 78      │
  │ Bob          │ 70      │ 68      │ 72      │
  └──────────────┴─────────┴─────────┴─────────┘

  Grade Scale:
    A  → 80–100    B+ → 70–79    B → 60–69
    C  → 50–59     D  → 40–49    F → below 40

  Pass threshold: 40 (customisable)
        """.trimIndent())

        // Lambda dispatch map for menu options
        val menuActions: Map<String, () -> List<Student>> = mapOf(
            "1" to { loadFromFile() },
            "2" to { ExcelReader.sampleData().also { ConsoleUtils.success("Sample data loaded — ${it.size} students.") } },
            "0" to { exitApp() }
        )

        while (true) {
            println()
            ConsoleUtils.subHeader("Main Menu")
            println("  [1] Load from CSV / Excel file")
            println("  [2] Use built-in sample data")
            println("  [0] Exit")
            println()

            val choice = ConsoleUtils.readLine("Your choice: ")
            val action = menuActions[choice]

            if (action != null) {
                val students = action()
                if (students.isNotEmpty()) return students
            } else {
                ConsoleUtils.error("Invalid choice. Please enter 1, 2, or 0.")
            }
        }
    }

    /** Prompts for a file path and delegates to [ExcelReader] */
    private fun loadFromFile(): List<Student> {
        val path = ConsoleUtils.readLine("Enter full file path (.csv): ")
        val result = ExcelReader.readFromFile(path)

        return result.fold(
            onSuccess = { students ->
                ConsoleUtils.success("Loaded ${students.size} students from: $path")
                students
            },
            onFailure = { err ->
                ConsoleUtils.error("Could not load file: ${err.message}")
                emptyList()
            }
        )
    }

    private fun exitApp(): List<Student> {
        ConsoleUtils.info("Goodbye! 👋")
        kotlin.system.exitProcess(0)
    }
}
