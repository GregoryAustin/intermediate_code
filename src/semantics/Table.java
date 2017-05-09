import java.util.ArrayList;
import java.util.*;

/**
 * Bucket class needed for external chaining
 */
class Bucket {
    String key;
    TreeNode binding;
    Bucket next;

    public Bucket(String key, TreeNode binding, Bucket next) {
        this.key = key;
        this.binding = binding;
        this.next = next;
    }
}

class Scope {
    Bucket[] table;
    boolean inScope;
    int scopeID;

    Scope(Bucket[] table, boolean inScope, int scopeID) {
        this.table = table;
        this.inScope = inScope;
        this.scopeID = scopeID;
    }
}

/**
 * Symbol table used to bind identifier names to nodes in the tree
 */
public class Table {
    private static int scopeCounter = 0;
    private static int id = 0; // ID for unique renaming
    public int currentScope;
    public static ArrayList<Scope> scopes = new ArrayList<>();
    public static ArrayList<TreeNode> procCalls = new ArrayList<>();
    public LinkedList<TreeNode> delProcs = new LinkedList<>();
    private final int SIZE = 256;
    private Bucket table[];

    public Table() {
        empty();
    }

    private void empty() {
        table = new Bucket[SIZE];
    }

    private int hash(String key) {
        int h = 0;
        for (int i = 0; i < key.length(); ++i) {
            h = h * 31 + key.charAt(i);
        }
        if (h < 0) h = Math.abs(h);
        return h % SIZE;
    }

    /**
     * Searches all scopes for the given key
     *
     * @param key The identifier name to look up
     * @return Returns the node where it was declared
     */
    public TreeNode lookup(String key) {
        // TODO: Extend to search all scopes once implemented
        int index = hash(key);
        for (Bucket b = table[index]; b != null; b = b.next)
            if (key.equals(b.key)) return b.binding;
        for (Scope scope : scopes) {
            if (scope.inScope) {
                for (Bucket b = scope.table[index]; b != null; b = b.next)
                    if (key.equals(b.key)) return b.binding;
            }
        }
        return null;
    }

    /**
     * Searches all scopes for the given key with the given type
     *
     * @param key
     * @param type
     * @return
     */
    public TreeNode lookup(String key, char type) {
        int index = hash(key);
        //System.out.println("Lookup for " + key + " of type " + type);
        for (Bucket b = table[index]; b != null; b = b.next)
            if (key.equals(b.key) && b.binding.type == type) return b.binding;
        //System.out.println("Not found");
        for (Scope scope : scopes) {
            if (scope.inScope) {
                for (Bucket b = scope.table[index]; b != null; b = b.next)
                    if (key.equals(b.key) && b.binding.type == type) return b.binding;
            }
        }
        return null;
    }

    public boolean itExistsInCurrentScope(String key, char type) {
        int index = hash(key);
        for (Bucket b = table[index]; b != null; b = b.next)
            if (key.equals(b.key) & b.binding.type == type) return true;
        return false;
    }

    /**
     * Puts the specified node into the Table, bound to the specified key (Identifier name).
     */
    public void bind(String key, TreeNode value) {
        if (itExistsInCurrentScope(key, value.type)) return;
        int index = hash(key);
        value.setID(id++);
        table[index] = new Bucket(key, value, table[index]);
    }

    /**
     * Remembers the current state of the Table and increments scope counter
     */
    public void enter() {
        scopes.add(new Scope(table, true, currentScope));
        empty();
        currentScope = ++scopeCounter;
    }

    /**
     * Restores the table to what it was at the most recent enter
     * that has not already been ended.
     */
    public void exit() {
        // Keep a copy of the scope for later use
        scopes.add(new Scope(table, false, currentScope));
        for (int x = scopes.size() - 1; x >= 0; --x) {
            if (scopes.get(x).inScope) {
                table = scopes.get(x).table;
                currentScope = scopes.get(x).scopeID;
                scopes.remove(x);
                return;
            }
        }
    }

    public void addProcCall(TreeNode proc) {
        for (TreeNode procCall : procCalls) {
            if (procCall.snippet.equals(proc.snippet)){
              delProcs.add(proc);
              return;
            }
        }

        procCalls.add(proc);
    }

    public void allProcCallsHaveDefs() throws Analyzer.ScopeError {
        if (procCalls.size() != 0) {
            throw new Analyzer.ScopeError("Procedure \"" + procCalls.get(0).snippet + "\" was not defined!");
        }
    }

    public void pruneProcCall(String procName, int nodeID) {
        for (TreeNode procCall : procCalls) {
            if (procCall.snippet.equals(procName)) {
                procCall.setID(nodeID);
                procCalls.remove(procCall);
                return;
            }
        }
    }

    public void doDelProcs(String procName, int nodeID) {
      for (int i = 0; i < delProcs.size(); ++i) {
        if (delProcs.get(i).snippet.equals(procName)) {
          delProcs.get(i).setID(nodeID);
          delProcs.remove(i);
          i--;
        }
      }
    }

    public Bucket[] getTable() {
        return table;
    }

    @Override
    public String toString() {
      // Collections.sort(scopes, (x, y) -> x.scopeID.compareTo(y.scopeID) < 0);
      // Collections.sort(x -> x.scopeID);
      String output = "";
      enter();
      for (Scope scope : scopes) {
          output += "===== Scope " + scope.scopeID + " =====\n";
          for (int x = 0; x < SIZE; ++x) {
            Bucket y = scope.table[x];
            if (y != null) {
              while (y != null) {
                output += y.binding.snippet + " - " + y.binding.type + y.binding.getID() + "\n";
                y = y.next;
              }
            }
          }
          output += "\n";
      }
      return output;
    }

}
