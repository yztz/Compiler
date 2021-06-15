package top.yzzblog.compiler;

import com.sun.javafx.binding.StringFormatter;
import javafx.scene.control.Tab;

import java.util.logging.SimpleFormatter;

public class Table<T> {
    public static final int defaultWith = 11;
    public static final String ellipsis = "...";
    public static final String gap = "|";

    public static final int LEFT = 0x1 << 1;
    public static final int CENTER = 0x1 << 2;
//    public static final int RIGHT = 0x1 << 3;

    private int cellWidth;
    private String[] colNames;
    private String[] rowNames;
    private T[][] contents;

    private int colNum;
    private int rowNum;


    public Table(int cellWidth, String[] rowNames, String[] colNames, T[][] contents) {
        if (null == contents ||
                null == contents[0] ||
                null != rowNames && rowNames.length != contents.length ||
                null != colNames && colNames.length != contents[0].length
        ) error("非法表格");

        this.cellWidth = cellWidth;

        this.colNames = colNames;
        this.contents = contents;
        this.rowNames = rowNames;

        this.rowNum = contents.length;
        this.colNum = contents[0].length;
    }

    public Table(String[] rowNames, String[] colNames, T[][] contents) {
        this(defaultWith, rowNames, colNames, contents);
    }

    public Table(T[][] contents) {
        this(null, null, contents);
    }

    private String makeCell(Object c, int align) {
        String content = "";

        if (null != c) content = c.toString();


        int length = content.length();
        if (length > cellWidth) {   // 裁剪
            content = content.substring(0, cellWidth - ellipsis.length()).concat(ellipsis);
        }

        if (align == LEFT) {
            return String.format("|%-" + cellWidth + "s", content);
        } else  {
            return center(content);
        }
    }

    private String makeCell(Object content) {
        return makeCell(content, CENTER);
    }

    private String center(String content) {
        StringBuilder sb = new StringBuilder();

        int length = content.length();
        int rest = cellWidth - length;
        int ls,rs;
        if (rest % 2 == 0) {    // 余下宽度为偶数
            ls = rs = rest / 2;
        } else {
            ls = rest / 2;
            rs = ls + 1;
        }
        sb.append('|');
        for (int i = 0; i < ls; i++) sb.append(' ');
        sb.append(content);
        for (int i = 0; i < rs; i++) sb.append(' ');
        sb.append('|');

        return sb.toString();
    }

    private String makeCell() {
        return makeCell("", CENTER);
    }


    private StringBuilder makeRow(T[] row, int align) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < colNum; i++) {
            T content = row[i];
            sb.append(makeCell(content, align));
        }
        return sb;
    }

    private StringBuilder makeRow(T[] row) {
        return makeRow(row, CENTER);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (colNames != null) { // 打印列名
            if(null != rowNames) sb.append(makeCell());
            for (String name : colNames) sb.append(makeCell(name));
            sb.append('\n');
            sb.append(gap());
        }
        if (rowNames != null) { // 带有行名的打印
            for (int i = 0; i < rowNum; i++) {
                sb.append(makeCell(rowNames[i]));
                sb.append(makeRow(contents[i]));
                sb.append('\n');
            }
        } else {
            for (int i = 0; i < rowNum; i++) {
                sb.append(makeRow(contents[i]));
                sb.append('\n');
            }
        }

        return sb.toString();
    }

    public T getCell(int row, int col) {
        return contents[row][col];
    }

    public void setCell(int row, int col, T c) {
        contents[row][col] = c;
    }

    private StringBuilder gap() {
        StringBuilder sb = new StringBuilder();
        int num;
        if (null == colNames)
            num = (cellWidth + gap.length() * 2) * colNum;
        else
            num = (cellWidth + gap.length() * 2) * (colNum + 1);
        for (int i = 0; i < num; i++) sb.append('-');
        return sb.append('\n');
    }

    private void error(String msg) {
        throw new TableMakeException(msg);
    }

    private void error() {
        throw new TableMakeException();
    }

//    private StringBuilder


    static class TableMakeException extends RuntimeException {
        TableMakeException() {
        }

        TableMakeException(String msg) {
            super(msg);
        }
    }
}
