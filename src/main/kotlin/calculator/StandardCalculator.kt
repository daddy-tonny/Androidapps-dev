package calculator

class StandardCalculator(passThreshold: Double = 40.0) : BaseCalculator(passThreshold) {
    override fun toLetter(average: Double): String = when {
        average >= 90 -> "A+"
        average >= 80 -> "A"
        average >= 70 -> "B+"
        average >= 60 -> "B"
        average >= 50 -> "C"
        average >= 40 -> "D"
        else          -> "F"
    }
}
