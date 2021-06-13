package top.yzzblog.compiler.test;

import org.junit.Test;
import top.yzzblog.compiler.Lex.Lexer;

public class LexTest {
    public static void main(String[] args) {
        Lexer lexer = new Lexer(System.in);
        lexer.parse();

    }

    @Test
    public void basic() {
        Lexer lexer = new Lexer("./test.sy");
        lexer.parse();
    }

    @Test
    public void comment() {
        Lexer lexer = new Lexer("./comment.sy");
        lexer.parse();
    }




}
