# Compiler-Construction-Assignment

This is a custom scripting language which features a C++-like syntax with custom keywords, a lexer using DFA-based tokenization, an error handler for robust debugging, and a symbol table for managing variables and constants.

**Features**
-Variable & Constant Declarations
-Supports integer, decimal, and string types
-assign keyword for defining constants
-Input & Output Handling
-show for displaying output
-take for user input
-Supports +, -, *, /, % operators
-Single-line comments using --
-Multi-line comments using == ... ==
-Detects invalid tokens and undeclared variable usage
-Reports duplicate declarations of constants

-Symbol Table
Tracks variable names, types, values, and scope

-Lexer & Tokenizer
Uses a DFA for token classification

Identifies keywords, operators, literals, and identifiers

HOW IT WORKS:

Lexical Analysis
The lexer reads the input and classifies tokens using a DFA.
It removes comments and processes keywords, identifiers, and literals.

Symbol Table Management
Tracks variables and constants, ensuring valid usage.

Error Handling
Detects and reports invalid tokens, undeclared variables, and duplicate constants.

Execution
Processes the parsed tokens and executes statements accordingly.

Please provide a .ha script file as input.
