package model

/**
 * Represents a student with a name and a list of numeric scores.
 * @param name The student's full name.
 * @param scores A list of numeric scores (can be empty or have nulls for missing).
 */
data class Student(
    val id: Int,
    val name: String,
    val scores: List<Double?>   // nullable to handle missing/invalid entries
) {
    /** Returns number of valid (non-null) scores */
    val validScoreCount: Int get() = scores.count { it != null }

    /** Returns only valid scores */
    val validScores: List<Double> get() = scores.filterNotNull()

    override fun toString(): String =
        "Student(id=$id, name='$name', scores=${scores.map { it?.toString() ?: "N/A" }})"
}
