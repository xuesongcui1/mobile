package com.hoomsun.mobile.tools;

import sun.misc.BASE64Decoder;

import java.io.FileOutputStream;
import java.io.IOException;

public class Tools {
    public static boolean generateImage(String imgStr, String path) throws Exception {
        if(imgStr==null || imgStr.equals("")){
            return false;
        }
        FileOutputStream fos = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i <b.length ; i++) {
                if(b[i] < 0){
                    b[i] += 256;
                }
            }
            fos = new FileOutputStream(path);
            fos.write(b);
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos!=null){
                fos.flush();
                fos.close();
            }
        }
        return true;
    }
}
