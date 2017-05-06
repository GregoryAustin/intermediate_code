public class Context {
    private int transitionTable[][];
    private String description[];
    private String keywords[];

	/*	note: states are represented as integers

		context has the following functions:

		public int getNextState(int state, char x)
		//based on a current state and an input character,
			this function returns the next state

		public String getDescription(int state)
		//if the state is accepting, then it gives the name of the
			token class
		//it also gives the specific errors which could occur;
			errors are states 41 and up


		public boolean isAccepting(int state)
		//checks whether a state is accepting, so
			at the end of a word if it is accepting, then
			it is a valid word in the regular expression

		public int isKeyword(String word)
		//checks whether an word is part of the
			keywords or special words that we have
	*/

    Context() {
        //fill the transitionTable
        int contents[][] =


                {
                        {13, -1, 2, 14, 15, 3, 16, 17, 18, 19, 20, 21, 22, 23, 24, 1},        //0- initial
                        {43, 43, 2, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43},        //1- only a "-"
                        {43, 2, 2, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41},        //2*- number
                        {4, 4, 4, 48, 48, 12, 4, 48, 48, 48, 48, 48, 48, 48, 48, 48},        //3- has an opening "
                        {5, 5, 5, 48, 48, 12, 5, 48, 48, 48, 48, 48, 48, 48, 48, 48},        //4- short string with 1 char
                        {6, 6, 6, 48, 48, 12, 6, 48, 48, 48, 48, 48, 48, 48, 48, 48},        //5- short string with 2 char
                        {7, 7, 7, 48, 48, 12, 7, 48, 48, 48, 48, 48, 48, 48, 48, 48},        //6- short string with 3 char
                        {8, 8, 8, 48, 48, 12, 8, 48, 48, 48, 48, 48, 48, 48, 48, 48},        //7- short string with 4 char
                        {9, 9, 9, 48, 48, 12, 9, 48, 48, 48, 48, 48, 48, 48, 48, 48},        //8- short string with 5 char
                        {10, 10, 10, 48, 48, 12, 10, 48, 48, 48, 48, 48, 48, 48, 48, 48},    //9- short string with 6 char
                        {11, 11, 11, 48, 48, 12, 11, 48, 48, 48, 48, 48, 48, 48, 48, 48},    //10- short string with 7 char
                        {47, 47, 47, 47, 47, 12, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47},    //11- short string with 8 char
                        {41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41},    //12*- closing "
                        {13, 13, 13, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41}        //13*- user defined name
                };
        transitionTable = contents;

        //fill Description array
        String contents2[] =
                {
                        "initial",            //0
                        "just a - sign",        //1
                        "integer",            //2*
                        "input \"",        //3
                        "incomplete short string with 1 character",        //4
                        "incomplete short string with 2 characters",        //5
                        "incomplete short string with 3 characters",        //6
                        "incomplete short string with 4 characters",        //7
                        "incomplete short string with 5 characters",        //8
                        "incomplete short string with 6 characters",        //9
                        "incomplete short string with 7 characters",        //10
                        "incomplete short string with 8 characters",        //11
                        "short string",            //12*
                        "user-defined name",    //13*
                        "comparison symbol",    //14*
                        "comparison symbol",    //15*
                        "space",                //16
                        "newline",            //17
                        "grouping symbol",        //18*
                        "grouping symbol",        //19*
                        "grouping symbol",        //20*
                        "grouping symbol",        //21*
                        "grouping symbol",        //22*
                        "grouping symbol",        //23*
                        "assignment operator",    //24*
                        "comparison symbol",    //25*
                        "boolean operator",        //26*
                        "boolean operator",        //27*
                        "boolean operator",        //28*
                        "number operator",        //29*
                        "number operator",        //30*
                        "number operator",        //31*
                        "control structure",        //32*
                        "control structure",        //33*
                        "control structure",        //34*
                        "control structure",        //35*
                        "control structure",        //36*
                        "io command",            //37*
                        "io command",            //38*
                        "halt",                //39*
                        "procedure",            //40*
                        "done",                //41
                        ////error states
                        "invalid character",        //42
                        "just a - sign",            //43
                        "invalid integer",        //44
                        "integer cannot start with 0",        //45
                        "invalid user-defined name",        //46
                        "short string too long",            //47
                        "invalid character in short string",    //48
                };
        description = contents2;

        String contents3[] =
                {
                        "eq", "and", "or", "not", "add", "sub", "mult", "if",
                        "then", "else", "while", "for", "input", "output", "halt", "proc"
                };
        keywords = contents3;

        //stateTable = new boolean[85];
        //description = new String[85];

    }

    ;

    public int getNextState(int state, char x) {
        //special zero case
        if (state == -1) return 41;

        if (state > 13) return 41;

        int index = getCharIndex(x);
        if (index == -1) return 42;    //invalid character
        return transitionTable[state][getCharIndex(x)];
    }

    public String getDescription(int state) {
        //special zero  case
        if (state == -1) return "integer";
        else return description[state];
    }

    public boolean isAccepting(int state) {
        if (state == -1) return true;
        if (state == 2) return true;
        if (state > 11 && state < 16) return true;
        if (state > 17 && state < 41) return true;
        return false;
    }

    public int isKeyword(String word) {
        //returns -1 if it is not keword
        //else returns the state

        for (int i = 0; i < keywords.length; i++)
            if (word.equals(keywords[i]))
                return i + 25;


        return -1;
    }

    //helper functions
    private int getCharIndex(char x) {
        //if it is a smaller case letter of the alphabet
        if ((int) x > 96 && (int) x < 123)
            return 0;
        //x is zero
        if (x == '0')
            return 1;
        //x is Dpos
        if ((int) x > 48 && (int) x < 58)
            return 2;

        switch (x) {
            case '<':
                return 3;
            case '>':
                return 4;
            case '"':
                return 5;
            case ' ':
                return 6;
            case '\r':
                return 7;
            case '\n':
                return 7;
            case '(':
                return 8;
            case ')':
                return 9;
            case '{':
                return 10;
            case '}':
                return 11;
            case ',':
                return 12;
            case ';':
                return 13;
            case '=':
                return 14;
            case '-':
                return 15;
        }//*/
        return -1;
    }
}
