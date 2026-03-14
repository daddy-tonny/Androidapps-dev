package util

import model.Student

/**
 * Simulates reading student data from an Excel (.xlsx) file.
 *
 * In a real project this would use Apache POI:
 *   val workbook = XSSFWorkbook(FileInputStream(path))
 *
 * For the console demo we provide:
 *   1. A CSV-like file parser (reads a plain text file that mimics Excel layout).
 *   2. A built-in sample dataset so the app runs without any file at all.
 */
object ExcelReader {

    /**
     * Parses a simple CSV/tab-separated file that mirrors the Excel format:
     *   Name, Score1, Score2, Score3, ...
     * First row is treated as a header and skipped.
     *
     * Lambda used for line-to-Student transformation.
     */
    fun readFromFile(path: String): Result<List<Student>> = runCatching {
        val file = java.io.File(path)
        require(file.exists()) { "File not found: $path" }

        val lines = file.readLines().drop(1)  // skip header

        // Lambda: maps each CSV line → Student
        val toStudent: (IndexedValue<String>) -> Student? = { (index, line) ->
            val parts = line.split(",").map { it.trim() }
            if (parts.size < 2) null
            else {
                val name   = parts[0]
                val scores = parts.drop(1).map { it.toDoubleOrNull() }  // null for invalid
                Student(id = index + 1, name = name, scores = scores)
            }
        }

        lines.withIndex()
             .mapNotNull(toStudent)   // passing lambda to HOF
             .also { require(it.isNotEmpty()) { "No valid student rows found." } }
    }

    /**
     * Returns a hardcoded sample dataset so the demo works out-of-the-box.
     * Lambda sorts students by name for consistent ordering.
     */
    fun sampleData(): List<Student> = listOf(
        Student(1,  "Alice Kamga",      listOf(85.0, 90.0, 78.0, 92.0)),
        Student(2,  "Bob Nguene",       listOf(70.0, 68.0, 72.0, null)),
        Student(3,  "Carol Mbida",      listOf(55.0, 60.0, 58.0, 62.0)),
        Student(4,  "David Ateba",      listOf(45.0, 40.0, 38.0, 42.0)),
        Student(5,  "Eva Bello",        listOf(30.0, 25.0, 35.0, 28.0)),
        Student(6,  "Frank Ondoa",      listOf(95.0, 98.0, 100.0, 97.0)),
        Student(7,  "Grace Nkomo",      listOf(63.0, 71.0, null, 68.0)),
        Student(8,  "Henry Fon",        listOf(50.0, 52.0, 48.0, 55.0)),
        Student(9,  "Irene Djomo",      listOf(null, null, null, null)),  // all missing
        Student(10, "James Tabi",       listOf(77.0, 80.0, 75.0, 82.0))
    ).sortedBy { it.name }   // lambda sorts by name
}
