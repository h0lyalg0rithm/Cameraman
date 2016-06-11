package com.surajms.cameraman;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.surajms.cameraman.utils.ImageUtils;

import java.util.ArrayList;

/**
 * Created by surajshirvankar on 6/12/16.
 */
public class ImagePreviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_image_preview);

        Bundle bundle = getIntent().getExtras();
        ArrayList<ImageData> images = bundle.getParcelableArrayList("images");
        final Integer selected = bundle.getInt("selected");

        ImageData imageData = images.get(selected);
        Bitmap bitmap = ImageUtils.fileToBitmap(imageData.getOriginal());

        ImageView image_preview = (ImageView) findViewById(R.id.image_preview);
        ImageButton delete = (ImageButton) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("selected", selected);
                intent.putExtra("delete", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        ImageButton ok = (ImageButton) findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        image_preview.setImageBitmap(bitmap);

    }
}
