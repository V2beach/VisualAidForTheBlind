package cn.v2beach.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.v2beach.demo.api.TransApi;
import cn.v2beach.demo.utils.AudioRecoderUtils;
import cn.v2beach.demo.utils.PopupWindowFactory;
import cn.v2beach.demo.utils.TimeUtils;
import cz.msebera.android.httpclient.entity.mime.Header;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements /*View.OnClickListener, */TextToSpeech.OnInitListener{

    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20200618000499520";
    private static final String SECURITY_KEY = "72_OLOFdNKEqS1Hr1QIA";

    //是相机还是相册
//    private boolean isCameraOrNot;
    String resultTEXT_temp = " ";
    String recordTEXT_temp = " ";

    //录音转文本
    private String recordPATH;

    //上传
    private String img_filepath;

    private String cameraStr;

    private static final int PHOTO_REQUEST_CODE = 0;
    private Button btnPhoto_1;
    private Button btnPhoto_2;
    private Button btnVoice;
    private Button btnTest;

    //录音1
    static final int VOICE_REQUEST_CODE = 66;
    private Button mButton;
    private ImageView mImageView;
    private TextView mTextView;
    private AudioRecoderUtils mAudioRecoderUtils;
    private Context context;
    private PopupWindowFactory mPop;
    private LinearLayout rl;
    //录音1

    //拍照
    private SurfaceView sfv_preview;
    private Button btn_take;
    private Camera camera = null;
    private SurfaceHolder.Callback cpHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            startPreview();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stopPreview();
        }
    };

    private void sendImage(byte[] bytes)
    {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.PNG, 60, stream);
//        byte[] bytes = stream.toByteArray();
//        String img = new String(Base64.encodeToString(bytes, Base64.DEFAULT));
        //这里改掉了
        String img = new String(bytes);
        String img_show = new String(Arrays.toString(bytes));
        System.err.println(img_show);
        System.err.println(img_show.charAt(1));
