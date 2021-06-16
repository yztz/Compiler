package top.yzzblog.compiler.Lex;

import com.alibaba.fastjson.JSONObject;
import top.yzzblog.compiler.util.Adapter;
import top.yzzblog.compiler.util.Util;

import java.util.HashMap;

public class AdapterFactory {

    public static Adapter parse(String filepath) {

        // 读取文件 解析
        JSONObject raw = Util.readJSONObj(filepath);
        HashMap<Tag, String> map = new HashMap<>();

        for (String key : raw.keySet()) {
            Tag tag = Tag.valueOf(key);
            map.put(tag, raw.getString(key));
        }

        return tag -> {
            if (!map.containsKey(tag)) throw new IllegalArgumentException("不支持的标签【" + tag + "】");
            return map.get(tag);
        };
    }

}
