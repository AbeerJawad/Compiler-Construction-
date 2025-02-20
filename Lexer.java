import java.io.IOException;
import java.util.*;

public class Lexer {
    private final DFA dfa;

    public Lexer(String dfaConfigFile) throws IOException {
        this.dfa = new DFA(dfaConfigFile);
    }

    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;

        while (i < input.length()) {
            char current = input.charAt(i);
            //whitespace
            if (Character.isWhitespace(current)) {
                i++;
                continue;
            }

            // Handle single-line comments "--"
            if (current == '-' && i + 1 < input.length() && input.charAt(i + 1) == '-') {
                while (i < input.length() && input.charAt(i) != '\n') {
                    i++;
                }
                continue;
            }

            // Handle multi-line comments "== ... =="
            if (current == '=' && i + 1 < input.length() && input.charAt(i + 1) == '=') {
                i += 2;
                while (i + 1 < input.length() && !(input.charAt(i) == '=' && input.charAt(i + 1) == '=')) {
                    i++;
                }
                i += 2;
                continue;
            }


            // Handle string
            if (current == '"') {
                StringBuilder stringLiteral = new StringBuilder();
                stringLiteral.append(current);
                i++;

                while (i < input.length() && input.charAt(i) != '"') {
                    stringLiteral.append(input.charAt(i));
                    i++;
                }

                if (i < input.length() && input.charAt(i) == '"') {
                    stringLiteral.append('"');
                    i++;
                    tokens.add(new Token(TokenType.STRING, stringLiteral.toString()));
                } else {
                    tokens.add(new Token(TokenType.UNKNOWN, stringLiteral.toString()));
                }
                continue;
            }

            // Handle numbers int and dec
            if (Character.isDigit(current)) {
                StringBuilder number = new StringBuilder();
                while (i < input.length() && Character.isDigit(input.charAt(i))) {
                    number.append(input.charAt(i));
                    i++;
                }

                // for dec
                if (i < input.length() && input.charAt(i) == '.' && i + 1 < input.length() && Character.isDigit(input.charAt(i + 1))) {
                    number.append('.');
                    i++;
                    while (i < input.length() && Character.isDigit(input.charAt(i))) {
                        number.append(input.charAt(i));
                        i++;
                    }
                }

                tokens.add(new Token(dfa.getTokenType(number.toString()), number.toString()));
                continue;
            }

            // Handle identifiers, keywords, and boolean literals using DFA
            if (Character.isLetter(current) || current == '_') {
                StringBuilder identifier = new StringBuilder();
                while (i < input.length() && (Character.isLetterOrDigit(input.charAt(i)) || input.charAt(i) == '_')) {
                    identifier.append(input.charAt(i));
                    i++;
                }

                String lexeme = identifier.toString();
                tokens.add(new Token(dfa.getTokenType(lexeme), lexeme));
                continue;
            }

            // Handle punctuation and separators
            if (";(){}".indexOf(current) != -1) {
                tokens.add(new Token(TokenType.SEPARATOR, Character.toString(current)));
                i++;
                continue;
            }

            // Handle operators
            TokenType opType = dfa.getTokenType(Character.toString(current));
            if (opType == null) opType = TokenType.UNKNOWN;
            tokens.add(new Token(opType, Character.toString(current)));
            i++;
        }

        return tokens;
    }


    public static class Token {
        TokenType type;
        String lexeme;

        public Token(TokenType type, String lexeme) {
            this.type = type;
            this.lexeme = lexeme;
        }

        @Override
        public String toString() {
            return type + ": " + lexeme;
        }
    }
}
