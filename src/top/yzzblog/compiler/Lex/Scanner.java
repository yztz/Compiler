package top.yzzblog.compiler.Lex;

import top.yzzblog.compiler.util.Util;

import java.io.*;

public class Scanner {
    private BufferedReader br;

    private int lineNum = 1;
    private int colNum = 0;

    private int lastColNum;

    private int lastChar;


    public Scanner(String filepath) {
        try {
            InputStream is = Util.getInputStream(filepath);
            br = new BufferedReader(new InputStreamReader(is));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Scanner(InputStream is) {
        br = new BufferedReader(new InputStreamReader(is));
    }

    public char scan() {
        int ch = -1;
        try {
            ch = br.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lastChar == '\n') {
            lineNum++;
            lastColNum = colNum;
            colNum = 0;
        }

        if (ch == -1) {
            close();
            return '$';
        } else if (ch != '\n') colNum++;

        lastChar = ch;

        return (char) ch;
    }

    public void close() {
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getLineNum() {
        return lineNum - 1;
    }

    public int getColNum() {
        return lastColNum;
    }

}
