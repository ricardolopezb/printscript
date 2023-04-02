package interpreter

import common.ast.ASTFactory
import common.ast.ASTType
import common.exceptions.InvalidTokenInputException
import common.token.Token
import common.token.TokenType
import interpreter.implementation.Interpreter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class InterpreterTest {

    private val interpreter: Interpreter = Interpreter()

    @Test
    fun testDeclarationAST() {
        val inputTokens = listOf(
            Token(0, TokenType.LET, "let", 0),
            Token(1, TokenType.IDENTIFIER, "myVar", 0),
            Token(2, TokenType.COLON, ":", 0),
            Token(3, TokenType.TYPE, "number", 0),
            Token(4, TokenType.SEMICOLON, ";", 0)
        )

        val ast = ASTFactory.createAST(ASTType.DECLARATION, inputTokens)
        interpreter.interpret(ast)
        assertTrue(interpreter.getSymbolTable().size == 1)
        assertEquals(
            Pair("number", ""),
            interpreter.getSymbolTable().get("myVar")
        )
    }

    @Test
    fun testAssignationWithoutDeclaredVariableAST() {
        val inputTokens = listOf(
            Token(1, TokenType.IDENTIFIER, "myVar", 0),
            Token(2, TokenType.ASSIGNATION, "=", 0),
            Token(3, TokenType.NUMERIC_LITERAL, "3", 0),
            Token(4, TokenType.SEMICOLON, ";", 0)
        )

        val ast = ASTFactory.createAST(ASTType.ASSIGNATION, inputTokens)

        assertThrows<Exception> {
            interpreter.interpret(ast)
        }
    }

    @Test
    fun testAssignationAST() {
        val inputTokensFirstLine = listOf(
            Token(0, TokenType.LET, "let", 0),
            Token(1, TokenType.IDENTIFIER, "myVar", 0),
            Token(2, TokenType.COLON, ":", 0),
            Token(3, TokenType.TYPE, "number", 0),
            Token(4, TokenType.SEMICOLON, ";", 0)
        )

        val inputTokensSecondLine = listOf(
            Token(1, TokenType.IDENTIFIER, "myVar", 1),
            Token(2, TokenType.ASSIGNATION, "=", 1),
            Token(3, TokenType.NUMERIC_LITERAL, "3", 1),
            Token(3, TokenType.OPERATOR, "+", 1),
            Token(3, TokenType.NUMERIC_LITERAL, "3", 1),
            Token(4, TokenType.SEMICOLON, ";", 1)
        )

        val astFirstLine = ASTFactory.createAST(ASTType.DECLARATION, inputTokensFirstLine)
        val astSecondLine = ASTFactory.createAST(ASTType.ASSIGNATION, inputTokensSecondLine)

        interpreter.interpret(astFirstLine)
        interpreter.interpret(astSecondLine)

        assertTrue(interpreter.getSymbolTable().size == 1)
        assertEquals(
            Pair("number", "6"),
            interpreter.getSymbolTable().get("myVar")
        )
    }

    @Test
    fun testMultipleDeclarations() {
        val inputTokens1 = listOf(
            Token(0, TokenType.LET, "let", 0),
            Token(1, TokenType.IDENTIFIER, "myVar", 0),
            Token(2, TokenType.COLON, ":", 0),
            Token(3, TokenType.TYPE, "number", 0),
            Token(4, TokenType.SEMICOLON, ";", 0)
        )

        val ast1 = ASTFactory.createAST(ASTType.DECLARATION, inputTokens1)

        val inputTokens2 = listOf(
            Token(0, TokenType.LET, "let", 0),
            Token(1, TokenType.IDENTIFIER, "myOtherVar", 0),
            Token(2, TokenType.COLON, ":", 0),
            Token(3, TokenType.TYPE, "string", 0),
            Token(4, TokenType.SEMICOLON, ";", 0)
        )

        val ast2 = ASTFactory.createAST(ASTType.DECLARATION, inputTokens2)
        interpreter.interpret(ast1)
        interpreter.interpret(ast2)
        assertTrue(interpreter.getSymbolTable().size == 2)
        assertEquals(
            Pair("number", ""),
            interpreter.getSymbolTable().get("myVar")
        )
        assertEquals(
            Pair("string", ""),
            interpreter.getSymbolTable().get("myOtherVar")
        )
    }

    @Test
    fun testFunctionAST() {
        val inputTokensFirstLine = listOf(
            Token(0, TokenType.LET, "let", 0),
            Token(1, TokenType.IDENTIFIER, "myVar", 0),
            Token(2, TokenType.COLON, ":", 0),
            Token(3, TokenType.TYPE, "number", 0),
            Token(4, TokenType.SEMICOLON, ";", 0)
        )

        val inputTokensSecondLine = listOf(
            Token(1, TokenType.IDENTIFIER, "myVar", 1),
            Token(2, TokenType.ASSIGNATION, "=", 1),
            Token(3, TokenType.NUMERIC_LITERAL, "3", 1),
            Token(3, TokenType.OPERATOR, "+", 1),
            Token(3, TokenType.NUMERIC_LITERAL, "3", 1),
            Token(4, TokenType.SEMICOLON, ";", 1)
        )
        val inputTokensThirdLine = listOf(
            Token(0, TokenType.PRINTLN, "println", 2),
            Token(1, TokenType.OPEN_PARENTHESIS, "(", 2),
            Token(2, TokenType.IDENTIFIER, "myVar", 2),
            Token(3, TokenType.CLOSE_PARENTHESIS, ")", 2),
            Token(4, TokenType.SEMICOLON, ";", 2)
        )

        val astFirstLine = ASTFactory.createAST(ASTType.DECLARATION, inputTokensFirstLine)
        val astSecondLine = ASTFactory.createAST(ASTType.ASSIGNATION, inputTokensSecondLine)
        val astThirdLine = ASTFactory.createAST(ASTType.FUNCTION, inputTokensThirdLine)

        interpreter.interpret(astFirstLine)
        interpreter.interpret(astSecondLine)
        interpreter.interpret(astThirdLine)

    }

    companion object {

        @JvmStatic
        fun differentFunctionArgumentsList() = listOf(
            Arguments.of(
                listOf(
                    Token(0, TokenType.PRINTLN, "println", 0),
                    Token(1, TokenType.OPEN_PARENTHESIS, "(", 0),
                    Token(2, TokenType.NUMERIC_LITERAL, "4", 0),
                    Token(3, TokenType.CLOSE_PARENTHESIS, ")", 0),
                    Token(4, TokenType.SEMICOLON, ";", 0)
                )
            ),
            Arguments.of(
                listOf(
                    Token(0, TokenType.PRINTLN, "println", 0),
                    Token(1, TokenType.OPEN_PARENTHESIS, "(", 0),
                    Token(2, TokenType.STRING_LITERAL, "HELLO", 0),
                    Token(3, TokenType.CLOSE_PARENTHESIS, ")", 0),
                    Token(4, TokenType.SEMICOLON, ";", 0)
                )
            ),
            Arguments.of(
                listOf(
                    Token(0, TokenType.PRINTLN, "println", 0),
                    Token(1, TokenType.OPEN_PARENTHESIS, "(", 0),
                    Token(2, TokenType.STRING_LITERAL, "PRINTING", 0),
                    Token(3, TokenType.CLOSE_PARENTHESIS, ")", 0),
                    Token(4, TokenType.SEMICOLON, ";", 0)
                )
            )

        )
    }

    @ParameterizedTest
    @MethodSource("differentFunctionArgumentsList")
    fun testFunctionASTPrint(inputTokens: List<Token>) {
        val ast = ASTFactory.createAST(ASTType.FUNCTION, inputTokens)
        interpreter.interpret(ast)
    }


}
