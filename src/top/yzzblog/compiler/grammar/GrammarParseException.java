package top.yzzblog.compiler.grammar;

public class GrammarParseException extends RuntimeException{
    public GrammarParseException(String msg) {
        super(msg);
    }

    public GrammarParseException(){}
}
