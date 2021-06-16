package top.yzzblog.compiler.util;

public interface Parser {
    void parse(Tokenizer tokenizer, Adapter adapter);
}
