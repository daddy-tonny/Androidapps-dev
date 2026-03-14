package ui

import model.Student
import util.FileImporter
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.filechooser.FileNameExtensionFilter

class HomePanel(private val onStudentsLoaded: (List<Student>) -> Unit) : JPanel(BorderLayout()) {

    private val statusLabel = JLabel(" ").apply {
        font = AppTheme.FONT_SMALL; foreground = AppTheme.TEXT_MUTED
        horizontalAlignment = SwingConstants.CENTER
    }

    init {
        isOpaque = true; background = AppTheme.BG_DARK
        border = EmptyBorder(36, 52, 28, 52)
        buildUI()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D
        val gp = RadialGradientPaint((width / 2).toFloat(), 100f, width * 0.55f,
            floatArrayOf(0f, 1f), arrayOf(Color(0x1E, 0x72, 0xFF, 20), Color(0, 0, 0, 0)))
        g2.paint = gp; g2.fillRect(0, 0, width, height)
    }

    private fun buildUI() {
        val header = JPanel().apply {
            isOpaque = false; layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = EmptyBorder(0, 0, 32, 0)
        }
        val titleRow = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)).apply { isOpaque = false }
        titleRow.add(JLabel("Grade Calculator").apply {
            font = Font("Segoe UI", Font.BOLD, 30); foreground = AppTheme.TEXT_WHITE
        })
        titleRow.add(Box.createHorizontalStrut(12))
        titleRow.add(object : JLabel("  PRO  ") {
            override fun paintComponent(g: Graphics) {
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                g2.paint = GradientPaint(0f, 0f, AppTheme.COBALT, width.toFloat(), 0f, AppTheme.CYAN)
                g2.fillRoundRect(0, 2, width, height - 4, 6, 6)
                super.paintComponent(g)
            }
        }.apply {
            font = Font("Segoe UI", Font.BOLD, 10); foreground = Color(0x03, 0x08, 0x12)
            border = EmptyBorder(0, 8, 0, 8)
        })
        header.add(titleRow)
        header.add(Box.createVerticalStrut(8))
        header.add(JLabel("Import student data  ·  Calculate grades  ·  Export results").apply {
            font = AppTheme.FONT_BODY; foreground = AppTheme.TEXT_MUTED
        })
        add(header, BorderLayout.NORTH)

        val centre = JPanel(GridBagLayout()).apply { isOpaque = false }
        val gbc = GridBagConstraints().apply {
            gridwidth = GridBagConstraints.REMAINDER; fill = GridBagConstraints.HORIZONTAL
            insets = Insets(8, 0, 8, 0)
        }
        centre.add(buildDropZone(), gbc)
        val infoRow = JPanel(GridLayout(1, 2, 14, 0)).apply { isOpaque = false }
        infoRow.add(buildFormatCard()); infoRow.add(buildGradeScaleCard())
        centre.add(infoRow, gbc)
        add(AppTheme.styledScroll(centre), BorderLayout.CENTER)

        val bottom = JPanel(BorderLayout(12, 0)).apply {
            isOpaque = false; border = EmptyBorder(16, 0, 0, 0)
        }
        bottom.add(statusLabel, BorderLayout.CENTER)
        bottom.add(AppTheme.secondaryButton("▶  Use Sample Data").apply {
            addActionListener {
                val s = FileImporter.sampleData()
                setStatus("✔  Sample data loaded — ${s.size} students", AppTheme.GREEN)
                onStudentsLoaded(s)
            }
        }, BorderLayout.EAST)
        add(bottom, BorderLayout.SOUTH)
    }

    private fun buildDropZone(): JPanel {
        val zone = object : JPanel(BorderLayout()) {
            var hovered = false
            override fun paintComponent(g: Graphics) {
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                if (hovered) {
                    g2.color = Color(0, 212, 255, 25)
                    g2.fillRoundRect(-5, -5, width+10, height+10, 22, 22)
                }
                g2.paint = GradientPaint(0f, 0f, AppTheme.BG_CARD, 0f, height.toFloat(), AppTheme.BG_SURFACE)
                g2.fillRoundRect(0, 0, width, height, 16, 16)
                g2.color  = if (hovered) AppTheme.CYAN else AppTheme.BORDER_BRIGHT
                g2.stroke = BasicStroke(if (hovered) 2f else 1.5f, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND, 0f, floatArrayOf(9f, 6f), 0f)
                g2.drawRoundRect(2, 2, width-4, height-4, 16, 16)
                if (hovered) {
                    g2.color = Color(0, 212, 255, 55); g2.stroke = BasicStroke(1f)
                    g2.drawLine(44, 2, width - 44, 2)
                }
            }
            init {
                addMouseListener(object : MouseAdapter() {
                    override fun mouseEntered(e: MouseEvent) { hovered = true; repaint() }
                    override fun mouseExited(e: MouseEvent)  { hovered = false; repaint() }
                })
            }
        }.apply {
            isOpaque = false; preferredSize = Dimension(0, 215)
            cursor = Cursor(Cursor.HAND_CURSOR)
        }

        val inner = JPanel(GridBagLayout()).apply { isOpaque = false }
        val gb = GridBagConstraints().apply { gridwidth = GridBagConstraints.REMAINDER }
        val btn = AppTheme.primaryButton("  Browse File  ")

        inner.add(JLabel("📂").apply { font = Font("Segoe UI Emoji", Font.PLAIN, 48) }, gb)
        inner.add(Box.createVerticalStrut(12), gb)
        inner.add(JLabel("Click to browse files or drag & drop here").apply {
            font = AppTheme.FONT_HEADING; foreground = AppTheme.TEXT_WHITE }, gb)
        inner.add(Box.createVerticalStrut(5), gb)
        inner.add(JLabel("Supports  .xlsx  ·  .xls  ·  .csv  ·  .pdf").apply {
            font = AppTheme.FONT_SMALL; foreground = AppTheme.TEXT_MUTED }, gb)
        inner.add(Box.createVerticalStrut(18), gb)
        inner.add(btn, gb)

        btn.addActionListener { openFileChooser() }
        zone.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) { openFileChooser() }
        })
        zone.transferHandler = object : javax.swing.TransferHandler() {
            override fun canImport(s: TransferSupport) =
                s.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.javaFileListFlavor)
            override fun importData(s: TransferSupport): Boolean {
                @Suppress("UNCHECKED_CAST")
                (s.transferable.getTransferData(
                    java.awt.datatransfer.DataFlavor.javaFileListFlavor) as List<File>)
                    .firstOrNull()?.let { loadFile(it) }
                return true
            }
        }
        zone.add(inner, BorderLayout.CENTER)
        return zone
    }

    private fun buildFormatCard(): JPanel {
        val card = AppTheme.card()
        val wrap = JPanel(BorderLayout(0, 10)).apply { isOpaque = false }
        wrap.add(JLabel("📋  Expected Format").apply {
            font = AppTheme.FONT_HEADING; foreground = AppTheme.ACCENT_LIGHT
        }, BorderLayout.NORTH)
        val rows = JPanel().apply {
            isOpaque = false; layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = EmptyBorder(4, 0, 0, 0)
            listOf("• Row 1 (header):  Name, Score1, Score2, …",
                "• Column 1:  Student name",
                "• Remaining:  Numeric scores (0–100)",
                "• PDF: each line →  Name, Score1, Score2").forEach { txt ->
                add(JLabel(txt).apply { font = AppTheme.FONT_MONO; foreground = AppTheme.TEXT_MUTED })
                add(Box.createVerticalStrut(3))
            }
        }
        wrap.add(rows, BorderLayout.CENTER)
        (card as JPanel).removeAll(); card.border = EmptyBorder(18, 22, 18, 22); card.add(wrap)
        return card
    }

    private fun buildGradeScaleCard(): JPanel {
        val card  = AppTheme.card()
        val panel = JPanel(GridBagLayout()).apply { isOpaque = false }
        val gbc   = GridBagConstraints().apply { insets = Insets(3, 10, 3, 10) }
        gbc.gridwidth = GridBagConstraints.REMAINDER
        panel.add(JLabel("🎓  Grade Scale").apply {
            font = AppTheme.FONT_HEADING; foreground = AppTheme.ACCENT_LIGHT }, gbc)
        panel.add(Box.createVerticalStrut(6), gbc)
        gbc.gridwidth = 1
        listOf("A+" to "≥ 90", "A" to "80–89", "B+" to "70–79",
            "B" to "60–69", "C" to "50–59", "D" to "40–49", "F" to "< 40").forEach { (g, r) ->
            val c = AppTheme.gradeColor(g)
            val badge = object : JLabel("  $g  ") {
                override fun paintComponent(gr: Graphics) {
                    val g2 = gr as Graphics2D
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                    g2.color = Color(c.red, c.green, c.blue, 30)
                    g2.fillRoundRect(0, 0, width, height, 6, 6)
                    super.paintComponent(gr)
                }
            }.apply { font = AppTheme.FONT_BADGE; foreground = c; isOpaque = false; border = EmptyBorder(3, 8, 3, 8) }
            panel.add(badge, gbc)
            panel.add(JLabel(r).apply { font = AppTheme.FONT_BODY; foreground = AppTheme.TEXT_MUTED }, gbc)
        }
        (card as JPanel).removeAll(); card.border = EmptyBorder(18, 22, 18, 22); card.add(panel)
        return card
    }

    private fun openFileChooser() {
        val fc = JFileChooser().apply {
            dialogTitle = "Select Student Data File"; isMultiSelectionEnabled = false
            addChoosableFileFilter(FileNameExtensionFilter(
                "All Supported (xlsx, xls, csv, pdf)", "xlsx", "xls", "csv", "pdf"))
            addChoosableFileFilter(FileNameExtensionFilter("Excel Files", "xlsx", "xls"))
            addChoosableFileFilter(FileNameExtensionFilter("PDF Files", "pdf"))
            addChoosableFileFilter(FileNameExtensionFilter("CSV Files", "csv"))
            fileFilter = choosableFileFilters[1]
        }
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) loadFile(fc.selectedFile)
    }

    private fun loadFile(file: File) {
        setStatus("Loading ${file.name}…", AppTheme.TEXT_MUTED)
        // ✅ fixed: removed dead SwingWorker line
        Thread {
            val result = FileImporter.import(file)
            SwingUtilities.invokeLater {
                result.fold(
                    onSuccess = { s ->
                        setStatus("✔  Loaded ${s.size} students from \"${file.name}\"", AppTheme.GREEN)
                        onStudentsLoaded(s)
                    },
                    onFailure = { err ->
                        setStatus("✘  ${err.message}", AppTheme.RED)
                        JOptionPane.showMessageDialog(this, err.message, "Import Error", JOptionPane.ERROR_MESSAGE)
                    }
                )
            }
        }.also { it.isDaemon = true }.start()
    }

    private fun setStatus(msg: String, color: Color) {
        statusLabel.text = msg; statusLabel.foreground = color
    }
}