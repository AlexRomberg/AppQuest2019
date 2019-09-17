package ch.codeclimb.dechiffrierer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        log("Text");
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
}
