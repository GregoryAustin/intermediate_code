import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ParserContext {
    private char rowHeader[];
    private String[][] transitionTable = new String[133][48];

    private int getColumn(char token) {
        for (int i = 0; i < rowHeader.length; i++) {
            if (token == rowHeader[i])
                return i;
        }
        return -1;
    }

    public String getState(char token, int nextRow) {
        if (getColumn(token) != -1) {
            return transitionTable[nextRow][getColumn(token)];
        }
        return "invToken";
    }

    public String getState(char token) {
        return getState(token, 0);
    }


    // 50 columns
    public ParserContext() {
        //Use this row header to determine next state
        char rowStuff[] = {'(', ')', ',', ';', '<', '=', '>', 'a', 'b', 'd', 'e', 'f', 'h', 'i', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'w', 'z', '{', '}', '$', 'A', 'B', 'C', 'D', 'I', 'L', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        rowHeader = rowStuff;

        //This business parses the file and gets the rows (numbered the same as in JFLAP and columns)
        File file = new File("parse_table.txt");
        try {
            Scanner scan = new Scanner(file);
            scan.useDelimiter("\n");
            String str = "";
            int row = 0;
            while (scan.hasNext()) {
                //System.out.print("Row " + row + ": ");

                str = scan.next();
                char[] charArr = str.toCharArray();
                //System.out.println(charArr);
                String[] strArr = new String[48];
                int count = 0;
                String tmp = "";

                int padding = 0;
                boolean pad = true;
                while (pad) {
                    if (charArr[padding] != '\t')
                        padding++;
                    else
                        pad = false;
                }
                padding++;
                for (int i2 = padding; i2 < charArr.length; i2++) {
                    if (charArr[i2] == '\t') {
                        strArr[count] = null;
                        count++;
                    } else {
                        tmp = "";
                        while (charArr[i2] != '\t' && i2 < charArr.length - 1) {
                            tmp += charArr[i2];
                            if (i2 < charArr.length - 1)
                                i2++;
                        }
                        if (i2 == charArr.length - 1)
                            if (charArr[charArr.length - 1] != '\t')
                                tmp += charArr[charArr.length - 1];

                        strArr[count] = tmp;
                        count++;
                    }
                }
                for (int i = 0; i < strArr.length; i++) {
                    transitionTable[row][i] = strArr[i];
                    //System.out.print(strArr[i] + "|");
                } //System.out.println();
                //System.out.println(count);
                row++;
            }
            //System.out.println (printRow(0));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String printRow(int row) {
        String tmp = "";
        for (int i = 0; i < 48; i++) {
            tmp += transitionTable[row][i] += " ";
        }
        return tmp;
    }
    //The best way to visualize how this should work is by playing with JFLAP


}