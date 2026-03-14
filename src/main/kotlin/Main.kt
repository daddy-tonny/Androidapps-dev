import calculator.StandardCalculator
import calculator.StrictCalculator
import model.Student
import ui.HomeScreen
import ui.PreviewScreen
import ui.ResultsScreen
import util.Colours
import util.ConsoleUtils

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║           GRADE CALCULATOR — Kotlin Console App             ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  OOP concepts demonstrated:                                 ║
 * ║    • Classes, Data Classes, Abstract Classes                ║
 * ║    • Inheritance  (BaseCalculator → Standard/StrictCalc)    ║
 * ║    • Interfaces   (Calculable)                              ║
 * ║    • Compa
 * nion Object (factory lambda in StandardCalculator) ║
 * ║    • Lambda expressions & higher-order functions            ║
 * ║    • Extension-style helper objects                         ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
fun main() {
    printBanner()

    // ── Select calculator mode ────────────────────────────────────────────────
    val calculator = chooseCalculator()

    // ── Navigation loop: Home → Preview → Results ─────────────────────────────
    var students: List<Student> = emptyList()

    while (true) {
        // Home Screen: load or reload data
        students = HomeScreen().show()

        // Preview Screen: confirm before computing
        val proceed = PreviewScreen(students).show()
        if (!proceed) continue   // user chose "go back"

        // Calculate grades using the chosen calculator
        ConsoleUtils.info("Computing grades…")
        val results = calculator.calculateAll(students)   // uses interface default HOF
        ConsoleUtils.success("Done! ${results.size} results computed.\n")

        // Results Screen: display + export
        ResultsScreen(results, calculator).show()

        // After results, ask whether to start over
        println()
        val again = ConsoleUtils.readLine("Load another file? [y/N]: ")
        if (again.lowercase() != "y") {
            ConsoleUtils.info("Thank you for using Grade Calculator. Goodbye! 👋")
            break
        }
    }
}

/**
 * Prompts the user to choose a grading mode.
 * Uses a lambda map for dispatch — same pattern as HomeScreen.
 */
fun chooseCalculator() = run {
    println("${Colours.CYAN}Select grading mode:${Colours.RESET}")
    println("  [1] Standard  (pass ≥ 40, grades A / B+ / B / C / D / F)")
    println("  [2] Strict    (pass ≥ 50, adds A+ for ≥ 90)")
    println("  [3] Custom    (set your own pass threshold)")
    println()

    // Lambda map for calculator selection
    val modes: Map<String, () -> calculator.BaseCalculator> = mapOf(
        "1" to { StandardCalculator() },
        "2" to { StrictCalculator() },
        "3" to {
            val t = ConsoleUtils.readLine("Enter pass threshold (0–100): ").toDoubleOrNull() ?: 40.0
            StandardCalculator.withThreshold(t)  // factory lambda from companion object
        }
    )

    var calc: calculator.BaseCalculator? = null
    while (calc == null) {
        val input = ConsoleUtils.readLine("Choice [1/2/3]: ")
        calc = modes[input]?.invoke()
        if (calc == null) ConsoleUtils.error("Please enter 1, 2, or 3.")
    }
    ConsoleUtils.success("Calculator ready. Pass threshold: ${calc.passThreshold}\n")
    calc
}

fun printBanner() {
    println("""
${Colours.CYAN}${Colours.BOLD}
  ██████╗ ██████╗  █████╗ ██████╗ ███████╗
 ██╔════╝ ██╔══██╗██╔══██╗██╔══██╗██╔════╝
 ██║  ███╗██████╔╝███████║██║  ██║█████╗  
 ██║   ██║██╔══██╗██╔══██║██║  ██║██╔══╝  
 ╚██████╔╝██║  ██║██║  ██║██████╔╝███████╗
  ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚═════╝ ╚══════╝
   CALCULATOR  ─  Kotlin Console Edition
${Colours.RESET}
  Kotlin OOP Demo: Classes • Inheritance • Lambdas • HOFs
    """.trimIndent())
}
