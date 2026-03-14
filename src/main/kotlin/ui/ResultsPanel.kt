package ui

import calculator.StandardCalculator
import export.Exporter
import model.GradeResult
import model.Student
import java.awt.*
import java.io.File
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.border.MatteBorder
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class ResultsPanel(
    private val onBack:    () -> Unit,
    private val onRestart: () -> Unit
) : JPanel(BorderLayout()) {

    private var results: List<GradeResult> = emptyList()
    private val tableModel = DefaultTableModel()
    private val table      = buildTable()
    private val statsRow   = JPanel(GridLayout(1, 4, 12, 0)).apply { isOpaque = false }
    private val chart      = DistributionChart()

    init {
        isOpaque = true
        background = AppTheme.BG_DARK
        border = EmptyBorder(36, 52, 28, 52)
        buildUI()
    }

    fun loadResults(students: List<Student>) {
        results = StandardCalculator().calculateAll(students)
        refreshStats()
        refreshTable()
        chart.setResults(results)
    }

    private fun buildUI() {
        val root = JPanel(BorderLayout(0, 18)).apply { isOpaque = false }
        root.add(buildHeader(), BorderLayout.NORTH)

        val body = JPanel(BorderLayout(0, 14)).apply { isOpaque = false }
        body.add(statsRow, BorderLayout.NORTH)

        val mid = JPanel(BorderLayout(14, 0)).apply { isOpaque = false }
        mid.add(buildTableCard(), BorderLayout.CENTER)
        mid.add(chart, BorderLayout.EAST)
        body.add(mid, BorderLayout.CENTER)

        root.add(body, BorderLayout.CENTER)
        root.add(buildFooter(), BorderLayout.SOUTH)
        add(root, BorderLayout.CENTER)
    }

    private fun buildHeader(): JPanel {
        val h = JPanel(BorderLayout()).apply { isOpaque = false; border = EmptyBorder(0, 0, 2, 0) }
        val left = JPanel().apply {
            isOpaque = false; layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JLabel("Results").apply {
                font = AppTheme.FONT_TITLE; foreground = AppTheme.TEXT_WHITE
            })
            add(Box.createVerticalStrut(5))
            add(JLabel("Ranked by average score  ·  All grades computed").apply {
                font = AppTheme.FONT_BODY; foreground = AppTheme.TEXT_MUTED
            })
        }
        val btns = JPanel(FlowLayout(FlowLayout.RIGHT, 8, 0)).apply { isOpaque = false }
        btns.add(AppTheme.dangerButton("↺  Start Over").apply { addActionListener { onRestart() } })
        btns.add(AppTheme.secondaryButton("‹  Preview").apply   { addActionListener { onBack() } })
        h.add(left, BorderLayout.WEST)
        h.add(btns, BorderLayout.EAST)
        return h
    }

    private fun buildTableCard(): JPanel = object : JPanel(BorderLayout()) {
        override fun paintComponent(g: Graphics) {
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.paint = GradientPaint(0f, 0f, AppTheme.BG_CARD, 0f, height.toFloat(), AppTheme.BG_SURFACE)
            g2.fillRoundRect(0, 0, width, height, 14, 14)
            g2.color = AppTheme.BORDER_MID
            g2.drawRoundRect(0, 0, width - 1, height - 1, 14, 14)
        }
    }.apply {
        isOpaque = false
        add(AppTheme.styledScroll(table), BorderLayout.CENTER)
    }

    private fun buildFooter(): JPanel {
        val f = JPanel(FlowLayout(FlowLayout.RIGHT, 8, 0)).apply {
            isOpaque = false; border = EmptyBorder(8, 0, 0, 0)
        }
        f.add(JLabel("Export:").apply { font = AppTheme.FONT_BODY; foreground = AppTheme.TEXT_MUTED })
        listOf("Excel" to "xlsx", "PDF" to "pdf", "HTML" to "html",
            "XML"   to "xml",  "CSV" to "csv").forEach { (lbl, ext) ->
            f.add(AppTheme.secondaryButton("↓ $lbl").apply { addActionListener { exportAs(ext) } })
        }
        return f
    }

    private fun buildTable(): JTable = object : JTable(tableModel) {
        override fun isCellEditable(r: Int, c: Int) = false
        override fun getRowHeight() = 40
    }.apply {
        background = AppTheme.BG_CARD; foreground = AppTheme.TEXT_PRIMARY
        gridColor  = AppTheme.BORDER;  selectionBackground = AppTheme.BG_HOVER
        selectionForeground = AppTheme.TEXT_WHITE; font = AppTheme.FONT_BODY
        showHorizontalLines = true; showVerticalLines = false
        intercellSpacing = Dimension(0, 0)
        tableHeader.apply {
            background = AppTheme.BG_NAV; foreground = AppTheme.TEXT_MUTED
            font = AppTheme.FONT_BADGE
            border = MatteBorder(0, 0, 1, 0, AppTheme.BORDER_MID)
            reorderingAllowed = false
        }
        setDefaultRenderer(Any::class.java, object : DefaultTableCellRenderer() {
            override fun getTableCellRendererComponent(
                tbl: JTable, value: Any?, sel: Boolean, foc: Boolean, row: Int, col: Int
            ): Component {
                val c = super.getTableCellRendererComponent(tbl, value, sel, foc, row, col) as JLabel
                c.background = if (sel) AppTheme.BG_HOVER else AppTheme.BG_CARD
                c.border = EmptyBorder(0, 14, 0, 14)
                c.horizontalAlignment = when (col) {
                    0, 3, 4, 5 -> SwingConstants.CENTER
                    else        -> SwingConstants.LEFT
                }
                when (col) {
                    0 -> { c.foreground = AppTheme.TEXT_DIM;   c.font = AppTheme.FONT_SMALL }
                    1 -> { c.foreground = AppTheme.TEXT_WHITE; c.font = Font("Segoe UI Semibold", Font.BOLD, 13) }
                    2 -> { c.foreground = AppTheme.TEXT_DIM;   c.font = AppTheme.FONT_MONO }
                    3 -> { c.foreground = AppTheme.CYAN;       c.font = Font("Segoe UI", Font.BOLD, 13) }
                    4 -> {
                        val grade = value?.toString() ?: ""
                        c.foreground = AppTheme.gradeColor(grade)
                        c.font = Font("Segoe UI", Font.BOLD, 15)
                    }
                    5 -> {
                        c.foreground = if (value?.toString() == "PASS") AppTheme.GREEN else AppTheme.RED
                        c.font = AppTheme.FONT_BADGE
                    }
                }
                return c
            }
        })
        // ✅ fixed: removed columnModel.getColumn() lines from here
    }

    private fun refreshStats() {
        statsRow.removeAll()
        if (results.isEmpty()) return
        val avg  = results.sumOf { it.average } / results.size
        val high = results.maxOf { it.average }
        val low  = results.minOf { it.average }
        val pct  = (results.count { it.isPassed } * 100.0 / results.size).toInt()
        statsRow.add(AppTheme.statCard("${"%.1f".format(avg)}",  "Class Average", AppTheme.COBALT))
        statsRow.add(AppTheme.statCard("${"%.1f".format(high)}", "Highest Score", AppTheme.GREEN))
        statsRow.add(AppTheme.statCard("${"%.1f".format(low)}",  "Lowest Score",  AppTheme.AMBER))
        statsRow.add(AppTheme.statCard("$pct%",                  "Pass Rate",     AppTheme.CYAN))
        statsRow.revalidate(); statsRow.repaint()
    }

    private fun refreshTable() {
        tableModel.setRowCount(0); tableModel.setColumnCount(0)
        tableModel.setColumnIdentifiers(arrayOf("#", "Name", "Scores", "Avg", "Grade", "Status"))
        results.forEach { r ->
            tableModel.addRow(arrayOf(
                r.rank,
                r.student.name,
                r.student.scores.joinToString("  ") { if (it < 0) "—" else "%.1f".format(it) },
                "%.2f".format(r.average),
                r.letter,
                if (r.isPassed) "PASS" else "FAIL"
            ))
        }
        // ✅ fixed: moved here — columns exist now, safe to resize
        table.columnModel.getColumn(0).apply { preferredWidth = 44; maxWidth = 52 }
        table.columnModel.getColumn(4).apply { preferredWidth = 64; maxWidth = 72 }
        table.columnModel.getColumn(5).apply { preferredWidth = 72; maxWidth = 80 }
    }

    private fun exportAs(ext: String) {
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No results to export.", "Warning", JOptionPane.WARNING_MESSAGE)
            return
        }
        val fc = JFileChooser().apply {
            dialogTitle = "Save as ${ext.uppercase()}"
            val filter = FileNameExtensionFilter("${ext.uppercase()} File", ext)
            addChoosableFileFilter(filter); fileFilter = filter
            selectedFile = File("grade_results.$ext")
        }
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return
        var f = fc.selectedFile
        if (!f.name.endsWith(".$ext")) f = File("${f.absolutePath}.$ext")
        runCatching {
            when (ext) {
                "xlsx" -> Exporter.toExcel(results, f)
                "pdf"  -> Exporter.toPdf(results, f)
                "html" -> Exporter.toHtml(results, f)
                "xml"  -> Exporter.toXml(results, f)
                else   -> Exporter.toCsv(results, f)
            }
            JOptionPane.showMessageDialog(this,
                "Exported successfully to:\n${f.absolutePath}", "Export Complete",
                JOptionPane.INFORMATION_MESSAGE)
        }.onFailure {
            JOptionPane.showMessageDialog(this, it.message, "Export Error", JOptionPane.ERROR_MESSAGE)
        }
    }
}

