# Grade Calculator — Dark Blue GUI

A professional Kotlin + Swing desktop app with a deep dark-blue theme.

---

## ▶ How to Open & Run in IntelliJ IDEA (3 steps)

1. **File → Open** → select the `GradeCalcGUI` folder
2. Click **"Load Gradle Project"** when prompted (bottom-right popup)
3. Wait for indexing → open `src/main/kotlin/Main.kt` → click the green **▶** → **Run 'MainKt'**

> **Requirements:** JDK 21 · Kotlin 1.9+ · IntelliJ IDEA 2023+

---

## Features

| Feature | Detail |
|---|---|
| 📂 Import | Excel (.xlsx/.xls), CSV, PDF via file browser or drag & drop |
| 👁 Preview | Review all student data before computing |
| 🎓 Grades | A+ / A / B+ / B / C / D / F with configurable pass threshold |
| 📊 Statistics | Class average, highest, lowest, pass rate |
| 📈 Chart | Live grade distribution bar chart |
| 💾 Export | Excel, PDF, HTML, XML, CSV |

---

## Project Structure

```
GradeCalcGUI/
├── build.gradle.kts                  ← Gradle build config
├── settings.gradle.kts               ← Project name
├── gradle/wrapper/
│   └── gradle-wrapper.properties     ← Gradle 8.7 wrapper
└── src/main/kotlin/
    ├── Main.kt                       ← Entry point + MainWindow + StepLabel
    ├── model/
    │   ├── Student.kt                ← Student data class
    │   └── GradeResult.kt            ← GradeResult data class
    ├── calculator/
    │   ├── Calculable.kt             ← Interface
    │   ├── BaseCalculator.kt         ← Abstract base
    │   ├── StandardCalculator.kt     ← Default grading (pass ≥ 40)
    │   └── StrictCalculator.kt       ← Strict grading (pass ≥ 50)
    ├── ui/
    │   ├── AppTheme.kt               ← Full dark-blue design system
    │   ├── HomePanel.kt              ← Home / import screen
    │   ├── PreviewPanel.kt           ← Data preview screen
    │   └── ResultsPanel.kt           ← Results + chart + export screen
    ├── util/
    │   └── FileImporter.kt           ← Excel, CSV, PDF import + sample data
    └── export/
        └── Exporter.kt               ← Excel, PDF, HTML, XML, CSV export
```

---

## Excel File Format

| Name  | Score 1 | Score 2 | Score 3 |
|-------|---------|---------|---------|
| Alice | 85      | 90      | 78      |
| Bob   | 70      | 68      | 72      |

- First row = headers (any column names work)
- First column = student name
- Remaining columns = numeric scores

---

## Grade Scale

| Grade | Range  |
|-------|--------|
| A+    | ≥ 90   |
| A     | 80–89  |
| B+    | 70–79  |
| B     | 60–69  |
| C     | 50–59  |
| D     | 40–49  |
| F     | < 40   |

Pass threshold: **40** (customizable in `StandardCalculator.kt`)
