package top.yzzblog.compiler.Lex;

import java.util.HashMap;

public class KeyWords {
    private static final HashMap<String, Tag> keywords = new HashMap<>();

    static {
        keywords.put("int", Tag.KW_INT);
        keywords.put("const", Tag.KW_CONST);
        keywords.put("void", Tag.KW_VOID);
        keywords.put("if", Tag.KW_IF);
        keywords.put("else", Tag.KW_ELSE);
        keywords.put("while", Tag.KW_WHILE);
        keywords.put("continue", Tag.KW_CONTINUE);
        keywords.put("break", Tag.KW_BREAK);
        keywords.put("return", Tag.KW_RETURN);
    }

    public static Tag getTag(String name) {
        return keywords.getOrDefault(name, Tag.ID);
    }
}
