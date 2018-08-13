package com.kwon.znshare.util;

import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class Util {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(Util.class);

    /**
     * 把对象转变成二进制
     *
     * @param obj 待转换的对象
     * @return 返回二进制数组
     */
    public static byte[] writeInto(Object obj) {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            //读取对象并转换成二进制数据
            oos.writeObject(obj);
            return bos.toByteArray();
        } catch (IOException e) {
            logger.warn("对象转换成二级制数据失败, {}", e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 把二进制数组的数据转回对象
     *
     * @param b
     * @return
     */
    public static Object restore(byte[] b) {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            //读取二进制数据并转换成对象
            bis = new ByteArrayInputStream(b);
            ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            logger.warn("二进制数据转回对象失败, {}", e);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void threadSleep(long min, long max) {
        try {
            Thread.sleep(getRandom(min, max));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static long getRandom(long min, long max) {
        return min + (((long) (new Random().nextDouble() * (max - min))));
    }


    /*方法二：推荐，速度最快
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[0-9]+(.[0-9]+)?$");
        return pattern.matcher(str).matches();
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null) return true;
        else if (obj instanceof CharSequence) return ((CharSequence) obj).length() == 0;
        else if (obj instanceof Collection) return ((Collection) obj).isEmpty();
        else if (obj instanceof Map) return ((Map) obj).isEmpty();
        else if (obj.getClass().isArray()) return Array.getLength(obj) == 0;

        return false;
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

//    public static void main(String[] args) {
//        System.out.println(isInteger("null"));
//    }

}
