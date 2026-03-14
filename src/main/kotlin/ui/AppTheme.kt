package ui

import java.awt.*
import java.awt.image.BufferedImage
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.plaf.basic.BasicScrollBarUI

object AppTheme {

    // ── Deep Blue Palette ─────────────────────────────────────────────────────
    val BG_DARK      = Color(0x05, 0x0D, 0x1A)
    val BG_NAV       = Color(0x07, 0x11, 0x22)
    val BG_CARD      = Color(0x0A, 0x16, 0x2B)
    val BG_SURFACE   = Color(0x0D, 0x1C, 0x38)
    val BG_HOVER     = Color(0x10, 0x22, 0x42)

    val CYAN         = Color(0x00, 0xD4, 0xFF)
    val CYAN_DIM     = Color(0x00, 0x96, 0xBB)
    val COBALT       = Color(0x1E, 0x72, 0xFF)
    val COBALT_DIM   = Color(0x13, 0x4F, 0xC0)
    val ACCENT       = CYAN
    val ACCENT_LIGHT = Color(0x7A, 0xE8, 0xFF)

    val BORDER       = Color(0x12, 0x28, 0x48)
    val BORDER_MID   = Color(0x1A, 0x38, 0x60)
    val BORDER_BRIGHT= Color(0x1E, 0x50, 0x88)
    val BORDER_GLOW  = Color(0x00, 0x6E, 0x9A)

    val TEXT_WHITE   = Color(0xEC, 0xF5, 0xFF)
    val TEXT_PRIMARY = Color(0xC2, 0xDC, 0xF8)
    val TEXT_MUTED   = Color(0x52, 0x74, 0xA4)
    val TEXT_DIM     = Color(0x28, 0x42, 0x68)

    val GREEN        = Color(0x00, 0xE5, 0x90)
    val RED          = Color(0xFF, 0x4E, 0x72)
    val AMBER        = Color(0xFF, 0xB8, 0x00)
    val PASS_COLOR   = GREEN
    val FAIL_COLOR   = RED
    val WARN_COLOR   = AMBER

    val GRADE_APLUS  = Color(0x00, 0xE5, 0x90)
    val GRADE_A      = Color(0x00, 0xCC, 0x80)
    val GRADE_BPLUS  = Color(0x00, 0xD4, 0xFF)
    val GRADE_B      = Color(0x1E, 0x72, 0xFF)
    val GRADE_C      = Color(0xFF, 0xB8, 0x00)
    val GRADE_D      = Color(0xFF, 0x7C, 0x2A)
    val GRADE_F      = Color(0xFF, 0x4E, 0x72)

    // ── Typography ────────────────────────────────────────────────────────────
    val FONT_TITLE   = Font("Segoe UI", Font.BOLD, 26)
    val FONT_HEADING = Font("Segoe UI Semibold", Font.BOLD, 14)
    val FONT_BODY    = Font("Segoe UI", Font.PLAIN, 13)
    val FONT_SMALL   = Font("Segoe UI", Font.PLAIN, 12)
    val FONT_TINY    = Font("Segoe UI", Font.PLAIN, 11)
    val FONT_MONO    = Font("Consolas", Font.PLAIN, 12)
    val FONT_STAT    = Font("Segoe UI", Font.BOLD, 32)
    val FONT_BADGE   = Font("Segoe UI", Font.BOLD, 11)

    fun gradeColor(g: String): Color = when (g) {
        "A+" -> GRADE_APLUS; "A"  -> GRADE_A
        "B+" -> GRADE_BPLUS; "B"  -> GRADE_B
        "C"  -> GRADE_C;     "D"  -> GRADE_D
        else -> GRADE_F
    }

