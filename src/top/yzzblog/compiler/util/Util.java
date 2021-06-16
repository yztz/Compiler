package top.yzzblog.compiler.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.io.InputStream;

public class Util {
    private final static Logger logger = LoggerFactory.getLogger(Util.class);

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

    public static String arrayToString(String []array) {
        StringBuilder sb = new StringBuilder();
        for (String str : array) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static InputStream getInputStream(String filepath) {
        InputStream is = null;
        try {
            is = Util.class.getClassLoader().getResourceAsStream(filepath);
            if (null == is) throw new IOException();
        } catch (IOException e) {
            logger.error(filepath + "文件IO错误");
            e.printStackTrace();
        }

        return is;
    }

    public static JSONObject readJSONObj(String filepath) {
        String tmp = null;
        try {
            InputStream is = getInputStream(filepath);
            tmp = IOUtils.toString(is);
        } catch (IOException e) {
            logger.error(filepath + "文件IO错误");
            e.printStackTrace();
        }

        return JSONObject.parseObject(tmp);
    }
}
