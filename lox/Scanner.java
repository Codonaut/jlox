package lox;

import static lox.TokenType.BANG;
import static lox.TokenType.BANG_EQUAL;
import static lox.TokenType.COMMA;
import static lox.TokenType.DOT;
import static lox.TokenType.EOF;
import static lox.TokenType.EQUAL;
import static lox.TokenType.EQUAL_EQUAL;
import static lox.TokenType.GREATER;
import static lox.TokenType.GREATER_EQUAL;
import static lox.TokenType.LEFT_BRACE;
import static lox.TokenType.LEFT_PAREN;
import static lox.TokenType.LESS;
import static lox.TokenType.LESS_EQUAL;
import static lox.TokenType.MINUS;
import static lox.TokenType.PLUS;
import static lox.TokenType.RIGHT_BRACE;
import static lox.TokenType.RIGHT_PAREN;
import static lox.TokenType.SEMICOLON;
import static lox.TokenType.SLASH;
import static lox.TokenType.STAR;
import static lox.TokenType.STRING;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
   
    private int start = 0;
    private int current = 0;
    private int line = 1;
    
    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // we're at beginning of next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
            case '/':
            if (match('/')) {
                // A comment goes until the end of the line
                while (peek() != '\n' && !isAtEnd()) advance();
            } else {
                addToken(SLASH);
            }
            break;

            case ' ':
            case '\r':
            case '\t':
            // Ignore whitespace
            break;

            case '\n':
            line++;
            break;

            case '"': string(); break;

            default:
            Lox.error(line, "Unexpected character.");
            break;
        }
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if(peek() == '\n') line++;
            advance();
        }

        // Unterminated string
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // The closing "
        advance();

        // Trim the surrounding quotes
        String value = source.substring(start + 1, current -1);
        addToken(STRING, value);
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
}