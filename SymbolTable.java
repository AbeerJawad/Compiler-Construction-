import java.util.*;

public class SymbolTable {
    private final Map<String, SymbolEntry> globalScope = new LinkedHashMap<>();
    private final Deque<Map<String, SymbolEntry>> localScopes = new ArrayDeque<>();

    public SymbolTable() {
        enterScope(); //global scope
    }

    public boolean add(String identifier, String type, Object value, boolean isConstant) {
        Map<String, SymbolEntry> currentScope = localScopes.peek();

        if (currentScope.containsKey(identifier)) {
            System.out.println("Error: Identifier '" + identifier + "' already declared in this scope.");
            return false;
        }

        SymbolEntry entry = new SymbolEntry(identifier, type, value, isConstant);
        currentScope.put(identifier, entry);
        return true;
    }

    public SymbolEntry lookup(String identifier) {
        for (Map<String, SymbolEntry> scope : localScopes) {
            if (scope.containsKey(identifier)) {
                return scope.get(identifier);
            }
        }
        return globalScope.get(identifier);
    }

    public boolean update(String identifier, Object newValue) {
        SymbolEntry entry = lookup(identifier);
        if (entry == null) {
            System.out.println("Error: Identifier '" + identifier + "' not found.");
            return false;
        }
        if (entry.isConstant) {
            System.out.println("Error: Cannot modify constant '" + identifier + "'.");
            return false;
        }
        entry.value = newValue;
        return true;
    }

    /** Enter a new local scope */
    public void enterScope() {
        localScopes.push(new LinkedHashMap<>());
    }

    /** Exit the current local scope */
    public void exitScope() {
        if (!localScopes.isEmpty()) {
            localScopes.pop();
        } else {
            System.out.println("Error: Cannot exit global scope.");
        }
    }

    /** Print the symbol table */
    public void printTable() {
        System.out.println("===== Symbol Table =====");
        for (Map.Entry<String, SymbolEntry> entry : globalScope.entrySet()) {
            System.out.println(entry.getValue());
        }
        int scopeLevel = localScopes.size();
        for (Map<String, SymbolEntry> scope : localScopes) {
            System.out.println("---- Scope Level " + scopeLevel + " ----");
            for (SymbolEntry entry : scope.values()) {
                System.out.println(entry);
            }
            scopeLevel--;
        }
        System.out.println("========================");
    }

    /** Symbol entry class */
    public static class SymbolEntry {
        String name;
        String type;
        Object value;
        boolean isConstant;

        public SymbolEntry(String name, String type, Object value, boolean isConstant) {
            this.name = name;
            this.type = type;
            this.value = value;
            this.isConstant = isConstant;
        }

        @Override
        public String toString() {
            return name + " | Type: " + type + " | Value: " + value + " | Constant: " + isConstant;
        }
    }

    public void addEntry(String name, TokenType type, String value, boolean isAssign) {
        String typeStr = type.toString();
        boolean isConstant = isAssign; // Set constant only if 'assign' was used
        add(name, typeStr, value, isConstant);
    }
}
