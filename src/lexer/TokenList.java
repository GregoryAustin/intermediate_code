import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TokenList {
    private TokenNode head;
    private TokenNode tail;
    private int count;

    public TokenList() {
        head = null;
        tail = null;
        count = 0;
    }

    public TokenList(TokenList other) {
        head = null;
        tail = null;
        count = 0;
        TokenNode cur = other.head;
        while (cur != null) {
            addToken(cur.tokenClass, cur.snippet);
            cur = cur.next;
        }
    }

    public TokenList(String fileName) {
        head = null;
        tail = null;
        count = 0;
        readFromFile(fileName);
    }

    public void addToken(String tokenClass, String snippet) {
        TokenNode newNode = new TokenNode(count, tokenClass, snippet);

        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        count++;
    }

    public void addToken(int id, String tokenClass, String snippet) {
        TokenNode newNode = new TokenNode(id, tokenClass, snippet);

        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        count++;
    }

    boolean isEmpty() {
        return head == null;
    }

    public String toString() {
        TokenNode cur = head;
        String outString = "";
        while (cur != null) {
            outString += cur.tokenNo + "\t" + cur.tokenClass + "\t" + cur.snippet + "\n";
            cur = cur.next;
        }
        return outString;
    }

    //new functions yay
    void readFromFile(String fileName) {
        File file = new File(fileName);
        try {
            Scanner scan = new Scanner(file);
            scan.useDelimiter("\t");

            String tempID;
            String tempClass;
            String tempSnippet;
            while (scan.hasNext()) {
                tempID = scan.next();
                tempClass = scan.next();
                scan.useDelimiter("\n");
                tempSnippet = scan.next();
                tempSnippet = tempSnippet.substring(1);
                scan.useDelimiter("");

                if (scan.hasNext()) {
                    scan.next();
                }
                scan.useDelimiter("\t");

                addToken(Integer.parseInt(tempID), tempClass, tempSnippet);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("error creating linked list from file");
        }
    }

    TokenNode removeFromHead() {
        TokenNode tmp = head;
        head = head.next;
        if (head == null) tail = null;
        return tmp;
    }

}