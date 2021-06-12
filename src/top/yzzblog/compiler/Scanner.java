package top.yzzblog.compiler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Scanner {
    private BufferedReader br;

    private int lineNum = 1;
    private int colNum = 0;

    private int lastChar;

    public Scanner(String filename) {
        try {
            br = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
            colNum = 0;
        }

        if (ch == -1) {
            close();
            return '$';
        } else if(ch != '\n') colNum++;

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
        return lineNum;
    }

    public int getColNum() {
        return  colNum;
    }
}
