import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DFA {
    private final DFAState startState;
    private final Map<DFAState, TokenType> acceptingStates = new HashMap<>();
    private final Set<String> keywords;
    private final Set<String> booleanLiterals;

    public DFA(String filePath) throws IOException {
        DFAConfig config = loadConfig(filePath);
        this.startState = buildDFA(config);
        this.keywords = new HashSet<>(config.keywords);
        this.booleanLiterals = new HashSet<>(config.booleanLiterals);
    }

    private DFAConfig loadConfig(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        DFAConfig config = new DFAConfig();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            if (line.startsWith("START_STATE:")) {
                config.startState = line.split(":")[1].trim();
            } else if (line.startsWith("STATES:")) {
                config.states = Arrays.asList(line.split(":")[1].trim().split(", "));
            } else if (line.startsWith("ACCEPTING_STATES:")) {
                continue;
            } else if (line.contains("->")) {
                String[] parts = line.split("->");
                String state = parts[0].trim();
                String value = parts[1].trim();

                if (value.contains(":")) {
                    String[] transitionParts = value.split(":");
                    String toState = transitionParts[0].trim();
                    String chars = transitionParts[1].trim();
                    config.transitions.add(new TransitionRule(state, toState, chars));
                } else {
                    config.acceptingStates.put(state, value);
                }
            } else if (line.startsWith("KEYWORDS:")) {
                config.keywords = Arrays.asList(line.split(":")[1].trim().split(", "));
            } else if (line.startsWith("BOOLEAN_LITERALS:")) {
                config.booleanLiterals = Arrays.asList(line.split(":")[1].trim().split(", "));
            }
        }
        return config;
    }

    private DFAState buildDFA(DFAConfig config) {
        Map<String, DFAState> states = new HashMap<>();

        // Create all states
        for (String stateName : config.states) {
            states.put(stateName, new DFAState(stateName, config.acceptingStates.containsKey(stateName)));
        }

        // Add transitions
        for (TransitionRule rule : config.transitions) {
            DFAState fromState = states.get(rule.from);
            DFAState toState = states.get(rule.to);
            for (char c : rule.characters.toCharArray()) {
                fromState.addTransition(c, toState);
            }
        }

        // Map states to types
        for (Map.Entry<String, String> entry : config.acceptingStates.entrySet()) {
            String stateName = entry.getKey();
            String tokenTypeStr = entry.getValue().trim();

            if (!states.containsKey(stateName)) continue;
            try {
                TokenType tokenType = TokenType.valueOf(tokenTypeStr.toUpperCase());
                acceptingStates.put(states.get(stateName), tokenType);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid token type: " + tokenTypeStr);
            }
        }

        return states.get(config.startState);
    }

    public TokenType getTokenType(String lexeme) {
        if (keywords.contains(lexeme)) {
            return TokenType.KEYWORD;
        }
        if (booleanLiterals.contains(lexeme)) {
            return TokenType.BOOLEAN;
        }

        if (lexeme.matches("^\\d+$")) { // Matches int
            return TokenType.NUMBER;
        }
        if (lexeme.matches("^\\d+\\.\\d+$")) { // Matches decimal
            return TokenType.DECIMAL;
        }

        if (lexeme.startsWith("--")) {
            return TokenType.COMMENT;
        }

        DFAState state = startState;
        for (char c : lexeme.toCharArray()) {
            state = state.getNextState(c);
            if (state == null) return TokenType.UNKNOWN;
        }

        return acceptingStates.getOrDefault(state, TokenType.IDENTIFIER);
    }


    public static class DFAState {
        private final String name;
        private final boolean isAccepting;
        private final Map<Character, DFAState> transitions = new HashMap<>();

        public DFAState(String name, boolean isAccepting) {
            this.name = name;
            this.isAccepting = isAccepting;
        }

        public void addTransition(char symbol, DFAState nextState) {
            transitions.put(symbol, nextState);
        }

        public DFAState getNextState(char c) {
            return transitions.get(c);
        }
    }

    private static class DFAConfig {
        String startState;
        List<String> states = new ArrayList<>();
        Map<String, String> acceptingStates = new HashMap<>();
        List<TransitionRule> transitions = new ArrayList<>();
        List<String> keywords = new ArrayList<>();
        List<String> booleanLiterals = new ArrayList<>();
    }

    private static class TransitionRule {
        String from;
        String to;
        String characters;

        TransitionRule(String from, String to, String characters) {
            this.from = from;
            this.to = to;
            this.characters = characters;
        }
    }
}
