import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final String LEXER_OUTPUT_FILE = "lexeroutput";
        final String PARSER_OUTPUT_FILE = "ParseTree";
        final String ANALYZER_FILE = "SymbolTables";
        final String PRUNED_PARSER_OUTPUT_FILE = "PrunedParseTree";
        final String BASIC_FILE = "spl.basic";

        if (args.length == 0) {
            System.out.println("Please specify the name of the file you would like to compile.");
            System.exit(1);
        }

        // LEXICAL ANALYSIS
        Lexer lex = new Lexer(args[0]);
        FileWriter fw = new FileWriter(LEXER_OUTPUT_FILE);
        fw.write(lex.toString());
        System.out.println("Lexical Analysis output saved to file '" + LEXER_OUTPUT_FILE + "'");
        fw.close();

        // SYNTACTICAL ANALYSIS
        Parser prsr = new Parser(lex.getTokenList());

        if (prsr.parse()) {
            fw = new FileWriter(PARSER_OUTPUT_FILE);
            fw.write(prsr.toString());
            System.out.println("Parse Tree saved to file '" + PARSER_OUTPUT_FILE + "'");
            fw.close();

            prsr.prune();
            fw = new FileWriter(PRUNED_PARSER_OUTPUT_FILE);
            fw.write(prsr.toString());
            System.out.println("Pruned Parse Tree saved to file '" + PRUNED_PARSER_OUTPUT_FILE + "'");
            fw.close();
        }
        else {
            System.exit(1);
        }

        // SEMANTIC ANALYSIS
        Analyzer analyzer = new Analyzer(prsr.getRoot());
        analyzer.checkTypeAndScope();
        
        //System.out.println(analyzer);
        fw = new FileWriter(ANALYZER_FILE);
        fw.write(analyzer.toString());
        fw.close();

        fw = new FileWriter(PRUNED_PARSER_OUTPUT_FILE);
        fw.write(analyzer.syntaxTree());
        fw.close();

        Codegen cgen = new Codegen(analyzer.getRoot());
        cgen.generateCode();

        fw = new FileWriter(BASIC_FILE);
        fw.write(cgen.toString());
        fw.close();
        System.out.println("Basic intermediate code added to file spl.basic");
        System.out.println(cgen.procIDs);

    }
}
