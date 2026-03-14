package calculator

import model.GradeResult
import model.Student

abstract class BaseCalculator(override val passThreshold: Double = 40.0) : Calculable {

    override fun calculate(student: Student): GradeResult {
        val avg = student.average
        return GradeResult(student, avg, toLetter(avg), avg >= passThreshold)
    }

    override fun calculateAll(students: List<Student>): List<GradeResult> =
        students.map { calculate(it) }
            .sortedByDescending { it.average }
            .mapIndexed { i, r -> r.copy(rank = i + 1) }
}
