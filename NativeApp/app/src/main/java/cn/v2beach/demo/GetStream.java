package cn.v2beach.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GetStream {
    public static String InfoFromStream(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();//获取字节数组输出流
        byte[] buffer = new byte[1024];//每次输出1024个字节
        int len = 0;
        try {
            while ((len = in.read(buffer)) != -1) {//输出的和读取的是一样的
                out.write(buffer, 0, len);
            }
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                //在最后关闭流，不要在数据返回时就关闭
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}