    // ── Buttons ───────────────────────────────────────────────────────────────
    fun primaryButton(text: String): JButton = object : JButton(text) {
        private var hover = false
        init {
            font = Font("Segoe UI", Font.BOLD, 13); foreground = Color(0x03, 0x08, 0x12)
            isFocusPainted = false; isBorderPainted = false; isContentAreaFilled = false
            cursor = Cursor(Cursor.HAND_CURSOR); border = EmptyBorder(0, 22, 0, 22)
            preferredSize = Dimension(preferredSize.width.coerceAtLeast(150), 40)
            addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mouseEntered(e: java.awt.event.MouseEvent) { hover = true; repaint() }
                override fun mouseExited(e: java.awt.event.MouseEvent)  { hover = false; repaint() }
            })
        }
        override fun paintComponent(g: Graphics) {
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            if (hover) { g2.color = Color(0, 212, 255, 30); g2.fillRoundRect(-4, -4, width+8, height+8, 16, 16) }
            g2.paint = GradientPaint(0f, 0f, if (hover) CYAN else COBALT,
                                     width.toFloat(), height.toFloat(), if (hover) COBALT else CYAN_DIM)
            g2.fillRoundRect(0, 0, width, height, 10, 10)
            super.paintComponent(g)
        }
    }

    fun secondaryButton(text: String): JButton = object : JButton(text) {
        private var hover = false
        init {
            font = Font("Segoe UI", Font.PLAIN, 13); foreground = TEXT_PRIMARY
            isFocusPainted = false; isBorderPainted = false; isContentAreaFilled = false
            cursor = Cursor(Cursor.HAND_CURSOR); border = EmptyBorder(0, 16, 0, 16)
            preferredSize = Dimension(preferredSize.width.coerceAtLeast(110), 38)
            addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mouseEntered(e: java.awt.event.MouseEvent) { hover = true; repaint() }
                override fun mouseExited(e: java.awt.event.MouseEvent)  { hover = false; repaint() }
            })
        }
        override fun paintComponent(g: Graphics) {
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.color = if (hover) BG_HOVER else BG_CARD; g2.fillRoundRect(0, 0, width, height, 10, 10)
            g2.color = if (hover) BORDER_BRIGHT else BORDER_MID; g2.drawRoundRect(0, 0, width-1, height-1, 10, 10)
            super.paintComponent(g)
        }
    }

    fun dangerButton(text: String): JButton = object : JButton(text) {
        private var hover = false
        init {
            font = Font("Segoe UI", Font.PLAIN, 13); foreground = RED
            isFocusPainted = false; isBorderPainted = false; isContentAreaFilled = false
            cursor = Cursor(Cursor.HAND_CURSOR); border = EmptyBorder(0, 16, 0, 16)
            preferredSize = Dimension(preferredSize.width.coerceAtLeast(110), 38)
            addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mouseEntered(e: java.awt.event.MouseEvent) { hover = true; repaint() }
                override fun mouseExited(e: java.awt.event.MouseEvent)  { hover = false; repaint() }
            })
        }
        override fun paintComponent(g: Graphics) {
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.color = if (hover) Color(255, 78, 114, 28) else BG_CARD; g2.fillRoundRect(0, 0, width, height, 10, 10)
            g2.color = if (hover) RED else BORDER_MID; g2.drawRoundRect(0, 0, width-1, height-1, 10, 10)
            super.paintComponent(g)
        }
    }

    // ── Cards ─────────────────────────────────────────────────────────────────
    fun card(): JPanel = object : JPanel(BorderLayout()) {
        override fun paintComponent(g: Graphics) {
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.paint = GradientPaint(0f, 0f, BG_CARD, 0f, height.toFloat(), BG_SURFACE)
            g2.fillRoundRect(0, 0, width, height, 14, 14)
            g2.color = BORDER_MID; g2.drawRoundRect(0, 0, width-1, height-1, 14, 14)
        }
    }.apply { isOpaque = false; border = EmptyBorder(18, 22, 18, 22) }

    fun statCard(value: String, label: String, color: Color): JPanel =
        object : JPanel(GridBagLayout()) {
            override fun paintComponent(g: Graphics) {
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                g2.color = Color(color.red, color.green, color.blue, 22)
                g2.fillRoundRect(-3, -3, width+6, height+6, 18, 18)
                g2.paint = GradientPaint(0f, 0f, BG_CARD, 0f, height.toFloat(), BG_SURFACE)
                g2.fillRoundRect(0, 0, width, height, 14, 14)
                g2.paint = GradientPaint(0f, 0f, color, width.toFloat(), 0f, color.darker())
                g2.fillRoundRect(0, 0, width, 4, 4, 4); g2.fillRect(0, 2, width, 2)
                g2.color = Color(color.red, color.green, color.blue, 55)
                g2.drawRoundRect(0, 0, width-1, height-1, 14, 14)
            }
        }.apply {
            isOpaque = false; border = EmptyBorder(22, 18, 18, 18)
            val gbc = GridBagConstraints().apply { gridwidth = GridBagConstraints.REMAINDER }
            add(JLabel(value).apply { font = FONT_STAT; foreground = color }, gbc)
            add(JLabel(label).apply { font = FONT_TINY; foreground = TEXT_MUTED }, gbc)
        }

    fun styledScroll(view: Component): JScrollPane = JScrollPane(view).apply {
        isOpaque = false; border = null; viewport.isOpaque = false
        verticalScrollBar.apply   { isOpaque = false; setUI(BlueScrollUI()); unitIncrement = 16 }
        horizontalScrollBar.apply { isOpaque = false; setUI(BlueScrollUI()) }
    }

    fun createAppIcon(): Image {
        val img = BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
        val g2  = img.createGraphics()
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.paint = GradientPaint(0f, 0f, COBALT, 32f, 32f, CYAN)
        g2.fillRoundRect(0, 0, 32, 32, 8, 8)
        g2.color = Color.WHITE; g2.font = Font("Segoe UI", Font.BOLD, 17)
        g2.drawString("G", 9, 23); g2.dispose(); return img
    }

    class BlueScrollUI : BasicScrollBarUI() {
        override fun configureScrollBarColors() { thumbColor = Color(0x14, 0x38, 0x70); trackColor = BG_DARK }
        override fun createDecreaseButton(o: Int) = zeroBtn()
        override fun createIncreaseButton(o: Int) = zeroBtn()
        private fun zeroBtn() = JButton().apply {
            preferredSize = Dimension(0, 0); minimumSize = Dimension(0, 0); maximumSize = Dimension(0, 0)
        }
        override fun paintThumb(g: Graphics, c: JComponent, r: Rectangle) {
            (g as Graphics2D).apply {
                setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                color = thumbColor; fillRoundRect(r.x+2, r.y+2, r.width-4, r.height-4, 6, 6)
            }
        }
        override fun paintTrack(g: Graphics, c: JComponent, r: Rectangle) {
            g.color = trackColor; g.fillRect(r.x, r.y, r.width, r.height)
        }
    }
}
