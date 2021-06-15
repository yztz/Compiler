
import org.junit.Before;
import org.junit.Test;
import top.yzzblog.compiler.grammar.Grammar;
import top.yzzblog.compiler.grammar.GrammarUtil;
import top.yzzblog.compiler.grammar.Rule;

public class SetTest {
    private Grammar grammar;

    @Before
    public void init() {

        grammar = Grammar.parse("test1.json");
        grammar = GrammarUtil.rmLRecursion(grammar);
        System.out.println(grammar);
        grammar.compile();

    }

    @Test
    public void firstSetTest() {
        System.out.println(grammar.first("F"));
    }

    @Test
    public void followSetTest() {
        System.out.println(grammar.follow("F"));
    }

    @Test
    public void firstAndFollowTest() {
        System.out.println("\tFirst\tFollow");
        for (String v : grammar.getV()) {
            System.out.println(v + "\t" + grammar.first(v) + "\t" + grammar.follow(v));
        }
    }

    @Test
    public void selectTest() {
        for (Rule rule : grammar.getRules()) {
            System.out.println(rule + " : " + grammar.select(rule));
        }
    }
}
