import org.junit.Before;
import org.junit.Test;
import top.yzzblog.compiler.grammar.Grammar;
import top.yzzblog.compiler.grammar.GrammarUtil;

public class GrammarUtilTest {
    private Grammar grammar;
    @Before
    public void init() {
            this.grammar = Grammar.parse("LL1.json");
    }

    @Test
    public void isLRecursionExistTest() {
        System.out.println(GrammarUtil.isLRecursionExist(grammar));
    }

    @Test
    public void isCommonFactorExistTest() {
        System.out.println(GrammarUtil.isCommonFactorExist(grammar));
    }

    @Test
    public void isLL1Test() {
        System.out.println(grammar);
        GrammarUtil.isLL1(grammar);
    }
}
