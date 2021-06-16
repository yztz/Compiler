

import org.junit.Test;
import top.yzzblog.compiler.Lex.Lexer;

public class LexTest {

    /**
     * 文档案例
     */
    @Test
    public void basic() {
        Lexer lexer = new Lexer("./lex(1)/test.sy");
        lexer.parse();
    }

    /**
     * 注释案例
     */
    @Test
    public void comment() {
        Lexer lexer = new Lexer("./lex(2)/test.sy");
        lexer.parse();
    }

}
