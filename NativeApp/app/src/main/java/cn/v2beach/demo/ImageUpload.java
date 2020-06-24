package cn.v2beach.demo;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.Thread.sleep;

public class ImageUpload{
    private static String str = null;
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    private static final OkHttpClient client = new OkHttpClient();
    public static String conn(File f) throws Exception {
        final File file=f;

//        final AtomicReference<String> atomicReference = new AtomicReference<String>();
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                //子线程需要做的工作
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file","image_1.jpg",
                                RequestBody.create(MEDIA_TYPE_JPG, file))
                        .build();
                //设置为自己的ip地址
                Request request = new Request.Builder()
                        .url("http://222.19.197.230:5000/caption")
                        .post(requestBody)
                        .build();
                try(Response response = client.newCall(request).execute()){
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    str = response.body().string();
                    str = str.substring(13, str.length()-11).replace("', '", " ");
                    System.err.println(str);

//                    atomicReference.set(str);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
//        while(atomicReference.get() == null){
        while(str == null){
            sleep(500);//学艺不精，多线程。
        }
//        System.err.println("HERE!!!!!" + atomicReference.get());
//        return atomicReference.get();
        return str;
    }
}