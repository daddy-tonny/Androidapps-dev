package model

/**
 * Stores the computed grade result for a single student.
 * @param student  The source Student.
 * @param average  The computed average of valid scores.
 * @param letter   The letter grade derived from the average.
 * @param passed   Whether the student passed (average >= threshold).
 */
data class GradeResult(
    val student: Student,
    val average: Double,
    val letter: String,
    val passed: Boolean
) {
    val status: String get() = if (passed) "PASS" else "FAIL"

    override fun toString(): String =
        "GradeResult(name='${student.name}', average=${"%.2f".format(average)}, letter=$letter, status=$status)"
}
