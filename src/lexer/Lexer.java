import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Lexer {
    private Context context;
    private TokenList lst = new TokenList();

    //So this baby has to take in input
    public Lexer(String splFile) {
        context = new Context();

        File file = new File(splFile);
        try {
            Scanner sc = new Scanner(file);
            sc.useDelimiter("");

            int state = 0;
            String snippet = "";
            boolean consume = true;
            char c = '-';

            //this is for debugging purposes
            String numbas = "0 ";

            //This loop goes through each character and adds tokens to the list or errors out
            while (sc.hasNext()) {
                //The variable consume is used to protect against multiple spaces and consuming more than you should
                //It kind of acts as a peek() method, but there is no peek() for Scanner objects.
                if (consume)
                    c = sc.next().charAt(0);
                else
                    consume = true;

                String type = context.getDescription(state);
                state = context.getNextState(state, c);

                if (state < 41) {
                    snippet += c;
                } else {
                    consume = false;
                }

                numbas += state + " ";

                if (state > 40) {
                    if (state == 41) {
                        if (type.equals("space") || type.equals("newline")) {
                        } else if (context.isKeyword(snippet) > 0) {
                            type = "keyword";
                            lst.addToken(type, snippet);
                        } else {
                            lst.addToken(type, snippet);
                        }

                    } else if (state > 41) {
                        snippet += c;
                        System.out.println("Lexical Error: |" + snippet + "| = " + context.getDescription(state) + ". Scanning aborted.");
                        System.exit(1);
                        //System.out.println("ERROR " + context.getDescription(state) +  ": " + snippet);
                    }

                    snippet = "";
                    state = 0;
                    numbas = state + " ";
                }
            }

            //This code is to handle when there is no space or linebreak (showing the end)
            //It also takes care of incomplete strings
            //It handles a case where there is only one inverted comma followed by nothing

            if (state >= 3 && state <= 11 && state != -1) {
                System.out.println("Lexical Error: |" + snippet + "| = " + context.getDescription(state) + ". Scanning aborted.");
                System.exit(1);
            }


            String type = "";

            for (int i = 0; i < 2; i++) {
                type = context.getDescription(state);
                state = context.getNextState(state, c);

                if (type.equals("initial"))
                    snippet += c;


                if (state == 41) {
                    if (type.equals("space") || type.equals("newline") || type.equals("done")) {
                    } else if (context.isKeyword(snippet) > 0) {
                        type = "keyword";
                        lst.addToken(type, snippet);
                    } else {
                        lst.addToken(type, snippet);
                    }

                } else if (state > 41) {
                    System.out.println("Lexical Error: |" + snippet + "| = " + context.getDescription(state) + ". Scanning aborted.");
                    System.exit(1);
                }
                c = ' ';
            }

            //Because when opening the "" the state is initial. So if there are no spaces at the end of the
            //.spl file then the state is initial and it doesn't see the problem Only after artificial spaces
            //are added that it sees the issue.
            if (state >= 3 && state <= 11) {
                System.out.println("Lexical Error: |" + snippet + "| = " + context.getDescription(state) + ". Scanning aborted.");
                System.exit(1);
            }

            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    ;

    public TokenList getTokenList() {
        return lst;
    }

    public String toString() {
        return lst.toString();
    }
}