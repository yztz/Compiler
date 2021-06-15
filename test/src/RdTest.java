
import org.junit.Before;
import org.junit.Test;
import top.yzzblog.compiler.Lex.Lexer;
import top.yzzblog.compiler.Lex.Tokenizer;
import top.yzzblog.compiler.recursiveDescent.RdParser;

public class RdTest {
    private Tokenizer tokenizer;

    @Before
    public void init() {
        tokenizer = new Lexer("exp.sy");
    }

    @Test
    public void rdTest() {
        RdParser parser = new RdParser(tokenizer);
        parser.analyze(true);
    }
}
