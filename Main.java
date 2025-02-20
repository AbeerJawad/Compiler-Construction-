import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            DFA dfa = new DFA("C:\\Users\\abeer\\IdeaProjects\\compilerconstruction\\forDFA.txt");
            SymbolTable symbolTable = new SymbolTable();
            ErrorHandler errorHandler = new ErrorHandler();

            List<String> codeLines = Files.readAllLines(Paths.get("C:\\Users\\abeer\\IdeaProjects\\compilerconstruction\\letsgo.ha"));

            int lineNumber = 1;
            for (String line : codeLines) {
                line = removeComments(line).trim(); // Remove comments
                if (line.isEmpty()) {
                    lineNumber++;
                    continue;
                }

                String[] tokens = line.split("\\s+");
                for (int i = 0; i < tokens.length; i++) {
                    String token = tokens[i];
                    TokenType tokenType = dfa.getTokenType(token);

                    if (tokenType == TokenType.INVALID) {
                        errorHandler.addError(lineNumber, "Unrecognized token '" + token + "'");
                        continue;
                    }

                    System.out.println(token + " -> " + tokenType);

                    // variable assign
                    if (tokenType == TokenType.IDENTIFIER && i + 1 < tokens.length && tokens[i + 1].equals("=")) {
                        if (i + 2 < tokens.length) {
                            String value = tokens[i + 2];
                            TokenType valueType = dfa.getTokenType(value);

                            if (valueType == TokenType.INVALID) {
                                errorHandler.addError(lineNumber, "Invalid value '" + value + "' assigned to " + token);
                                continue;
                            }

                            boolean isConstant = (i > 0 && tokens[i - 1].equals("assign")); // "assign" keyword make constant
                            if (!symbolTable.add(token, valueType.toString(), value, isConstant)) {
                                errorHandler.addError(lineNumber, "Duplicate declaration of '" + token + "'.");
                            }
                        } else {
                            errorHandler.addError(lineNumber, "Missing value after '=' for variable '" + token + "'");
                        }
                    }

                    // error checking identifier use
                    if (tokenType == TokenType.IDENTIFIER && (i == 0 || !tokens[i - 1].equals("="))) {
                        if (symbolTable.lookup(token) == null) {
                            errorHandler.addError(lineNumber, "Undeclared variable '" + token + "' used.");
                        }
                    }
                }
                lineNumber++;
            }

            // symbol table
            System.out.println("\nFinal Symbol Table:");
            symbolTable.printTable();

            // errors
            System.out.println("\nError Report:");
            errorHandler.printErrors();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String removeComments(String line) {
        int singleCommentIndex = line.indexOf("--");
        if (singleCommentIndex != -1) {
            line = line.substring(0, singleCommentIndex).trim();
        }

        int startMultiComment = line.indexOf("==");
        int endMultiComment = line.lastIndexOf("==");
        if (startMultiComment != -1 && endMultiComment > startMultiComment) {
            line = (line.substring(0, startMultiComment) + line.substring(endMultiComment + 2)).trim();
        }

        return line;
    }
}