// ── Grade Distribution Chart ──────────────────────────────────────────────────
class DistributionChart : JPanel() {

    private var results: List<GradeResult> = emptyList()
    private val grades = listOf("A+", "A", "B+", "B", "C", "D", "F")

    init {
        isOpaque = false
        preferredSize = Dimension(234, 0)
    }

    fun setResults(r: List<GradeResult>) { results = r; repaint() }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        g2.paint = GradientPaint(0f, 0f, AppTheme.BG_CARD, 0f, height.toFloat(), AppTheme.BG_SURFACE)
        g2.fillRoundRect(0, 0, width, height, 14, 14)
        g2.color = AppTheme.BORDER_MID
        g2.drawRoundRect(0, 0, width - 1, height - 1, 14, 14)

        val pad    = 18
        val topPad = 46
        val data   = grades.associateWith { gr -> results.count { it.letter == gr } }
        val maxVal = data.values.maxOrNull()?.coerceAtLeast(1) ?: 1
        val barH   = ((height - topPad - pad * 2) / grades.size - 7).coerceAtLeast(10)

        g2.font  = AppTheme.FONT_HEADING
        g2.color = AppTheme.ACCENT_LIGHT
        g2.drawString("Distribution", pad, 30)

        grades.forEachIndexed { i, gr ->
            val cnt   = data[gr] ?: 0
            val y     = topPad + i * (barH + 7)
            val avail = width - pad * 2 - 46
            val barW  = (avail * cnt.toDouble() / maxVal).toInt().coerceAtLeast(if (cnt > 0) 6 else 0)
            val color = AppTheme.gradeColor(gr)
            val bx    = pad + 38

            g2.font  = AppTheme.FONT_BADGE
            g2.color = color
            g2.drawString(gr, pad, y + barH - 3)

            g2.color = Color(color.red, color.green, color.blue, 18)
            g2.fillRoundRect(bx, y, avail, barH, 5, 5)

            if (cnt > 0) {
                g2.paint = GradientPaint(
                    bx.toFloat(), 0f, Color(color.red, color.green, color.blue, 190),
                    (bx + barW).toFloat(), 0f, color
                )
                g2.fillRoundRect(bx, y, barW, barH, 5, 5)
                g2.font  = AppTheme.FONT_TINY
                g2.color = color
                g2.drawString("$cnt", bx + barW + 5, y + barH - 3)
            }
        }

        if (results.isNotEmpty()) {
            g2.font  = AppTheme.FONT_TINY
            g2.color = AppTheme.TEXT_DIM
            g2.drawString("${results.size} students total", pad, height - 8)
        }
    }
}