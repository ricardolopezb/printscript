/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package printscript.v1.app

import common.token.Token
import common.token.TokenType
import interpreter.implementation.Interpreter
import lexer.factory.TokenTypeManagerFactory
import lexer.implementation.Lexer
import parser.implementation.Parser
import java.io.File
import java.util.Scanner


fun main(args: Array<String>) {

    val filename = File("print.txt")
    if(!filename.exists()) throw Error("File does not exist.")

    //print(getTokenFromStringRepresentation("Token(order_id=10, tokenType=IDENTIFIER, value=num, row=0)"))
    try{
        executionFunction(filename)
    } catch(exception: Exception){
        printInRed(exception)
    }



}

private fun printInRed(exception: Exception) = println("\u001B[31m${exception.message}\u001B[0m")

fun executionFunction(file: File) {

    runLexer(file, ExecuteFunction())


}

private fun runLexer(file: File, function: PrintscriptFunction) {
    val lexer =
        Lexer(TokenTypeManagerFactory.createPrintScriptTokenTypeManager(), listOf(';', ':', '(', ')', ' ', '\n', '\t','+','=','-','*','/'))
    lexer.extractTokensFromFile(file)

    val listOfTokensInLine = mutableListOf<Token>()
    val scanner = Scanner(File("Tokens.txt"))
    var semicolonFound = false
    var currentLine = 1
    while (scanner.hasNextLine()) {
        val token = getTokenFromStringRepresentation(scanner.nextLine())
        currentLine = token.row
        listOfTokensInLine.add(token)

        if (token.tokenType == TokenType.SEMICOLON) {
            function.execute(listOfTokensInLine)
            listOfTokensInLine.clear()
            semicolonFound = true
        }
        if(!scanner.hasNextLine() && token.tokenType != TokenType.SEMICOLON)
            throw java.lang.Exception("There is a semicolon missing in the last line of the file")
    }

}

fun getTokenFromStringRepresentation(input: String): Token {
    val parts = input.substringAfter("(").dropLast(1).split(", ")

    val order_id = parts[0].substringAfter("=").toInt()
    val tokenType = TokenType.valueOf(parts[1].substringAfter("="))
    val value = parts[2].substringAfter("=")
    val row = parts[3].substringAfter("=").toInt()

    return Token(order_id, tokenType, value, row)
}

interface PrintscriptFunction {
    fun execute(tokenLine: List<Token>)
}

class ExecuteFunction: PrintscriptFunction {
    val parser = Parser()
    val interpreter = Interpreter()
    override fun execute(tokenLine: List<Token>) = interpreter.interpret(parser.parse(tokenLine))

}

class FormatFunction: PrintscriptFunction {
    override fun execute(tokenLine: List<Token>) {
        TODO("Not yet implemented")
    }
}

class LinterFunction: PrintscriptFunction {
    override fun execute(tokenLine: List<Token>) {
        TODO("Not yet implemented")
    }

}
