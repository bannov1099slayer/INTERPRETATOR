import lb.Variables;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;



public class main {
    public static void main(String[] args) throws IOException {
        final String input = new String( Files.readAllBytes(Paths.get("program.txt")), "UTF-8");
        final List<Token> tokens = new Lexer(input).tokenize();
        for (Token token : tokens){
            System.out.println(token);
        }
        final Statement program = new Parser(tokens).parse();
        System.out.println(program.toString());
        program.execute();

    }
}