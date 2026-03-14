package calculator

import model.GradeResult
import model.Student

/**
 * Abstract base calculator that implements shared logic.
 * Subclasses must provide [passThreshold] and may override [toLetter].
 *
 * Demonstrates:
 *  - Abstract class + inheritance
 *  - Lambda expressions
 *  - Higher-order functions
 */
abstract class BaseCalculator : Calculable {

    // ── Grade-band table as a list of (minScore, letter) pairs ──────────────
    private val gradeBands: List<Pair<Double, String>> = listOf(
        80.0 to "A",
        70.0 to "B+",
        60.0 to "B",
        50.0 to "C",
        40.0 to "D"
    )

    /**
     * Converts an average to a letter grade using a lambda-driven search.
     * Demonstrates lambda + firstOrNull usage.
     */
    override fun toLetter(average: Double): String =
        gradeBands.firstOrNull { (min, _) -> average >= min }?.second ?: "F"

    /**
     * Core calculation: computes average, letter, and pass/fail.
     * Missing scores are skipped gracefully.
     */
    override fun calculate(student: Student): GradeResult {
        val avg = if (student.validScores.isEmpty()) 0.0
                  else student.validScores.average()   // stdlib higher-order fn

        return GradeResult(
            student = student,
            average = avg,
            letter  = toLetter(avg),
            passed  = avg >= passThreshold
        )
    }

    /**
     * Returns statistics for a batch of results using lambdas.
     */
    fun statistics(results: List<GradeResult>): Map<String, Double> {
        if (results.isEmpty()) return emptyMap()
        val averages = results.map { it.average }   // lambda
        return mapOf(
            "highest"   to averages.maxOrNull()!!,
            "lowest"    to averages.minOrNull()!!,
            "classAvg"  to averages.average(),
            "passRate"  to (results.count { it.passed }.toDouble() / results.size * 100)
        )
    }

    /**
     * Filters results using a predicate lambda — demonstrates HOF usage.
     */
    fun filter(results: List<GradeResult>, predicate: (GradeResult) -> Boolean): List<GradeResult> =
        results.filter(predicate)
}
