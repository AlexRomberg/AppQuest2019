package ch.codeclimb.dechiffrierer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.*;

import java.nio.channels.InterruptedByTimeoutException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MenuItem.OnMenuItemClickListener {

    //global objects
    Button btLog;
    EditText tbInput;
    ImageView ivPicture, ivPlaceholder;
    Bitmap bmPicture, bmOriginal;
    TextView tvStatus;
    boolean RedFilter = false;
    int Brightnes = 255;

    static final int REQUEST_IMAGE_CAPTURE = 1, REQUEST_IMAGE_EDIT = 2;

    //is executed when app gets loaded
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializes Views
        btLog = findViewById(R.id.btLog);
        tbInput = findViewById(R.id.tbInput);
        ivPicture = findViewById(R.id.imageView);
        ivPlaceholder = findViewById(R.id.ivPlaceholder);
        tvStatus = findViewById(R.id.tvStatus);
    }

    //is executed when app gets started
    protected void onResume() {
        super.onResume();

        //Adds eventlisteners
        btLog.setOnClickListener(this);
        ivPicture.setOnClickListener(this);
    }

    //Adds optionmenu to app
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add("Neues Bild aufnehmen");
        menuItem.setOnMenuItemClickListener(this);
        return super.onCreateOptionsMenu(menu);
    }


    //Sends string to logbuch-application
    private void log(String qrCode) {
        Intent intent = new Intent("ch.appquest.intent.LOG");

        if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
            Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
            return;
        }

        String logmessage = "{\n  \"task\": \"Dechiffrierer\",\n  \"solution\": \"" + qrCode + "\"\n}";
        intent.putExtra("ch.appquest.logmessage", logmessage);
        startActivity(intent);
    }

    //Is executed when a view gets clicked
    @Override
    public void onClick(View v) {
        if (v.getId() == btLog.getId()) {

            //"log" button pressed
            String logMessage = tbInput.getText().toString();
            log(logMessage);
            tbInput.setText("");

        } else if (v.getId() == ivPicture.getId()) {

            //imageview pressed
            onPictureClicked();

        }
    }

    //Requests an Image from camera-application
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //Is executed when intetnt replies
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //new picture intent
            Bundle extras = data.getExtras();
            bmPicture = applyFilter((Bitmap) extras.get("data"));
            bmOriginal = bmPicture;
            ivPicture.setImageBitmap(bmPicture);
            tvStatus.setText("Auf Bild tippen um es zu bearbeiten.");
            ivPlaceholder.setVisibility(View.INVISIBLE);
            Brightnes = 255;
            RedFilter = false;

        } else if (requestCode == REQUEST_IMAGE_EDIT && resultCode == RESULT_OK) {

            //edit picture intent
            Bundle extras = data.getExtras();
            bmPicture = (Bitmap) extras.get("IMAGE");
            Brightnes = (int) extras.get("BRIGHTNES");
            RedFilter = (boolean) extras.get("REDFILTER");
            ivPicture.setImageBitmap(bmPicture);

        }
    }


    //takes green and blue value out of image
    private Bitmap applyFilter(Bitmap bitmap) {

        //takes image dimensions
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //creates array and fills pixels in it
        int[] data = new int[width * height];
        bitmap.getPixels(data, 0, width, 0, 0, width, height);

        //edits evey pixel
        for (int i = 0; i < data.length; i++) {

            //converts ARGB-value to A, R, G, B 255 values
            int A = (data[i] >> 24) & 0xff;
            int R = (data[i] >> 16) & 0xff;
            int G = R;
            int B = R;

            //converts new 255 values back to ARGB-value
            data[i] = (A & 0xff) << 24 | (R & 0xff) << 16 | (G & 0xff) << 8 | (B & 0xff);
        }
        //creates bitmap
        return Bitmap.createBitmap(data, width, height, Bitmap.Config.ARGB_8888);
    }

    //opens bitmap-edit activity
    public void onPictureClicked() {
        if (bmPicture != null) {
            Intent intent = new Intent(this, editPicture.class);
            intent.putExtra("IMAGE", bmPicture);
            intent.putExtra("ORIGINAL", bmOriginal);
            intent.putExtra("BRIGHTNES", Brightnes);
            intent.putExtra("REDFILTER", RedFilter);
            startActivityForResult(intent, REQUEST_IMAGE_EDIT);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        dispatchTakePictureIntent();
        return true;
    }
}
