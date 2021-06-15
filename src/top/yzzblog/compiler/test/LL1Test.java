package top.yzzblog.compiler.test;

import org.junit.Before;
import org.junit.Test;
import top.yzzblog.compiler.LL1.LL1Parser;
import top.yzzblog.compiler.grammar.Grammar;
import top.yzzblog.compiler.grammar.GrammarUtil;
import top.yzzblog.compiler.grammar.Rule;


import java.io.IOException;

public class LL1Test {
    private Grammar grammar;

    @Before
    public void init() {
        try {
            this.grammar = Grammar.parse("test1.json");
            this.grammar = GrammarUtil.rmLRecursion(grammar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readTest() {
        try {
            System.out.println(Grammar.parse("./test.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void patternTest() {
//        Rule rule = new Rule();
//        rule.setR("ab$$dc$");
        Rule rule = new Rule("a$::a");
        System.out.println(rule);
    }

    @Test
    public void rmLRecursionTest() {
        try {
            Grammar grammar = Grammar.parse("./test.json");
            System.out.println(GrammarUtil.rmLRecursion(grammar));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void extractCommonTest() {
        try {
            Grammar grammar = Grammar.parse("./commonFactor.json");
            System.out.println(GrammarUtil.extractCommonFactor(grammar));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tableTest() {
        LL1Parser parser = new LL1Parser(grammar, null);
        System.out.println(parser);
    }
}
