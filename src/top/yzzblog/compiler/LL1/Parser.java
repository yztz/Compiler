package top.yzzblog.compiler.LL1;

import top.yzzblog.compiler.Lex.Adapter;
import top.yzzblog.compiler.Lex.Tokenizer;
import top.yzzblog.compiler.grammar.Grammar;

public interface Parser {
    void parse(Tokenizer tokenizer, Adapter adapter);
}
