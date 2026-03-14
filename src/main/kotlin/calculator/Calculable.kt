package calculator

import model.GradeResult
import model.Student

interface Calculable {
    val passThreshold: Double
    fun calculate(student: Student): GradeResult
    fun calculateAll(students: List<Student>): List<GradeResult> = students.map { calculate(it) }
    fun toLetter(average: Double): String
}
