

import org.junit.Test;
import top.yzzblog.compiler.grammar.Grammar;
import top.yzzblog.compiler.grammar.GrammarParseException;

public class GrammarTest {

    @Test
    public void test() {
        try {
            Grammar grammar = Grammar.parse("grammarParse.json");
        }catch (GrammarParseException e) {
            e.printStackTrace();
        }

    }
}
