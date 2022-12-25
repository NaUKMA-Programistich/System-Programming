import java.io.File

private const val identifier = "^[dzhos]+\$"
private const val number = "\\d+(.\\d+)?"
private val operators = listOf(
    "(", ")", "+", "-", "*", "/", "+=", "*=", "-=", "/=", "++", "--", "==", "!=", "=",
    "{", "}", ">", "<", "[", "]", "<=", ">=", "&&", "||", "!", "!!", "===", "!==", "?.",
    "?:", "::", "..", ":", "?", "->", "@", ";", "$", "_", ","
)
private val functions = listOf("cos", "sin", "tag", "main", "print", "println")
private val reserved = listOf(
    "fun", "as", "as?", "break", "class", "continue", "do", "else", "false", "for", "in", "if", "in", "!in",
    "interface", "is", "!is", "null", "object", "package", "return", "super", "this", "throw", "true", "try",
    "typealias", "typeof", "val", "var", "when", "while", "by", "catch", "constructor", "delegate", "dynamic",
    "field", "file", "finally", "get", "import", "init", "param", "property", "receiver", "set", "setparam",
    "value", "where", "abstract", "it", "actual", "annotation", "companion", "const", "crossinline", "data",
    "enum", "expect", "external", "final", "infix", "inline", "inner", "internal", "lateinit", "noinline",
    "open", "operator", "out", "override", "private", "protected", "public", "reified", "sealed", "suspend",
    "tailrec", "vararg", "field", "String"
)

fun main() {
    val file = File("test.txt")
    if (!file.exists()) return

    val text = file.readText()
    val parsedText = parseText(text)
    println(parsedText)

    parsedText.forEach { lexeme ->
        if (reserved.contains(lexeme)) {
            println("Reserved: $lexeme")
        } else if (functions.contains(lexeme)) {
            println("Functions: $lexeme")
        } else if (operators.contains(lexeme)) {
            println("Operators: $lexeme")
        } else if (identifier.toRegex() matches lexeme.lowercase()) {
            println("Identifier: $lexeme")
        } else if (number.toRegex() matches lexeme) {
            if (lexeme.contains(".")) println("Number with dots: $lexeme")
            else println("Number: $lexeme")
        } else if (lexeme.startsWith("\"") && lexeme.endsWith("\"")) {
            println("String: $lexeme")
        } else println("Wrong: $lexeme")
    }
}

fun parseText(text: String): List<String> {
    return text
        .split("[\\n\\r\\s]".toRegex())
        .map { parseBracket(it) }
        .flatten()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
}

fun parseBracket(text: String): List<String> {
    val result = mutableListOf<String>()
    var word = ""
    for (item in text) {
        val singleOperators = operators.filter { it.length == 1 }
        if (singleOperators.contains(item.toString())) {
            result.add(word)
            result.add(item.toString())
            word = ""
        } else word += item
    }
    if (word.isNotEmpty()) result.add(word)
    return result.map { it.trim() }
}
