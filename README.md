# Grade Calculator — Gradle Edition

## How to Open in IntelliJ IDEA (3 steps only)

1. **File → Open** → select the `GradeCalculator` folder
2. IntelliJ detects `build.gradle.kts` automatically and sets up EVERYTHING
3. Open `src/main/kotlin/Main.kt` → click the green ▶ button → Run!

That's it. No manual SDK config, no Sources Root, no Kotlin setup needed.

---

## Project Structure

```
GradeCalculator/
├── build.gradle.kts              ← Gradle handles Kotlin + JDK automatically
├── settings.gradle.kts
├── sample_students.csv           ← Test data file
└── src/main/kotlin/
    ├── Main.kt                   ← Entry point — run this
    ├── model/
    │   ├── Student.kt
    │   └── GradeResult.kt
    ├── calculator/
    │   ├── Calculable.kt         ← Interface
    │   ├── BaseCalculator.kt     ← Abstract class
    │   └── StandardCalculator.kt ← Concrete subclasses
    ├── ui/
    │   ├── HomeScreen.kt
    │   ├── PreviewScreen.kt
    │   └── ResultsScreen.kt
    └── util/
        ├── ExcelReader.kt
        └── ConsoleUtils.kt
```

## Using the App
- Press **2** → use built-in sample data (no file needed)
- Press **1** → Standard grading mode
- Navigate: Home → Preview → Results
- Export results to CSV / HTML / XML
