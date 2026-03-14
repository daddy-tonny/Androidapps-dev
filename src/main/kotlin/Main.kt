import ui.AppTheme
import ui.HomePanel
import ui.PreviewPanel
import ui.ResultsPanel
import model.Student
import javax.swing.*
import java.awt.*
import javax.swing.border.EmptyBorder
import javax.swing.border.MatteBorder

fun main() {
    System.setProperty("awt.useSystemAAFontSettings", "on")
    System.setProperty("swing.aatext", "true")
    SwingUtilities.invokeLater { MainWindow().isVisible = true }
}

class MainWindow : JFrame("Grade Calculator") {

    private val cardLayout = CardLayout()
    private val cardPanel  = JPanel(cardLayout)
    private val stepLabels = listOf(
        makeStepLabel("1", "Import"),
        makeStepLabel("2", "Preview"),
        makeStepLabel("3", "Results")
    )
    private lateinit var previewPanel: PreviewPanel
    private lateinit var resultsPanel: ResultsPanel

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        minimumSize   = Dimension(980, 700)
        preferredSize = Dimension(1140, 780)
        iconImage     = AppTheme.createAppIcon()
        contentPane.background = AppTheme.BG_DARK
        (contentPane as JPanel).layout = BorderLayout()

        add(buildNav(), BorderLayout.NORTH)

        val homePanel = HomePanel { students -> goToPreview(students) }
        previewPanel  = PreviewPanel(onBack = { showCard("home") }, onCompute = { s -> goToResults(s) })
        resultsPanel  = ResultsPanel(onBack = { showCard("preview") }, onRestart = { showCard("home") })

        cardPanel.background = AppTheme.BG_DARK
        cardPanel.add(homePanel,    "home")
        cardPanel.add(previewPanel, "preview")
        cardPanel.add(resultsPanel, "results")
        add(cardPanel, BorderLayout.CENTER)
        add(buildStatusBar(), BorderLayout.SOUTH)

        pack()
        setLocationRelativeTo(null)
        showCard("home")
    }

    private fun buildNav(): JPanel {
        val nav = object : JPanel(BorderLayout()) {
            override fun paintComponent(g: Graphics) {
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                g2.paint = GradientPaint(0f, 0f, AppTheme.BG_NAV, width.toFloat(), 0f, Color(0x07, 0x14, 0x2C))
                g2.fillRect(0, 0, width, height)
            }
        }.apply {
            isOpaque = false
            border   = MatteBorder(0, 0, 1, 0, AppTheme.BORDER_MID)
            preferredSize = Dimension(0, 62)
        }

        // Logo
        val logo = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)).apply {
            isOpaque = false; border = EmptyBorder(0, 24, 0, 0)
            val icon = object : JLabel() {
                override fun paintComponent(g: Graphics) {
                    val g2 = g as Graphics2D
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                    g2.paint = GradientPaint(0f, 0f, AppTheme.COBALT, 30f, 30f, AppTheme.CYAN)
                    g2.fillRoundRect(0, 0, 30, 30, 8, 8)
                    g2.color = Color.WHITE
                    g2.font  = Font("Segoe UI", Font.BOLD, 15)
                    g2.drawString("G", 9, 22)
                }
            }.apply { preferredSize = Dimension(30, 30) }
            add(icon)
            add(JLabel("  GradeCalc").apply {
                font = Font("Segoe UI", Font.BOLD, 17); foreground = AppTheme.TEXT_WHITE
            })
        }

        // Step pills
        val steps = JPanel(FlowLayout(FlowLayout.CENTER, 0, 0)).apply { isOpaque = false }
        stepLabels.forEachIndexed { i, lbl ->
            steps.add(lbl)
            if (i < stepLabels.size - 1)
                steps.add(JLabel("   →   ").apply {
                    font = Font("Segoe UI", Font.PLAIN, 12); foreground = AppTheme.TEXT_DIM
                })
        }

        nav.add(logo,  BorderLayout.WEST)
        nav.add(steps, BorderLayout.CENTER)
        return nav
    }

    private fun makeStepLabel(num: String, name: String): StepLabel = StepLabel(num, name)

    private fun buildStatusBar(): JPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 16, 4)).apply {
        background = AppTheme.BG_NAV
        border     = MatteBorder(1, 0, 0, 0, AppTheme.BORDER)
        preferredSize = Dimension(0, 26)
        add(JLabel("Grade Calculator  v1.0  ·  Kotlin + Swing").apply {
            font = AppTheme.FONT_TINY; foreground = AppTheme.TEXT_DIM
        })
    }

    private fun goToPreview(students: List<Student>) { previewPanel.loadStudents(students); showCard("preview") }
    private fun goToResults(students: List<Student>) { resultsPanel.loadResults(students);  showCard("results") }

    private fun showCard(name: String) {
        cardLayout.show(cardPanel, name)
        val idx = when (name) { "home" -> 0; "preview" -> 1; else -> 2 }
        stepLabels.forEachIndexed { i, lbl -> lbl.setActive(i == idx) }
    }
}

class StepLabel(num: String, name: String) : JLabel(" $num  $name ") {
    private var active = false
    init {
        font       = Font("Segoe UI", Font.PLAIN, 13)
        foreground = AppTheme.TEXT_MUTED
        border     = EmptyBorder(5, 14, 5, 14)
    }
    fun setActive(a: Boolean) { active = a; foreground = if (a) AppTheme.CYAN else AppTheme.TEXT_MUTED; repaint() }
    override fun paintComponent(g: Graphics) {
        if (active) {
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.color = Color(0, 212, 255, 25)
            g2.fillRoundRect(0, 0, width, height, 8, 8)
        }
        super.paintComponent(g)
    }
}
