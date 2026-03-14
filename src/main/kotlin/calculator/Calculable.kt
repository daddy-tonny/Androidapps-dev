package calculator

import model.GradeResult
import model.Student

/**
 * Interface defining the contract for any grade calculator.
 * Demonstrates interface / abstraction usage.
 */
interface Calculable {

    /** Pass/fail threshold — default 40, can be overridden */
    val passThreshold: Double

    /**
     * Computes a [GradeResult] for a single [Student].
     */
    fun calculate(student: Student): GradeResult

    /**
     * Batch-calculates results for a list of students.
     * Default implementation uses a lambda + map — subclasses may override.
     */
    fun calculateAll(students: List<Student>): List<GradeResult> =
        students.map { calculate(it) }       // lambda passed to higher-order fun

    /**
     * Converts a numeric average to a letter grade.
     */
    fun toLetter(average: Double): String

    /**
     * Returns a summary line for a [GradeResult].
     * Lambda used as a formatting strategy.
     */
    fun formatResult(
        result: GradeResult,
        formatter: (GradeResult) -> String = { r ->
            "%-20s | Avg: %6.2f | %2s | %s".format(
                r.student.name, r.average, r.letter, r.status
            )
        }
    ): String = formatter(result)
}
