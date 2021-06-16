import org.junit.Before;
import org.junit.Test;
import top.yzzblog.compiler.grammar.Grammar;
import top.yzzblog.compiler.grammar.GrammarUtil;

public class CommonFactorTest {
    private static final String DIR = "./commonFactor/";
    private Grammar grammar;

    @Before
    public void init() {
        this.grammar = Grammar.parse(DIR + "grammar.json");
    }

    @Test
    public void isIt() {
        System.out.println(GrammarUtil.isCommonFactorExist(grammar));
    }

    @Test
    public void getRidOfIt() {
        System.out.println(GrammarUtil.extractCommonFactor(grammar));
    }
}
