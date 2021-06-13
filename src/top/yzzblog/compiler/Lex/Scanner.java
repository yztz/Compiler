package top.yzzblog.compiler.Lex;

import java.io.*;

public class Scanner {
    private BufferedReader br;

    private int lineNum = 1;
    private int colNum = 0;

    private int lastChar;

    private File file;

    public Scanner(String filename) {
        file = new File(filename);

        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
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
        return lineNum;
    }

    public int getColNum() {
        return colNum;
    }

    public String getFile() {
        return file.getAbsolutePath();
    }
}
