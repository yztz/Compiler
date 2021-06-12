package top.yzzblog.compiler;

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

        public ID(Tag tag, String name) {
            super(tag);
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

        public Num(Tag tag, Integer val) {
            super(tag);
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
