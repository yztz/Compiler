package top.yzzblog.compiler;

import java.util.HashMap;

public class KeyWords {
    private HashMap<String, Tag> keywords;

    public KeyWords() {

    }

    public Tag getTag(String name) {
        return keywords.getOrDefault(name, null);
    }
}
