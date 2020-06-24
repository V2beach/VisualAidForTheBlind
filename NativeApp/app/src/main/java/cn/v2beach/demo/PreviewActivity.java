package cn.v2beach.demo;

import android.net.Uri;
import android.os.Bundle;

import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

/**
 * Created by Jay on 2015/11/22 0022.
 */
public class PreviewActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView img = new ImageView(this);
        String path = getIntent().getStringExtra("path");
        if(path != null){
            img.setImageURI(Uri.fromFile(new File(path)));
        }
        setContentView(img);
    }
}
