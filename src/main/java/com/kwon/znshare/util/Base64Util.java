package com.kwon.znshare.util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;

public class Base64Util {

    /**
     * 将本地文件进行Base64位编码
     *
     * @param filePath
     * @return
     */
    public static String encodeFileToBase64(String filePath) {
        String base64Str = "";
        try {
            InputStream is = new FileInputStream(new File(filePath));
            byte[] bytes = IOUtils.toByteArray(is);
            base64Str = Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base64Str;
    }
}
