public class Analyzer {
    private Table symbolTable;
    private TreeNode root;

    public Analyzer(TreeNode root) {
        this.root = root;
    }

    static class TypeError extends Exception {
        public TypeError(String message) {
            //System.out.println(symbolTable.toString());
            super(message);

        }
    }

    static class ScopeError extends Exception {
        public ScopeError(String message) {
            //System.out.println(symbolTable.toString());
            super(message);
        }
    }

    private char checkTypeAndScope(TreeNode currentNode) throws TypeError, ScopeError {
        switch (currentNode.tokenClass) {
            case "Q":
                // Q -> P
                // PROG' -> PROG
                if (currentNode.childrenSize() != 0) {
                    currentNode.type = checkTypeAndScope(currentNode.getChild(0));
                    return currentNode.type;
                }
                break;

            case "P":
                // P -> C
                // PROG -> CODE
                if (currentNode.childrenSize() == 1) {
                    if (checkTypeAndScope(currentNode.getChild(0)) == 'w') {
                        currentNode.type = 'w';
                        return currentNode.type;
                    } else {
                        throw new TypeError("P");
                    }
                }
                // P -> C ; D
                // PROG -> CODE ; PROC_DEFS
                else if (currentNode.childrenSize() == 2) {
                    if (checkTypeAndScope(currentNode.getChild(0)) == 'w' && checkTypeAndScope(currentNode.getChild(1)) == 'w') {
                        currentNode.type = 'w';
                        return currentNode.type;
                    } else {
                        throw new TypeError("P");
                    }
                }
                break;

            case "D":
                // D -> R
                // PROC_DEFS -> PROC
                if (currentNode.childrenSize() == 1) {
                    if (checkTypeAndScope(currentNode.getChild(0)) == 'w') {
                        currentNode.type = 'w';
                        return currentNode.type;
                    } else {
                        throw new TypeError("D");
                    }
                }
                // D -> R D
                // PROC_DEFS -> PROC PROC_DEFS
                else if (currentNode.childrenSize() == 2) {
                    if (checkTypeAndScope(currentNode.getChild(0)) == 'w' && checkTypeAndScope(currentNode.getChild(1)) == 'w') {
                        currentNode.type = 'w';
                        return currentNode.type;
                    } else {
                        throw new TypeError("D");
                    }
                }
                break;
            case "R":
                // R -> p u ( P }
                // PROC -> proc UserDefinedName { PROG }
                // Procedures have their own scope - See Slide 8 of Project2b
                currentNode.getChild(1).type = 'p';
                if (symbolTable.itExistsInCurrentScope(currentNode.getChild(1).snippet, currentNode.getChild(1).type)) {
                    System.out.println("Ignoring duplicate procedure definition for " + currentNode.getChild(1).snippet);
                    return 'w';
                }
                symbolTable.enter();
                if (checkTypeAndScope(currentNode.getChild(2)) == 'w') {
                    currentNode.type = 'w';
                    // It's important that we first leave the scope before binding the procedure name
                    symbolTable.exit();
                    symbolTable.bind(currentNode.getChild(1).snippet, currentNode.getChild(1));
                    symbolTable.pruneProcCall(currentNode.getChild(1).snippet, currentNode.getChild(1).getID());
                    symbolTable.doDelProcs(currentNode.getChild(1).snippet, currentNode.getChild(1).getID());
                    return currentNode.type;
                }
                break;

            case "C":
                // C -> I
                // CODE -> INSTR
                // C -> C ; I
                // CODE -> CODE ; INSTR
            case "I":
                // I -> h
                // INSTR -> halt
                // I -> O
                // INSTR -> IO
                // I -> A
                // INSTR -> ASSIGN
                // I -> W
                // INSTR -> COND_BRANCH
                // I -> Z
                // INSTR -> COND_LOOP
                // I -> Y
                // INSTR -> CALL
                for (int x = 0; x < currentNode.childrenSize(); ++x) {
                    if (checkTypeAndScope(currentNode.getChild(x)) != 'w') throw new TypeError("");
                }
                currentNode.type = 'w';
                return currentNode.type;

            case "O":
                // O -> i ( V )
                // IO -> input ( VAR )
                if (currentNode.getChild(0).snippet.equals("input")) {
                    // TODO: Add binding to symbol table
                    currentNode.getChild(1).type = 'n';
                    checkTypeAndScope(currentNode.getChild(1));
                    currentNode.type = 'w';
                    return currentNode.type;
                }
                // O -> z ( V )
                // IO -> output ( VAR )
                else if (currentNode.getChild(0).snippet.equals("output")) {
                    currentNode.getChild(1).type = 'o';
                    checkTypeAndScope(currentNode.getChild(1));
                    currentNode.type = 'w';
                    return currentNode.type;
                }
                break;

            case "Y":
                // Y -> u
                // CALL -> UserDefinedName
                currentNode.getChild(0).type = 'p';
                symbolTable.addProcCall(currentNode.getChild(0));
                currentNode.type = 'w';
                return currentNode.type;

            case "V":
                // V -> S
                // VAR -> SVAR
                // V -> N
                // VAR -> NVAR
                if (currentNode.type == 'n') {
                    currentNode.getChild(0).type = 'n';
                    checkTypeAndScope(currentNode.getChild(0));
                    return currentNode.type;
                } else if (currentNode.type == 'o') {
                    // Look up type in the symbol table - Throw exception if it doesn't exist
                    // No ambiguity because the last declared symbol is used
                    currentNode.getChild(0).type = 'o';
                    checkTypeAndScope(currentNode.getChild(0));
                } else {
                    System.out.println("Else in V executed. Current Node: " + currentNode.toString());
                }

                break;
            case "S":
                // S can possibly be "short string" or "integer"
                // S -> u
                // SVAR -> UserDefinedName
                if (currentNode.getChild(0).snippet.equals("code")) {
                  System.out.println(currentNode.tokenNo);
                }
                if (currentNode.type == 'n') {System.out.println(currentNode.tokenNo + "n");
                    TreeNode node1 = symbolTable.lookup(currentNode.getChild(0).snippet, 'n');
                    currentNode.type = 'n';
                    currentNode.getChild(0).type = 'n';

                    if (node1 == null) {
                        symbolTable.bind(currentNode.getChild(0).snippet, currentNode.getChild(0));
                    } else {
                        currentNode.getChild(0).setID(node1.getID());
                    }
                    return currentNode.type;
                } else if (currentNode.type == 's') {
                    currentNode.getChild(0).type = 's';
                    symbolTable.bind(currentNode.getChild(0).snippet, currentNode.getChild(0));
                    return currentNode.type;
                } else {
                    TreeNode node = null;
                    if (symbolTable.lookup(currentNode.getChild(0).snippet, 'n') != null) {
                        node = symbolTable.lookup(currentNode.getChild(0).snippet, 'n');
                    }
                    else if (symbolTable.lookup(currentNode.getChild(0).snippet, 's') != null) {
                      node = symbolTable.lookup(currentNode.getChild(0).snippet, 's');

                    }
                    // else {
                    //   throw new ScopeError(currentNode.getChild(2).getChild(0).snippet + " has not been declared!");
                    // }
                    if (node == null) {
                        throw new ScopeError(currentNode.getChild(0).snippet + " has not been declared!");
                    } else {
                        currentNode.getChild(0).type = currentNode.type = node.type;
                        currentNode.getChild(0).setID(node.getID()); // new
                        return currentNode.type;
                    }
                }

            case "N":
                // N -> u
                // NVAR -> UserDefinedName
              //  currentNode.type = 'n';
              //  currentNode.getChild(0).type = 'n';
              //  symbolTable.bind(currentNode.getChild(0).snippet, currentNode.getChild(0));
              //
              TreeNode node1 = symbolTable.lookup(currentNode.getChild(0).snippet, 'n');
              currentNode.type = 'n';
              currentNode.getChild(0).type = 'n';

              if (node1 == null) {
                  symbolTable.bind(currentNode.getChild(0).snippet, currentNode.getChild(0));
              } else {
                  currentNode.getChild(0).setID(node1.getID());
              }
               return currentNode.type = 'n';

            case "A":
                // A -> T = U
                // ASSIGN -> T = U
                // Probably one of the most important. Check in this order:

                // If left child is not in table
                //      If right child is "short string" or "integer", add binding for left child with same type
                //      If right child is "user-defined name", look up type and add binding for left child with same type
                //      Assign 'w' to this node and return it
                // Else if left child is in table
                //      If right child is "short string" or "integer", check that the two types match
                //      If right child is "user-defined name", look up type and check that the two types match
                //      Throw exception if types don't match
                //      Otherwise assign 'w' to this node and return it
                // char rightChildType;

                checkTypeAndScope(currentNode.getChild(2));
                char rightChildType = currentNode.getChild(2).type;

                if (symbolTable.lookup(currentNode.getChild(0).getChild(0).getChild(0).snippet) == null) {
                    if (rightChildType == 's' || rightChildType == 'n') {
                        currentNode.getChild(0).getChild(0).getChild(0).type = rightChildType;
                        currentNode.getChild(0).getChild(0).type = rightChildType;
                        // TODO: Propagate this down safely instead
                        symbolTable.bind(currentNode.getChild(0).getChild(0).getChild(0).snippet, currentNode.getChild(0).getChild(0).getChild(0));
                        return currentNode.type = 'w';
                    } else {
                        System.out.println(rightChildType == '\u0000');
                        throw new TypeError("A -> T = U (1): " + currentNode.toString());
                    }
                } else {
                    TreeNode node = symbolTable.lookup(currentNode.getChild(0).getChild(0).getChild(0).snippet);
                    char leftChildType = node.type;

                    currentNode.getChild(0).getChild(0).getChild(0).setID(node.getID());

                    if (leftChildType == rightChildType) {
                        currentNode.getChild(0).getChild(0).getChild(0).type = rightChildType;
                        return currentNode.type = 'w';
                    } else if (symbolTable.lookup(currentNode.getChild(0).getChild(0).getChild(0).snippet, (leftChildType == 's' ? 'n' : 's')) == null) {
                        currentNode.getChild(0).getChild(0).getChild(0).type = (leftChildType == 's' ? 'n' : 's');
                        symbolTable.bind(currentNode.getChild(0).getChild(0).getChild(0).snippet, currentNode.getChild(0).getChild(0).getChild(0));
                        return currentNode.type = 'w';
                    } else {
                      currentNode.getChild(0).getChild(0).getChild(0).type = rightChildType;
                        return currentNode.type = 'w';
                    }
                }

            case "U":
                // U -> S
                // U -> SVAR
                // U -> s
                // U -> "short string"
                // U -> X
                // U -> NUMEXPR
                currentNode.type = checkTypeAndScope(currentNode.getChild(0));
                return currentNode.type;

            case "T":
                // T -> S
                // T -> SVAR
                // T -> N
                // T -> NVAR
                currentNode.type = checkTypeAndScope(currentNode.getChild(0));
                return currentNode.type;

            case "X":
                // X -> N
                // NUMEXPR -> NVAR
                // X -> L
                // NUMEXPR -> CALC
                // X -> b
                // NUMEXPR -> "integer"
                if (currentNode.getChild(0).tokenClass.equals("N") && symbolTable.lookup(currentNode.getChild(0).getChild(0).snippet, 'n') == null)
                    throw new ScopeError(currentNode.getChild(0).getChild(0).snippet + " has not been declared!");
                if (currentNode.getChild(0).tokenClass.equals("N")) // new
                    currentNode.getChild(0).getChild(0).setID(symbolTable.lookup(currentNode.getChild(0).getChild(0).snippet, 'n').getID());// new
                return currentNode.type = checkTypeAndScope(currentNode.getChild(0));

            case "L":
                // L -> d ( X , X )
                // CALC -> add ( NUMEXPR, NUMEXPR )
                // L -> q ( X , X )
                // CALC -> sub ( NUMEXPR, NUMEXPR )
                // L -> m ( X , X )
                // CALC -> mult ( NUMEXPR, NUMEXPR )
                if (checkTypeAndScope(currentNode.getChild(1)) == 'n' && checkTypeAndScope(currentNode.getChild(2)) == 'n') {
                    return currentNode.type = 'n';
                } else {
                    throw new TypeError("L -> dqm(X,X): " + currentNode.getChild(0) + " " + currentNode.toString());
                }

            case "W":
                // W -> f ( B ) t { C }
                // COND_BRANCH -> if ( BOOL ) then { CODE }
                // W -> f ( B ) t { C } l { C }
                // COND_BRANCH -> if ( BOOL ) then { CODE } else { CODE }
                if (checkTypeAndScope(currentNode.getChild(1)) != 'b') {
                    throw new TypeError("Condition not a boolean!");
                }
                if (checkTypeAndScope(currentNode.getChild(3)) == 'w') {
                    if (currentNode.childrenSize() == 6) {
                        if (checkTypeAndScope(currentNode.getChild(5)) == 'w') {
                            return currentNode.type = 'w';
                        } else {
                            throw new TypeError("W");
                        }
                    }
                    return currentNode.type = 'w';
                } else {
                    throw new TypeError("W");
                }

            case "B":
                // B -> e ( V , V )
                // BOOL -> eq ( VAR , VAR )
                // B -> ( N < N )
                // BOOL -> ( NVAR < NVAR )
                // B -> ( N > N )
                // BOOL -> ( NVAR > NVAR )
                // B -> n B
                // BOOL -> not BOOL
                // B -> a ( BOOL , BOOL )
                // BOOL -> and ( BOOL , BOOL }
                // B -> o ( BOOL , BOOL )
                // BOOL -> or ( BOOL , BOOL }
                if (currentNode.getChild(0).snippet.equals("eq")) {
                    currentNode.getChild(1).type = 'o';
                    checkTypeAndScope(currentNode.getChild(1));
                    currentNode.getChild(2).type = 'o';
                    checkTypeAndScope(currentNode.getChild(2));
                    return currentNode.type = 'b';
                } else if (currentNode.getChild(0).snippet.equals("and") || currentNode.getChild(0).snippet.equals("or")) {
                    checkTypeAndScope(currentNode.getChild(1));
                    checkTypeAndScope(currentNode.getChild(2));
                    return currentNode.type = 'b';
                } else if (currentNode.getChild(0).snippet.equals("not")) {
                    checkTypeAndScope(currentNode.getChild(1));
                    return currentNode.type = 'b';
                } else {
                    // < or >
                    TreeNode tmp1 = symbolTable.lookup(currentNode.getChild(0).getChild(0).snippet, 'n');
                    if (tmp1 == null) {
                      throw new ScopeError(currentNode.getChild(0).getChild(0).snippet + " has not been declared!");
                    } else {
                      checkTypeAndScope(currentNode.getChild(0));
                      currentNode.getChild(0).getChild(0).setID(tmp1.getID());

                    }


                    TreeNode tmp2 = symbolTable.lookup(currentNode.getChild(2).getChild(0).snippet, 'n');
                    if (tmp2 == null) {
                      throw new ScopeError(currentNode.getChild(2).getChild(0).snippet + " has not been declared!");
                    } else {
                      checkTypeAndScope(currentNode.getChild(2));
                      currentNode.getChild(2).getChild(0).setID(tmp2.getID());
                    }



                    return currentNode.type = 'b';
                }
            case "Z":
                // Z -> w ( B ) { C }
                // COND_LOOP -> while ( BOOL ) { CODE }
                // Z -> r ( N = "integer" ; N < N ; N = d ( N , b ) ) { C }
                // COND_LOOP -> for ( NVAR = Number ; NVAR < NVAR ; NVAR = add ( NVAR , Number) ) { CODE }
                if (currentNode.getChild(0).snippet.equals("while")) {
                    if (checkTypeAndScope(currentNode.getChild(1)) == 'b' && checkTypeAndScope(currentNode.getChild(2)) == 'w') {
                        return currentNode.type = 'w';
                    } else {
                        throw new TypeError("while (BOOL) {CODE}: " + currentNode.toString());
                    }
                } else {
                    symbolTable.enter();
                    // the naming for this happens in the NVAR -> UserDefinedName
                    currentNode.getChild(1).type = 'n';
                    currentNode.getChild(1).getChild(0).type = 'n';
                    symbolTable.bind(currentNode.getChild(1).getChild(0).snippet, currentNode.getChild(1).getChild(0));

                    checkTypeAndScope(currentNode.getChild(3));

                    checkTypeAndScope(currentNode.getChild(4));
                    currentNode.getChild(4).setID(currentNode.getChild(1).getChild(0).getID());
                    TreeNode tmp = symbolTable.lookup(currentNode.getChild(6).getChild(0).snippet, 'n');
                    if (tmp == null){
                        throw new ScopeError(currentNode.getChild(6).getChild(0).snippet + " has not been declared!");
                    } else {

                      checkTypeAndScope(currentNode.getChild(6));
                      currentNode.getChild(6).getChild(0).setID(tmp.getID());
                    }

                    checkTypeAndScope(currentNode.getChild(7));
                    currentNode.getChild(7).getChild(0).setID(currentNode.getChild(1).getChild(0).getID());
                    checkTypeAndScope(currentNode.getChild(10));
                    currentNode.getChild(10).getChild(0).setID(currentNode.getChild(1).getChild(0).getID());
                    checkTypeAndScope(currentNode.getChild(11));

                    symbolTable.exit();
                    if (checkTypeAndScope(currentNode.getChild(12)) == 'w') {
                        return currentNode.type = 'w';
                    } else {
                        throw new TypeError("for(NVAR=0 ; NVAR<NVAR ; NVAR=add(NVAR,1)) {CODE}: " + currentNode.toString());
                    }
                }

            case "keyword":
                // halt
                return 'w';

            case "short string":
                currentNode.type = 's';
                return currentNode.type;

            case "integer":
                currentNode.type = 'n';
                return currentNode.type;

            default:
                System.out.println("Analyser got node " + currentNode.toString());
                break;
        }

        return currentNode.type;
    }

    public boolean checkTypeAndScope() {
        // scopes and scope counter might not be reset
        symbolTable = new Table();
        try {
            checkTypeAndScope(root);
            System.out.println("Type checking successful.");
            // This is done seperately because proc defs happen after calls, unlike variable decl-usage paths
            symbolTable.allProcCallsHaveDefs();
            System.out.println("Scope analysis successful.");
            return true;
        } catch (TypeError | ScopeError typeError) {
            System.out.println(typeError.getMessage());
            System.exit(1);
        }
        return false;
    }

    public String toString() {
        // return DFPrint(root, 0);
        return symbolTable.toString();
    }

    public TreeNode getRoot() {
      return root;
    }

    public String syntaxTree () {
      return DFPrint(root, 0);
    }

    public String DFPrint(TreeNode cur, int depth) {
        String ret = "";
        if (cur != null) {
            for (int i = 0; i < depth; i++)
                ret += "---";
            ret += cur.toString() + '\n';

            for (int i = 0; i < cur.childrenSize(); i++)
                ret += DFPrint(cur.getChild(i), depth + 1);
        }
        return ret;
    }
}
