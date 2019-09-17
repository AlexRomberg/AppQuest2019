package ch.codeclimb.dechiffrierer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import android.widget.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btLog, btNewPicture;
    EditText tbInput;
    ImageView ivPicture;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btLog = findViewById(R.id.btLog);
        btNewPicture = findViewById(R.id.btNewPicture);
        tbInput = findViewById(R.id.tbInput);
        ivPicture = findViewById(R.id.imageView);
    }

    protected void onResume() {
        super.onResume();

        //Adds eventlisteners
        btLog.setOnClickListener(this);
        btNewPicture.setOnClickListener(this);
    }

    //Sends string to logbuch application
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

    @Override
    public void onClick(View v) {
        if (v.getId() == btLog.getId()) {
            String logMessage = tbInput.getText().toString();
            log(logMessage);
            tbInput.setText("");
        } else if (v.getId() == btNewPicture.getId()) {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivPicture.setImageBitmap(imageBitmap);
        }
    }

    private Bitmap applyFilter(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] data = new int[width * height];

        bitmap.getPixels(data, 0, width, 0, 0, width, height);

        // Hier können die Pixel im data-array bearbeitet und
        // anschliessend damit ein neues Bitmap erstellt werden

        for (int i = 0; i < data.length; i ++){
            data[i] = data[i];
            tbInput.setText(""+data);
        }


        return Bitmap.createBitmap(data, width, height, Bitmap.Config.ARGB_8888);
    }
}
