package top.yzzblog.compiler.Lex;

public class Token {
    public final Tag tag;

    public Token(Tag tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tag=" + tag +
                '}';
    }

    static class ID extends Token {
        public final String name;

        public ID(String name) {
            super(Tag.ID);
            this.name = name;
        }

        @Override
        public String toString() {
            return "ID{" +
                    "tag=" + tag +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    static class Num extends Token {
        public final Integer val;

        public Num(Integer val) {
            super(Tag.NUM);
            this.val = val;
        }

        @Override
        public String toString() {
            return "Num{" +
                    "tag=" + tag +
                    ", val=" + val +
                    '}';
        }
    }




}
