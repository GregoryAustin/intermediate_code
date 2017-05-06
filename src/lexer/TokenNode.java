public class TokenNode {
    public int tokenNo;
    public String tokenClass;
    public String snippet;

    public TokenNode next;

    public TokenNode(int number, String tokenClass, String snippet) {
        tokenNo = number;
        this.tokenClass = tokenClass;
        this.snippet = snippet;
        next = null;
    }
}