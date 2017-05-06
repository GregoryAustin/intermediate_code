import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Used for unit tests of hash table used by semantic analyser
 * Run with java -cp out/:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar org.junit.runner.JUnitCore TableTester
 */
public class TableTester {
    private static Table table = new Table();

    @Test
    public void bindAndLookupSingleName() {
        table = new Table();
        TreeNode node = new TreeNode(0, "user-defined name", "var1");
        node.type = 'n';
        table.bind(node.snippet, node);
        assertEquals(node, table.lookup(node.snippet));
    }

    @Test
    public void bindAndLookupSingleNameDifferentTypes() {
        table = new Table();
        TreeNode node1 = new TreeNode(0, "user-defined name", "var1");
        node1.type = 'n';
        TreeNode node2 = new TreeNode(1, "user-defined name", "var1");
        node2.type = 's';
        table.bind(node1.snippet, node1);
        table.bind(node2.snippet, node2);
        assertEquals('s', table.lookup(node1.snippet).type);
    }

    @Test
    public void justReturnsIfBindingAlreadyExists() {
        table = new Table();
        TreeNode node1 = new TreeNode(0, "user-defined name", "var1");
        node1.type = 'n';
        TreeNode node2 = new TreeNode(1, "user-defined name", "var1");
        node2.type = 's';
        table.bind(node1.snippet, node1);
        table.bind(node2.snippet, node2);
        assertEquals(node1, table.lookup(node1.snippet, node1.type));
    }

    @Test
    public void checkThatTableRemainsIntact() {
        table = new Table();
        TreeNode node1 = new TreeNode(0, "user-defined name", "var1");
        node1.type = 'n';
        TreeNode node2 = new TreeNode(1, "user-defined name", "var1");
        node2.type = 's';
        table.bind(node1.snippet, node1);
        table.bind(node2.snippet, node2);
        Bucket[] oldTable = table.getTable();
        table = new Table();
        TreeNode n = null;
        for (Bucket anOldTable : oldTable) if (anOldTable != null) n = anOldTable.binding;
        assertEquals(node2, n);
    }

    @Test
    public void checkThatScopesAreBeingPreserved() {
        table = new Table(); // Enter Scope 0
        TreeNode node = new TreeNode(0, "user-defined name", "var1");
        table.bind(node.snippet, node);
        table.enter(); // Enter Scope 1
        table.enter(); // Enter Scope 2
        table.exit();  // Exit Scope 2
        table.enter();  // Enter Scope 3
        table.exit();   // Exit Scope 3
        table.enter(); // 4 saved scopes
        //for (Scope scope : table.scopes) System.out.println(scope.scopeID);
        //System.out.println("Current Scope: " + table.currentScope);
        assertEquals(4, Table.scopes.size());
    }
}
