package calculator

class StrictCalculator(passThreshold: Double = 50.0) : BaseCalculator(passThreshold) {
    override fun toLetter(average: Double): String = when {
        average >= 95 -> "A+"
        average >= 85 -> "A"
        average >= 75 -> "B+"
        average >= 65 -> "B"
        average >= 55 -> "C"
        average >= 50 -> "D"
        else          -> "F"
    }
}
