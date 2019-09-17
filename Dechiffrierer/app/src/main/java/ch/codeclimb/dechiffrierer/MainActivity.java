package ch.codeclimb.dechiffrierer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btLog;
    EditText tbInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btLog = findViewById(R.id.btLog);
        tbInput = findViewById(R.id.tbInput);

    }

    protected void onResume() {
        super.onResume();

        //Adds eventlisteners
        btLog.setOnClickListener(this);
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
