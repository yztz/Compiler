package top.yzzblog.compiler;

public class Util {

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isLetter(char c) {
        return c <= 'z' && c >= 'a' || c <= 'Z' && c >= 'A';
    }

    public static boolean isHexDigit(char c) {
        return isDigit(c) || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'Z';
    }

    public static boolean isOctDigit(char c) {
        return c >= '0' && c <= '7';
    }

    public static boolean isBinDigit(char c) {
        return c == '0' || c == '1';
    }

    public static boolean isWhite(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }
}
