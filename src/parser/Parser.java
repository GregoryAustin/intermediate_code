import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Parser {
    private boolean verbose = false;
    private ParserContext context;
    private TokenList lexerList;
    private Queue<Character> list;
    private Stack<String> stack;
    private Stack<TreeNode> nodeStack;
    private int bigCounter;
    private TreeNode root;

    public Parser(TokenList lexerList) {
        this.lexerList = lexerList;
        // Pass in a new copy of the list as convertToParseFormat is destructive
        list = convertToParseFormat(new TokenList(lexerList));
        this.lexerList.addToken(bigCounter++, "eof", "$");
        list.add('$');
        stack = new Stack<String>();
        nodeStack = new Stack<TreeNode>();
        context = new ParserContext();
        bigCounter = 0;
    }

    private Queue<Character> convertToParseFormat(TokenList list) {
        TokenNode tmp = null;
        Queue<Character> tmpQ = new LinkedList<Character>();

        //All this business is adding
        while (!list.isEmpty()) {
            tmp = list.removeFromHead();
            // for integers
            // integer -> b

            // for user-defined name
            // user-defined name -> u

            // for short strings
            // short string -> s
            if (tmp.tokenClass.equals("integer") || tmp.tokenClass.equals("user-defined name") || tmp.tokenClass.equals("short string")) {
                if (tmp.tokenClass.equals("integer")) {
                    //if (tmp.snippet.equals("0")) tmpQ.add('0');
                    //else if (tmp.snippet.equals("1")) tmpQ.add('1');
                    //else
                    tmpQ.add('b');
                } else if (tmp.tokenClass.equals("user-defined name")) {
                    tmpQ.add('u');
                } else if (tmp.tokenClass.equals("short string")) {
                    tmpQ.add('s');
                }
            } else {
                if (tmp.tokenClass.equals("keyword")) {
                    switch (tmp.snippet) {
                        // "eq" -> e/
                        // "and"-> a/
                        // "or" -> o/
                        // "not" -> n
                        // "add" -> d
                        // "sub" -> q
                        // "mult" -> m
                        // "if" -> f
                        // "then" -> t
                        // "else" -> l
                        // "while" -> w
                        // "for" -> r
                        // "input" -> i
                        // "output" -> z
                        // "halt" -> h
                        // "proc" -> p
                        case "eq":
                            tmpQ.add('e');
                            break;
                        case "and":
                            tmpQ.add('a');
                            break;
                        case "or":
                            tmpQ.add('o');
                            break;
                        case "not":
                            tmpQ.add('n');
                            break;
                        case "add":
                            tmpQ.add('d');
                            break;
                        case "sub":
                            tmpQ.add('q');
                            break;
                        case "mult":
                            tmpQ.add('m');
                            break;
                        case "if":
                            tmpQ.add('f');
                            break;
                        case "then":
                            tmpQ.add('t');
                            break;
                        case "else":
                            tmpQ.add('l');
                            break;
                        case "while":
                            tmpQ.add('w');
                            break;
                        case "for":
                            tmpQ.add('r');
                            break;
                        case "input":
                            tmpQ.add('i');
                            break;
                        case "output":
                            tmpQ.add('z');
                            break;
                        case "halt":
                            tmpQ.add('h');
                            break;
                        case "proc":
                            tmpQ.add('p');
                            break;
                        default:
                            System.out.println("system error with keyword parse conversion ");
                            break;
                    }
                } else if (tmp.tokenClass.equals("grouping symbol")) {
                    //Snippets for grouping symbols
                    // (
                    // )
                    // {
                    // }
                    // ,
                    // ;
                    tmpQ.add(tmp.snippet.charAt(0));

                } else if (tmp.tokenClass.equals("comparison symbol")) {
                    tmpQ.add(tmp.snippet.charAt(0));
                } else if (tmp.tokenClass.equals("assignment operator")) {
                    tmpQ.add(tmp.snippet.charAt(0));
                } else {
                    System.out.println("system error with parse conversion");
                    System.out.println(tmp.tokenClass);
                }
            }

        }


//snippets for comparison symbols
// <
// >

//Snippets for assignment operator
// =
        return tmpQ;

    }

    public Boolean parse() {
        Character curSymbol;
        boolean first = true;

        stack.push(Integer.toString(0));
        curSymbol = list.remove();
        TreeNode tmpNode;
        TokenNode curToken = lexerList.removeFromHead();
        TreeNode curNode = new TreeNode(bigCounter++, curToken.tokenClass, curToken.snippet);//change 0 to ID
        nodeStack.push(curNode);

        while (true) {
            if (stack.peek() == null) {
                System.out.println("Syntax error: null ptr" + curSymbol);
                return false;
            }

            String tempString = context.getState(curSymbol, Integer.parseInt(stack.peek()));

            if (tempString == null) {
                System.out.println("Syntax error: " + curSymbol);
                return false;
            } else if (tempString.equals("acc")) {
                if (verbose) System.out.println("Syntax accepted");
                root = nodeStack.pop();
                break;
            } else if (tempString.charAt(0) == 's') {
                if (verbose) System.out.println("In Parse: shift " + tempString.substring(1));
                stack.push(tempString.substring(1));
                curSymbol = list.remove();

                if (!first) {
                    curToken = lexerList.removeFromHead();
                    curNode = new TreeNode(bigCounter++, curToken.tokenClass, curToken.snippet);//change 0 to ID
                    nodeStack.push(curNode);

                } else {
                    first = false;
                }
            } else //==r
            {
                int production = Integer.parseInt(tempString.substring(1));
                Character n = LHSymbol(production);
                int r = RHSymbolNum(production);

                if (verbose) System.out.println("In Parse: reduce " + tempString.substring(1));


                tmpNode = new TreeNode(bigCounter++, Character.toString(n), "none");//change 0 to ID
                if (verbose) System.out.println("TmpNode set to: " + Character.toString(n));
                for (int i = 0; i < r; i++) {
                    tmpNode.addChild(nodeStack.pop());
                    if (verbose) System.out.println("Popping this as well: " + stack.peek());
                    stack.pop();
                }
                if (verbose) {
                    System.out.println("\t about to push " + context.getState(n, Integer.parseInt(stack.peek())));
                    System.out.println("\t where n is " + n);
                    System.out.println("\t where stackpeek is " + Integer.parseInt(stack.peek()));
                }

                stack.push(context.getState(n, Integer.parseInt(stack.peek())));
                nodeStack.push(tmpNode);
            }
        }


        return true;
    }

    public Character LHSymbol(int production) {
        switch (production) {
            case 0:
                return 'E';
            case 1:
                return 'Q';
            case 2:
                return 'P';
            case 3:
                return 'P';
            case 4:
                return 'D';
            case 5:
                return 'D';
            case 6:
                return 'R';
            case 7:
                return 'C';
            case 8:
                return 'C';
            case 9:
                return 'I';
            case 10:
                return 'I';
            case 11:
                return 'I';
            case 12:
                return 'I';
            case 13:
                return 'I';
            case 14:
                return 'O';
            case 15:
                return 'Y';
            case 16:
                return 'V';
            case 17:
                return 'V';
            case 18:
                return 'S';
            case 19:
                return 'N';
            case 20:
                return 'A';
            case 21:
                return 'U';
            case 22:
                return 'U';
            case 23:
                return 'X';
            case 24:
                return 'X';
            case 25:
                return 'X';
            case 26:
                return 'L';
            case 27:
                return 'L';
            case 28:
                return 'L';
            case 29:
                return 'W';
            case 30:
                return 'W';
            case 31:
                return 'B';
            case 32:
                return 'B';
            case 33:
                return 'B';
            case 34:
                return 'B';
            case 35:
                return 'B';
            case 36:
                return 'B';
            case 37:
                return 'Z';
            case 38:
                return 'Z';
            case 39:
                return 'O';
            case 40:
                return 'I';
            case 41:
                return 'U';
            case 42:
                return 'T';
            case 43:
                return 'T';
            default:
                return '#';

        }
    }

    public int RHSymbolNum(int production) {
        switch (production) {
            case 0:
                return 1;
            case 1:
                return 1;
            case 2:
                return 1;
            case 3:
                return 3;
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 5;
            case 7:
                return 1;
            case 8:
                return 3;
            case 9:
                return 1;
            case 10:
                return 1;
            case 11:
                return 1;
            case 12:
                return 1;
            case 13:
                return 1;
            case 14:
                return 4;
            case 15:
                return 1;
            case 16:
                return 1;
            case 17:
                return 1;
            case 18:
                return 1;
            case 19:
                return 1;
            case 20:
                return 3;
            case 21:
                return 1;
            case 22:
                return 1;
            case 23:
                return 1;
            case 24:
                return 1;
            case 25:
                return 1;
            case 26:
                return 6;
            case 27:
                return 6;
            case 28:
                return 6;
            case 29:
                return 8;
            case 30:
                return 12;
            case 31:
                return 6;
            case 32:
                return 5;
            case 33:
                return 5;
            case 34:
                return 2;
            case 35:
                return 6;
            case 36:
                return 6;
            case 37:
                return 7;
            case 38:
                return 22;
            case 39:
                return 4;
            case 40:
                return 1;
            case 41:
                return 1;
            case 42:
                return 1;
            case 43:
                return 1;
            default:
                return -1;
        }
    }

    //Get a symbol and number
    //r means reduce
    //reduce has a production number next to it
    //s means shift
    //shift is a state number next to it


    //A symbol is read from the input and pushed on the stack
    //The symbol
    //after the symbol the state is put
    private void shift(Character symbol, int state) {
        //create new node
        //push new node to reference stack
        stack.push(symbol.toString());
        stack.push(Integer.toString(state));
    }

    //The right hand side of a production are replaced by the
    //nonterminal on the left hand side.
    //and popped off the stack
    //important: the last symbol on the right hand side is the top of the stack
    //When things are reduced the new top of the stack is used for the next state
    //then the left hand side is put and then the state. (always symbol then state)
    //when reduced the right hand side is the children of the left hand side
    private Character reduce(int production) {
        if (verbose) {
            System.out.println("REDUCE:reducing on production: " + production);
            System.out.println("REDUCE: just popped " + stack.pop());
            System.out.println("REDUCE: just popped " + stack.pop());
            System.out.println("REDUCE: stack is " + stack);
        }
        return 'S';
    }

    public String toString() {
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

    public void prune() {
        if (root != null) root.prune();
    }

    public TreeNode getRoot() {
        return root;
    }
}
