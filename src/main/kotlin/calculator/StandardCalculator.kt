package calculator

/**
 * Standard calculator with default pass threshold of 40.
 * Demonstrates concrete subclass inheriting from [BaseCalculator].
 */
class StandardCalculator(override val passThreshold: Double = 40.0) : BaseCalculator() {

    companion object {
        /** Factory lambda — returns a StandardCalculator with a custom threshold */
        val withThreshold: (Double) -> StandardCalculator = { threshold ->
            StandardCalculator(threshold)
        }
    }
}

/**
 * Strict calculator that uses a higher pass threshold (50).
 * Demonstrates inheritance and overriding a property.
 */
class StrictCalculator : BaseCalculator() {
    override val passThreshold: Double = 50.0

    /** Override toLetter to add an A+ band for scores >= 90 */
    override fun toLetter(average: Double): String =
        when {
            average >= 90 -> "A+"
            else          -> super.toLetter(average)
        }
}
