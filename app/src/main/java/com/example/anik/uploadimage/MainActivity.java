package com.example.anik.uploadimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    Camera camera;
    Button buttonClickPhoto, buttonUploadPhoto;
    ImageView imageView;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camera = new Camera(MainActivity.this);
        buttonClickPhoto = (Button) findViewById(R.id.buttonClickPhoto);
        buttonUploadPhoto = (Button) findViewById(R.id.buttonUploadPhoto);

        imageView = (ImageView) findViewById(R.id.imageView);
        buttonClickPhoto.setOnClickListener(clickPhotoListener);
        buttonUploadPhoto.setOnClickListener(uploadPhotoListener);
        if(!camera.isAvailable()){
            Toast.makeText(this, "Camera is not supported", Toast.LENGTH_SHORT).show();
            buttonUploadPhoto.setEnabled(false);
            buttonClickPhoto.setEnabled(false);
        }
    }

    View.OnClickListener uploadPhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
            intent.putExtra("filePath", fileUri.getPath());
            startActivity(intent);
        }
    };

    View.OnClickListener clickPhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            captureImage();
        }
    };

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = camera.getOutputMediaFileUri(Camera.MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, Camera.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == Camera.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                //launchUploadActivity(true);
                // bimatp factory
                BitmapFactory.Options options = new BitmapFactory.Options();
                // down sizing image as it throws OutOfMemory Exception for larger
                // images
                options.inSampleSize = 6;
                final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                imageView.setImageBitmap(bitmap);
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),"Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
