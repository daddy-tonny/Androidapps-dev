package model

data class Student(
    val name:   String,
    val scores: List<Double>
) {
    val validScores: List<Double> get() = scores.filter { it >= 0 }
    val average: Double get() = if (validScores.isEmpty()) 0.0 else validScores.sum() / validScores.size
}
