package top.yzzblog.compiler;

public class Token {
    public final Tag tag;

    public Token(Tag tag) {
        this.tag = tag;
    }

    static class ID extends Token {
        public ID(Tag tag) {
            super(tag);
        }
    }
}
