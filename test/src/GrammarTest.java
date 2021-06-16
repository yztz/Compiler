

import org.junit.Test;
import top.yzzblog.compiler.grammar.Grammar;
import top.yzzblog.compiler.grammar.GrammarParseException;

public class GrammarTest {
    private static final String DIR = "./grammar(2)/";

    @Test
    public void test() {
        try {
            Grammar grammar = Grammar.parse(DIR + "grammar.json");
            System.out.println(grammar);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
