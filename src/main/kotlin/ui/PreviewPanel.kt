package ui

import model.Student
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.border.MatteBorder
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class PreviewPanel(
    private val onBack:    () -> Unit,
    private val onCompute: (List<Student>) -> Unit
) : JPanel(BorderLayout()) {

    private var students: List<Student> = emptyList()
    private val tableModel = DefaultTableModel()
    private val table      = buildTable()
    private val infoLabel  = JLabel(" ").apply {
        font = AppTheme.FONT_SMALL; foreground = AppTheme.TEXT_MUTED
    }
    private val computeBtn = AppTheme.primaryButton("  Calculate Grades  ›")

    init {
        isOpaque = true; background = AppTheme.BG_DARK
        border = EmptyBorder(36, 52, 28, 52)
        buildUI()
    }

    fun loadStudents(list: List<Student>) { students = list; refresh() }

    private fun buildUI() {
        // Header
        val header = JPanel(BorderLayout()).apply { isOpaque = false; border = EmptyBorder(0, 0, 24, 0) }
        val left = JPanel().apply {
            isOpaque = false; layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JLabel("Preview Data").apply { font = AppTheme.FONT_TITLE; foreground = AppTheme.TEXT_WHITE })
            add(Box.createVerticalStrut(5))
            add(JLabel("Review all imported data before computing grades").apply {
                font = AppTheme.FONT_BODY; foreground = AppTheme.TEXT_MUTED })
        }
        val backBtn = AppTheme.secondaryButton("‹  Back").apply { addActionListener { onBack() } }
        header.add(left, BorderLayout.WEST); header.add(backBtn, BorderLayout.EAST)
        add(header, BorderLayout.NORTH)

        // Table card
        val tableCard = object : JPanel(BorderLayout()) {
            override fun paintComponent(g: Graphics) {
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                g2.paint = GradientPaint(0f, 0f, AppTheme.BG_CARD, 0f, height.toFloat(), AppTheme.BG_SURFACE)
                g2.fillRoundRect(0, 0, width, height, 14, 14)
                g2.color = AppTheme.BORDER_MID; g2.drawRoundRect(0, 0, width-1, height-1, 14, 14)
            }
        }.apply { isOpaque = false; add(AppTheme.styledScroll(table), BorderLayout.CENTER) }
        add(tableCard, BorderLayout.CENTER)

        // Footer
        val footer = JPanel(BorderLayout(12, 0)).apply {
            isOpaque = false; border = EmptyBorder(16, 0, 0, 0)
        }
        computeBtn.addActionListener {
            if (students.isEmpty()) JOptionPane.showMessageDialog(
                this, "No students loaded.", "Warning", JOptionPane.WARNING_MESSAGE)
            else onCompute(students)
        }
        footer.add(infoLabel, BorderLayout.CENTER); footer.add(computeBtn, BorderLayout.EAST)
        add(footer, BorderLayout.SOUTH)
    }

    private fun buildTable(): JTable = object : JTable(tableModel) {
        override fun isCellEditable(r: Int, c: Int) = false
        override fun getRowHeight() = 38
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
                tbl: JTable, value: Any?, sel: Boolean, foc: Boolean, row: Int, col: Int): Component {
                val c = super.getTableCellRendererComponent(tbl, value, sel, foc, row, col) as JLabel
                c.background = when {
                    sel          -> AppTheme.BG_HOVER
                    row % 2 == 1 -> AppTheme.BG_SURFACE
                    else         -> AppTheme.BG_CARD
                }
                c.foreground = if (col == 0) AppTheme.TEXT_WHITE else AppTheme.TEXT_MUTED
                c.font   = if (col == 0) Font("Segoe UI Semibold", Font.BOLD, 13) else AppTheme.FONT_MONO
                c.border = EmptyBorder(0, if (col == 0) 16 else 8, 0, 8)
                c.horizontalAlignment = if (col == 0) SwingConstants.LEFT else SwingConstants.CENTER
                return c
            }
        })
    }

    private fun refresh() {
        tableModel.setRowCount(0); tableModel.setColumnCount(0)
        if (students.isEmpty()) { infoLabel.text = " "; return }
        val max = students.maxOf { it.scores.size }
        tableModel.setColumnIdentifiers((listOf("Name") + (1..max).map { "Score $it" }).toTypedArray())
        students.forEach { s ->
            val row = Array<Any>(max + 1) { "" }
            row[0] = s.name
            s.scores.forEachIndexed { i, sc -> row[i+1] = if (sc < 0) "—" else "%.1f".format(sc) }
            tableModel.addRow(row)
        }
        val pct = (students.count { it.average >= 40 } * 100.0 / students.size).toInt()
        infoLabel.text = "${students.size} students  ·  $max score columns  ·  Est. pass rate: $pct%"
    }
}
