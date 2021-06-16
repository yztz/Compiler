package top.yzzblog.compiler.util;

import top.yzzblog.compiler.Lex.Token;

public interface Tokenizer {
    Token getToken();
    int getLineNo();
    int getColNo();
}
