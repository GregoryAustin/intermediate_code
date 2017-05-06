import java.util.*;


public class TreeNode extends TokenNode {
    private LinkedList<TreeNode> children;
    private TreeNode parent;
    //private InfoTable tableEntry;
    public char type;
    private int nodeID;


    public TreeNode(int number, String tokenClass, String snippet) {
        super(number, tokenClass, snippet);
        children = new LinkedList<TreeNode>();
        nodeID = -1;
        //tableEntry = new InfoTable();
        parent = null;
        // Set default type to the null character since empty chars are not allowed
        type = '\u0000';
    }

    public void setID(int id) {
        nodeID = id;
    }

    public int getID () {
        return nodeID;
    }

    public void addChild(TreeNode node) {
        node.parent = this;
        children.addFirst(node);
    }

    public LinkedList<TreeNode> getChildren() {
        return children;
    }

    public int childrenSize() {
        return children.size();
    }

    public TreeNode getChild(int i) {
        return children.get(i);
    }

    public String toString() {
        String ret;
        if (nodeID != -1)
          ret = "||Node " + tokenNo + "\t" + tokenClass
                  + "\t" + snippet + " NodeID " + nodeID + " Parent: " + parent.tokenClass + "||";
        else if (parent != null)
            ret = "||Node " + tokenNo + "\t" + tokenClass
                    + "\t" + snippet + " Parent: " + parent.tokenClass + "||";
        else
            ret = "||Node " + tokenNo + "\t" + tokenClass
                    + "\t" + snippet + " Parent: nun||";
        return ret;
    }

    public void prune() {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).snippet.equals("{") ||
                    children.get(i).snippet.equals("}") ||
                    children.get(i).snippet.equals("(") ||
                    children.get(i).snippet.equals(")") ||
                    children.get(i).snippet.equals(";") ||
                    children.get(i).snippet.equals(",")) {
                children.remove(i);
                i = 0;
            }
        }

        for (int i = 0; i < children.size(); i++) {
            children.get(i).prune();
        }

    }

}
