package top.yzzblog.compiler.test;

import org.junit.Before;
import org.junit.Test;
import top.yzzblog.compiler.grammar.Grammar;
import top.yzzblog.compiler.grammar.GrammarUtil;

import java.io.IOException;

public class SetTest {
    private Grammar grammar;

    @Before
    public void init() {
        try {
            grammar = Grammar.parse("exp.json");
            grammar = GrammarUtil.rmLRecursion(grammar);
            System.out.println(grammar);
            grammar.compile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void firstSetTest() {
        System.out.println(grammar.first("F"));
    }

    @Test
    public void followSetTest() {
        System.out.println(grammar.follow("F"));
    }
}
