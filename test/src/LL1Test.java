import org.junit.Before;
import org.junit.Test;
import top.yzzblog.compiler.Lex.Adapter;
import top.yzzblog.compiler.Lex.AdapterFactory;
import top.yzzblog.compiler.LL1.LL1Parser;
import top.yzzblog.compiler.Lex.Lexer;
import top.yzzblog.compiler.Lex.Tokenizer;
import top.yzzblog.compiler.grammar.Grammar;
import top.yzzblog.compiler.grammar.GrammarUtil;
import top.yzzblog.compiler.grammar.Rule;


public class LL1Test {
    private static final String DIR = "./LL1(1)/";
    private Grammar grammar;
    private Adapter adapter;
    private Tokenizer tokenizer;

    @Before
    public void init() {
        this.grammar = Grammar.parse(DIR + "grammar.json");
        this.grammar = GrammarUtil.rmLRecursion(grammar);
        this.grammar = GrammarUtil.extractCommonFactor(grammar);

        System.out.println(grammar);

        this.adapter = AdapterFactory.parse(DIR + "adapter.json");
        this.tokenizer = new Lexer(DIR + "test.sy");
    }

    @Test
    public void parseTest() {
        LL1Parser parser = new LL1Parser(grammar);
        parser.parse(tokenizer, adapter);
    }


    @Test
    public void rmLRecursionTest() {
        Grammar grammar = Grammar.parse("./test.json");
        System.out.println(GrammarUtil.rmLRecursion(grammar));

    }

    @Test
    public void extractCommonTest() {

        Grammar grammar = Grammar.parse("./commonFactor.json");
        System.out.println(GrammarUtil.extractCommonFactor(grammar));

    }

    @Test
    public void tableTest() {
        LL1Parser parser = new LL1Parser(grammar);
        System.out.println(parser);
    }

}
