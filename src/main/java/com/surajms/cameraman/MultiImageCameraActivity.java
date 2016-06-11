package com.surajms.cameraman;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.surajms.cameraman.utils.ImageUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by surajshirvankar on 6/11/16.
 */
public class MultiImageCameraActivity extends AppCompatActivity {
    private static String TAG = MultiImageCameraActivity.class.toString();

    Camera camera;

    CameraPreview cameraPreview;
    FrameLayout preview;
    ImagePreviewAdapter imagePreviewAdapter;
    ImageView captureButton;
    RecyclerView previewRecyclerview;
    Button done;

    ArrayList<ImageData> images = new ArrayList<>();

    public static final int MEDIA_TYPE_IMAGE = 1;
    private int PREVIEW = 100;
    private ImageView flash;
    private ImageView flipCamera;
    private int maxImages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_image_camera);

        preview = (FrameLayout) findViewById(R.id.camera_preview);
        captureButton = (ImageView) findViewById(R.id.button_capture);
        flash = (ImageButton) findViewById(R.id.flash);
        flipCamera = (ImageButton) findViewById(R.id.flip_camera);
        previewRecyclerview = (RecyclerView) findViewById(R.id.preview_recyclerview);
        done = (Button) findViewById(R.id.done);

        maxImages = getIntent().getIntExtra("maxImages", 5);


        if (!hasCamera(getApplicationContext())) {
            Toast.makeText(MultiImageCameraActivity.this, "No Camera on the device", Toast.LENGTH_SHORT).show();
            finish();
        }

        camera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        cameraPreview = new CameraPreview(this, camera, preview);
        cameraPreview.setBackgroundColor(TRIM_MEMORY_BACKGROUND);
        preview.addView(cameraPreview);
        cameraPreview.startCameraPreview();


        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, mPicture);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("images", images);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flashMode = camera.getParameters().getFlashMode();
                Camera.Parameters parameters = camera.getParameters();
                switch (flashMode) {
                    case Camera.Parameters.FLASH_MODE_ON:
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                        flash.setImageResource(R.drawable.ic_flash_auto_white_24dp);
                        break;
                    case Camera.Parameters.FLASH_MODE_OFF:
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        flash.setImageResource(R.drawable.ic_flash_on_white_24dp);
                        break;
                    case Camera.Parameters.FLASH_MODE_AUTO:
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        flash.setImageResource(R.drawable.ic_flash_off_white_24dp);
                        break;
                }
                camera.setParameters(parameters);
            }
        });

        flipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ClickGuard.guard(captureButton);
        imagePreviewAdapter = new ImagePreviewAdapter(this, images);
        imagePreviewAdapter.setMaxImageCount(maxImages);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        previewRecyclerview.setAdapter(imagePreviewAdapter);
        previewRecyclerview.setLayoutManager(linearLayoutManager);
        imagePreviewAdapter.setListener(new ImagePreviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ImageData item) {
                if (images.size() > position) {
                    Intent intent = new Intent(MultiImageCameraActivity.this, ImagePreviewActivity.class);
                    intent.putExtra("images", images);
                    intent.putExtra("selected", position);
                    startActivityForResult(intent, PREVIEW);
                }

                if (images.size() < maxImages)
                    captureButton.setVisibility(View.VISIBLE);
            }
        });

    }


    private boolean hasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            Log.d(TAG, "Couldnt open camera");
        }
        return c; // returns null if camera is unavailable
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File originalFile = ImageUtils.createImageFile("Test");
            File thumbnailFile = ImageUtils.createTempImageFile(getApplicationContext());

            if (originalFile == null) {
                Log.d(TAG, "Error creating original media file, check storage permissions: ");
                return;
            }

            if (thumbnailFile == null) {
                Log.d(TAG, "Error creating thumbnail media file, check storage permissions: ");
                return;
            }

            Bitmap original = ImageUtils.bytesToBitmap(data);
            ImageUtils.writeBytesToFile(originalFile, data);
            ImageUtils.broadcastFile(getApplicationContext(), originalFile);

            Bitmap resized = ImageUtils.scaleImage(original, 0.3);
            ImageUtils.writeImageToFile(resized, thumbnailFile);
            ImageUtils.broadcastFile(getApplicationContext(), thumbnailFile);


            updateImagesArrayList(originalFile, thumbnailFile);
            cameraPreview.startCameraPreview();
        }

    };

    private void updateImagesArrayList(File originalFile, File thumbnailFile) {
        ImageData imageData = new ImageData(originalFile.getAbsolutePath(), thumbnailFile.getAbsolutePath());
        images.add(imageData);
        imagePreviewAdapter.notifyDataSetChanged();
        if (images.size() > 4)
            captureButton.setVisibility(View.GONE);
    }

    private void releaseCameraAndPreview() {

        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if (cameraPreview != null) {
            cameraPreview.destroyDrawingCache();
            cameraPreview.mCamera = null;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseCameraAndPreview();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PREVIEW) {
                int selected = data.getIntExtra("selected", 0);
                Boolean delete = data.getBooleanExtra("delete", false);
                if (delete)
                    imagePreviewAdapter.deleteImageData(selected, images);
                if (images.size() < maxImages)
                    captureButton.setVisibility(View.VISIBLE);
            }
        }
    }
}