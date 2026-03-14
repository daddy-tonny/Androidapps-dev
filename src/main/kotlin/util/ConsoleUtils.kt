package util

/** ANSI colour codes for console output */
object Colours {
    const val RESET  = "\u001B[0m"
    const val BOLD   = "\u001B[1m"
    const val GREEN  = "\u001B[32m"
    const val RED    = "\u001B[31m"
    const val YELLOW = "\u001B[33m"
    const val CYAN   = "\u001B[36m"
    const val BLUE   = "\u001B[34m"
    const val WHITE  = "\u001B[37m"
    const val MAGENTA = "\u001B[35m"
}

/** Helpers for rendering tables in the console */
object ConsoleUtils {

    private const val COL_NAME   = 22
    private const val COL_SCORES = 32
    private const val COL_AVG   =  8
    private const val COL_LTR   =  4
    private const val COL_STS   =  6

    fun separator(char: String = "─", width: Int = 78): String = char.repeat(width)

    fun header(title: String) {
        val line = separator("═")
        println("${Colours.CYAN}${Colours.BOLD}")
        println(line)
        println("  $title")
        println(line)
        println(Colours.RESET)
    }

    fun subHeader(title: String) {
        println("${Colours.BLUE}${Colours.BOLD}── $title ${separator("─", 60 - title.length)}${Colours.RESET}")
    }

    fun success(msg: String) = println("${Colours.GREEN}✔  $msg${Colours.RESET}")
    fun error(msg: String)   = println("${Colours.RED}✘  $msg${Colours.RESET}")
    fun info(msg: String)    = println("${Colours.YELLOW}ℹ  $msg${Colours.RESET}")
    fun prompt(msg: String)  = print("${Colours.WHITE}${Colours.BOLD}▶  $msg${Colours.RESET}")

    /** Colour-codes a letter grade */
    fun colourGrade(letter: String): String = when (letter) {
        "A", "A+" -> "${Colours.GREEN}$letter${Colours.RESET}"
        "B+", "B" -> "${Colours.CYAN}$letter${Colours.RESET}"
        "C"        -> "${Colours.YELLOW}$letter${Colours.RESET}"
        "D"        -> "${Colours.MAGENTA}$letter${Colours.RESET}"
        else       -> "${Colours.RED}$letter${Colours.RESET}"
    }

    /** Colour-codes pass/fail */
    fun colourStatus(status: String): String = when (status) {
        "PASS" -> "${Colours.GREEN}PASS${Colours.RESET}"
        else   -> "${Colours.RED}FAIL${Colours.RESET}"
    }

    /**
     * Prints a table row using a lambda to format each cell.
     * Demonstrates lambda-as-parameter usage.
     */
    fun tableRow(cells: List<String>, widths: List<Int>, colour: String = "") {
        val row = cells.zip(widths)
                       .joinToString(" │ ") { (cell, w) -> cell.padEnd(w).take(w) }
        println("$colour│ $row │${Colours.RESET}")
    }

    fun tableHeader(labels: List<String>, widths: List<Int>) {
        val line = "├" + widths.joinToString("┼") { "─".repeat(it + 2) } + "┤"
        val top  = "┌" + widths.joinToString("┬") { "─".repeat(it + 2) } + "┐"
        println(top)
        tableRow(labels, widths, Colours.BOLD)
        println(line)
    }

    fun tableFooter(widths: List<Int>) {
        println("└" + widths.joinToString("┴") { "─".repeat(it + 2) } + "┘")
    }

    fun readLine(prompt: String): String {
        this.prompt(prompt)
        return readLine()?.trim() ?: ""
    }

    fun pause() {
        readLine("Press ENTER to continue... ")
    }
}
