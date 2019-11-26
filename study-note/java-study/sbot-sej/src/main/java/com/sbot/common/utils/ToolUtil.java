package com.sbot.common.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *
 * @author jintingying
 * @version 1.0
 * @date 2019/11/26
 */
public class ToolUtil {

    public static boolean isNotEmpty(Object object) {
        boolean ret = false;
        if (object != null) {
            if (object instanceof String) {
                ret = ((String) object).length() > 0 ? true : false;
            } else if (object instanceof Map) {
                ret = ((Map) object).size() > 0 ? true : false;
            } else if (object instanceof List) {
                ret = !((List) object).isEmpty() ? true : false;
            }
        }
        return ret;
    }

    public static boolean isEmpty(Object object) {
        return !isNotEmpty(object);
    }


    public static String randomID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String randomID35() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-");
        String id = UUID.randomUUID().toString();
        String date = sdf.format(new Date());
        return date + id.substring(9, 13) + id.substring(14, 18)
                + id.substring(19, 23) + id.substring(24);
    }

    /**
     * 获取文件后缀名  eg: '.txt'
     */
    public static String getFileSuffix(String fileName) {
        if (!fileName.isEmpty()) {
            int lastIndexOf = fileName.lastIndexOf(".");
            return fileName.substring(lastIndexOf);
        }
        return "";
    }

    /**
     * 首字母转小写
     */
    public static String lowerFirst(String word) {
        if (Character.isLowerCase(word.charAt(0))) {
            return word;
        } else {
            return String.valueOf(Character.toLowerCase(word.charAt(0))) + word.substring(1);
        }
    }

    /**
     * 首字母转大写
     */
    public static String upperFirst(String word) {
        if (Character.isUpperCase(word.charAt(0))) {
            return word;
        } else {
            return String.valueOf(Character.toUpperCase(word.charAt(0))) + word.substring(1);
        }
    }

    /**
     * 获取对象所有字段（包括继承）
     * @param o
     * @return
     */
    public static Field[] getAllField(Object o) {
        Class<?> clazz = o.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    public static <T> List<Object> list2Object(Object s, List<T> list) {
        List<Object> newList = new ArrayList<>();
        Class<?> clazz1 = s.getClass();
        Field[] fields1 = clazz1.getDeclaredFields();
        if (null != list && !list.isEmpty()) {
            for (T data : list) {
                Object object = null;
                Class<?> clazz = data.getClass();
                Field[] fields2 = clazz.getDeclaredFields();
                try {
                    object = clazz1.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < fields1.length; i++) {
                    Field field1 = fields1[i];
                    for (int j = 0; j < fields2.length; j++) {
                        Field field2 = fields2[j];
                        String f1 = field1.getName();
                        String f2 = field2.getName();
                        if (f1.equals(f2)) {
                            try {
                                field1.setAccessible(true);
                                field2.setAccessible(true);
                                field1.set(object, field2.get(data));
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }
                newList.add(object);
            }
        }
        return newList;
    }


}
