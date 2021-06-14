package top.yzzblog.compiler.LL1;

import top.yzzblog.compiler.Lex.Tokenizer;
import top.yzzblog.compiler.grammar.Grammar;

public class LL1Parser {
    private Grammar grammar;
    private Tokenizer tokenizer;

    public LL1Parser(Grammar grammar, Tokenizer tokenizer) {
        this.grammar = grammar;
        this.tokenizer = tokenizer;
    }



}
