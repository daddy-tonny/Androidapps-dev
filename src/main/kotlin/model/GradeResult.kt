package model

data class GradeResult(
    val student:  Student,
    val average:  Double,
    val letter:   String,
    val isPassed: Boolean,
    val rank:     Int = 0
)