//        System.err.println(Arrays.toString(bytes));


        img_show = img_show.substring(0, 9);
        System.err.println(img_show);


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("img", img);


        client.setConnectTimeout(5000000);
        client.setResponseTimeout(5000000);
        client.setTimeout(5000000);


        client.post("http://222.19.197.230:5000/caption", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] repStr) {
                Toast.makeText(MainActivity.this, "Upload Success!", Toast.LENGTH_LONG).show();
                Log.i("log", "======" + Arrays.toString(repStr));
            }
            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] repStr, Throwable throwable) {
                Log.i("log", "======" + Arrays.toString(repStr));

                Toast.makeText(MainActivity.this, "Upload Fail!", Toast.LENGTH_LONG).show();

            }
        });
    }

    public String flaskConn(File imgFile) {//可用？
        URL url = null;
        try {
            url = new URL("http://222.19.197.230:5000/caption");

//            File imgFile=new File(filePath);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data;  boundary=----123456789");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os=new DataOutputStream(conn.getOutputStream());
            StringBuilder body=new StringBuilder();
            body.append("------123456789\r\n");
            body.append("Content-Disposition: form-data; name='img';  filename='"+imgFile.getName()+"'\r\n");
            body.append("Content-Type: image/jpeg\r\n\r\n");
            os.write(body.toString().getBytes());

            InputStream is=new FileInputStream(imgFile);
            byte[] b=new byte[1024];
            int len=0;
            while((len=is.read(b))!=-1){
                os.write(b,0,len);
            }
            String end="\r\n------123456789--";
            os.write(end.getBytes());


//            OutputStream outputStream = conn.getOutputStream();
//            outputStream.write(sendImage(picData).getBytes("UTF-8"));

            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                String result = GetStream.InfoFromStream(in);
//                        JSONObject jo = new JSONObject(result);//将流里的数据用JSON解析
                Log.i("log", "======" + result);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    String picData;

    /**
     * unicode编码转换为汉字
     * @param unicodeStr 待转化的编码
     * @return 返回转化后的汉子
     */
    public static String UnicodeToCN(String unicodeStr) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(unicodeStr);
        char ch;
        while (matcher.find()) {
            //group
            String group = matcher.group(2);
            //ch:'李四'
            ch = (char) Integer.parseInt(group, 16);
            //group1
            String group1 = matcher.group(1);
            unicodeStr = unicodeStr.replace(group1, ch + "");
        }

        return unicodeStr.replace("\\", "").trim();
    }

    private void bindViews() {
        sfv_preview = (SurfaceView) findViewById(R.id.sfv_preview);
        btn_take = (Button) findViewById(R.id.btn_take);
        sfv_preview.getHolder().addCallback(cpHolderCallback);

        btn_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                isCameraOrNot = true;
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        String path = "";

                        picData = new String(data);

                        if ((path = saveFile(data)) != null) {

                            img_filepath = path;

                            Intent it = new Intent(MainActivity.this, PreviewActivity.class);
                            it.putExtra("path", path);
                            startActivity(it);
                            if (textToSpeech != null && !textToSpeech.isSpeaking()) {
                                // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                                textToSpeech.setPitch(0.5f);
                                //设定语速 ，默认1.0正常语速
                                textToSpeech.setSpeechRate(1.5f);
                                //朗读，注意这里三个参数的added in API level 4   四个参数的added in API level 21
//            textToSpeech.speak(speechTxt.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

                                TransApi api = new TransApi(APP_ID, SECURITY_KEY);

                                System.err.println("HERE!!!!!!!"+resultTEXT);

                                String str = null;
                                try {
                                    str = api.getTransResult(resultTEXT, "en", "zh");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                str = str.substring(str.indexOf("dst\":")+6, str.length()-4);
                                str = UnicodeToCN(str);

                                System.err.println("HERE!!!!!!!"+str);

                                Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();

                                textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null);

                            }


                        } else {
                            Toast.makeText(MainActivity.this, "保存照片失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//                isCameraOrNot = false;
            }
        });
    }

    String resultTEXT;//caption response
    String recordTEXT;//录音

    //保存临时文件的方法
    private String saveFile(byte[] bytes){
        try {
            File file = new File(Environment.getExternalStorageDirectory()+"/record/"+TimeUtils.getCurrentTime()+".jpg");
            FileOutputStream fos = new FileOutputStream(file);

//            Log.i("log", "=======" + String.valueOf(bytes));
//            Log.i("log", "=======" + new String(bytes));
//            System.err.println(Arrays.toString(bytes));

//            flaskConn(file);

            fos.write(bytes);
            fos.flush();
            fos.close();

            //顺序错了，还没写入文件就上传了，怪不得主线程等待子线程老是报错
            try {

                //这里出现的BUG，就是第一次给祁洪宇发视频的那个bug，是个线程bug，用最暴力的办法解决了。
                //反思一下。

                //recordTEXT作用是什么来着？
                resultTEXT = ImageUpload.conn(file);
                while (resultTEXT.equals(resultTEXT_temp)){//还相同？就重新上传一次
                    sleep(1000);
                    resultTEXT = ImageUpload.conn(file);
                }
                resultTEXT_temp = resultTEXT;//不让子线程(返回服务器结果)超过主线程(念出来)
            } catch (Exception e) {
                e.printStackTrace();
            }

            cameraStr = file.getAbsolutePath();

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    //开始预览
    private void startPreview(){
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(sfv_preview.getHolder());
            camera.setDisplayOrientation(90);   //让相机旋转90度
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //停止预览
    private void stopPreview() {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    /** 获取权限*/
    private void getPermission() {
        if (Build.VERSION.SDK_INT>22){
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }else {
                //说明已经获取到摄像头权限了
                Log.i("MainActivity","已经获取了权限");
            }
        }else {
//这个说明系统版本在6.0之下，不需要动态获取权限。
            Log.i("MainActivity","系统版本在6.0之下，不需要动态获取权限。");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //先解决了再说，只加了这一点代码
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //拍照
        getPermission();
        bindViews();


        btnPhoto_2 = (Button) findViewById(R.id.button_5);
        btnPhoto_2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PHOTO_REQUEST_CODE);
            }
        });

        //录音2
        context = this;

        rl = (LinearLayout) findViewById(R.id.ll_1);

        mButton = (Button) findViewById(R.id.button_record);

        //PopupWindow的布局文件
        final View view = View.inflate(this, R.layout.layout_microphone, null);

        mPop = new PopupWindowFactory(this,view);

        //PopupWindow布局文件里面的控件
        mImageView = (ImageView) view.findViewById(R.id.iv_recording_icon);
        mTextView = (TextView) view.findViewById(R.id.tv_recording_time);

        mAudioRecoderUtils = new AudioRecoderUtils();

        //录音回调
        mAudioRecoderUtils.setOnAudioStatusUpdateListener(new AudioRecoderUtils.OnAudioStatusUpdateListener() {

            //录音中....db为声音分贝，time为录音时长
            @Override
            public void onUpdate(double db, long time) {
                mImageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
                mTextView.setText(TimeUtils.long2String(time));
            }

            //录音结束，filePath为保存路径
            @Override
            public void onStop(String filePath) throws InterruptedException {
                Toast.makeText(MainActivity.this, recordTEXT, Toast.LENGTH_SHORT).show();//"录音保存在：" + filePath
                recordPATH = filePath;
                mTextView.setText(TimeUtils.long2String(0));

                if (textToSpeech != null && !textToSpeech.isSpeaking()) {
                    // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                    textToSpeech.setPitch(0.5f);
                    //设定语速 ，默认1.0正常语速
                    textToSpeech.setSpeechRate(1.5f);
                    //朗读，注意这里三个参数的added in API level 4   四个参数的added in API level 21
//            textToSpeech.speak(speechTxt.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

                    sleep(3000);
                    Toast.makeText(MainActivity.this, recordTEXT, Toast.LENGTH_LONG).show();

                    textToSpeech.speak(recordTEXT, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });


        //6.0以上需要权限申请
        requestPermissions();
        //录音2


        //测试
//        btnTest = (Button) findViewById(R.id.button_6);
//        btnTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showToast(v);
//            }
//        });

        //语音
//        speechBtn = (Button) findViewById(R.id.button_6);
//        speechBtn.setOnClickListener(this);
//        speechTxt = (EditText) findViewById(R.id.editText);
        textToSpeech = new TextToSpeech(this, this); // 参数Context,TextToSpeech.OnInitListener

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PHOTO_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    //通过uri的方式返回，部分手机uri可能为空

//                    ImageView imageView = (ImageView) findViewById(R.id.image_view);

//                    if(uri != null){
                    try {
                        //通过uri获取到bitmap对象
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                            imageView.setImageBitmap(bitmap);

                        File file = new File(Environment.getExternalStorageDirectory()+"/record/"+TimeUtils.getCurrentTime()+".jpg");
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        bos.flush();
                        bos.close();

                        try {
                            resultTEXT = ImageUpload.conn(file);
                            while (resultTEXT.equals(resultTEXT_temp)){//还相同？就重新上传一次
                                sleep(1000);
                                resultTEXT = ImageUpload.conn(file);
                            }
                            resultTEXT_temp = resultTEXT;//不让子线程(返回服务器结果)超过主线程(念出来)


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (textToSpeech != null && !textToSpeech.isSpeaking()) {
                            // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                            textToSpeech.setPitch(0.5f);
                            //设定语速 ，默认1.0正常语速
                            textToSpeech.setSpeechRate(1.5f);
                            //朗读，注意这里三个参数的added in API level 4   四个参数的added in API level 21
//            textToSpeech.speak(speechTxt.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

                            TransApi api = new TransApi(APP_ID, SECURITY_KEY);

                    System.err.println("HERE!!!!!!!"+resultTEXT);

                            String str = null;
                            try {
                                str = api.getTransResult(resultTEXT, "en", "zh");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            str = str.substring(str.indexOf("dst\":")+6, str.length()-4);
                            str = UnicodeToCN(str);

                    System.err.println("HERE!!!!!!!"+str);

                            Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();

                            textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null);

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    }else {
//                        //部分手机可能直接存放在bundle中
//                        Bundle bundleExtras = data.getExtras();
//                        if(bundleExtras != null){
//                            Bitmap  bitmaps = bundleExtras.getParcelable("data");
//                            imageView.setImageBitmap(bitmaps);
//                        }
//                    }

                }
                break;
        }
    }

    public void showToast(View view){

        Toast.makeText(this, cameraStr, Toast.LENGTH_SHORT).show();
    }

    //录音3
    /**
     * 开启扫描之前判断权限是否打开
     */
    private void requestPermissions() {
        //判断是否开启摄像头权限
        if ((ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
        ) {
            StartListener();

            //判断是否开启语音权限
        } else {
            //请求获取摄像头权限
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, VOICE_REQUEST_CODE);
        }

    }

    /**
     * 请求权限回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == VOICE_REQUEST_CODE) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED) ) {
                StartListener();
            } else {
                Toast.makeText(context, "已拒绝权限！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void StartListener(){
        //Button的touch监听
        mButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:

                        mPop.showAtLocation(rl, Gravity.CENTER, 0, 0);

                        mButton.setText("松开保存");
                        mAudioRecoderUtils.startRecord();


                        break;

                    case MotionEvent.ACTION_UP:

                        mAudioRecoderUtils.stopRecord();        //结束录音（保存录音文件）
//                        mAudioRecoderUtils.cancelRecord();    //取消录音（不保存录音文件）
                        mPop.dismiss();
                        mButton.setText("按住说话");

//                        AmrToText amrToText = new AmrToText(recordPATH);
//                        recordTEXT = amrToText.DEMO();
                        //笔记：Program type already present: com.cloud.apigateway.sdk.utils.AccessService
                        //报错之后，clean项目重新build就好了，查不到，自己试出来发现的。

                        break;
                }
                return true;
            }
        });
    }

    //录音3

    private Button speechBtn; // 按钮控制开始朗读
    private EditText speechTxt; // 需要朗读的内容
    private TextToSpeech textToSpeech; // TTS对象

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.CHINA);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "数据丢失或不支持", Toast.LENGTH_SHORT).show();
            }
        }
    }

}