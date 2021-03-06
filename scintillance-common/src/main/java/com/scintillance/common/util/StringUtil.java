package com.scintillance.common.util;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/21 0021.
 */
public class StringUtil extends StringUtils {

    /**
     * 将类名或属性名根据驼峰命名法分割成单词组
     * @param name
     * @return
     */
    public static List<String> splitNameAccordingCamelCase(String name) {
        Assert.notHasText(name,"name必须包含非空白字符");

        String separatorUnderline = "_";
        String[] splitByUnderline = name.split(separatorUnderline);

        List<String> words = new ArrayList<>();

        for(String part : splitByUnderline) {
            StringBuffer stringBuffer = new StringBuffer();
            for(int i=0;i<part.toCharArray().length;i++) {
                char ch = part.charAt(i);
                if(Character.isUpperCase(ch)) {
                    if(stringBuffer.length()!=0) {
                        words.add(stringBuffer.toString());
                        stringBuffer.setLength(0);
                    }
                }
                stringBuffer.append(Character.toLowerCase(ch));
            }
            words.add(stringBuffer.toString());
        }
        return words;
    }

    /**
     * 首字母转小写
     * @param src
     * @return
     */
    public static String lowerCaseInitial(String src) {
        if(!hasText(src)) return src;

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(src.substring(0,1).toLowerCase());
        stringBuffer.append(src.substring(1,src.length()));

        return stringBuffer.toString();
    }

    /**
     * 首字母转大写
     * @param src
     * @return
     */
    public static String upCaseInitial(String src) {
        if(!hasText(src)) return src;

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(src.substring(0,1).toUpperCase());
        stringBuffer.append(src.substring(1,src.length()));

        return stringBuffer.toString();
    }

    public static boolean equals(String one, String another) {
        if(one != null) {
            return one.equals(another);
        }else if(another != null) {
            return another.equals(one);
        }else {
            return true;
        }
    }
}
