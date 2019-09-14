package ch.codeclimb.metalldetektor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {
    SensorManager sensorManager;
    Sensor magnetSensor;
    ProgressBar pbStaerke;
    TextView tvError;
    Button btScanQR, btCalibrate;

    static final int SCAN_QR_CODE_REQUEST_CODE = 1;

    //Will be started when programm gets created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialises components
        pbStaerke = findViewById(R.id.pbStaerke);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magnetSensor = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED).get(0);
        tvError = findViewById(R.id.tvErrorMessage);
        btCalibrate = findViewById(R.id.btCalibrate);
        btScanQR = findViewById(R.id.btScanQR);
    }

    //Will be started when programm gets loaded
    protected void onResume() {
        super.onResume();

        //Sets pbStärke Max
        pbStaerke.setMax((int) magnetSensor.getMaximumRange());

        //Adds eventlisteners
        sensorManager.registerListener(this, magnetSensor, SensorManager.SENSOR_DELAY_NORMAL);
        btCalibrate.setOnClickListener(this);
        btScanQR.setOnClickListener(this);
    }

    //refreshes progressbar if sensor changes
    public void onSensorChanged(SensorEvent event) {
        float[] mag = event.values;
        double value = Math.sqrt(mag[0] * mag[0] + mag[1] * mag[1] + mag[2] * mag[2]);
        pbStaerke.setProgress((int) Math.round(value));
    }

    //no function
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //sends QR-Code app scan command
    public void scanQRCode() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, SCAN_QR_CODE_REQUEST_CODE);

    }

    //receives QR-code app response
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SCAN_QR_CODE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String logMsg = intent.getStringExtra("SCAN_RESULT");
                log(logMsg);
            }
        }
    }

    //Sends string to logbuch application
    private void log(String qrCode) {
        Intent intent = new Intent("ch.appquest.intent.LOG");

        if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
            Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
            return;
        }

        String logmessage = "{\n  \"task\": \"Metalldetektor\",\n  \"solution\": \"" + qrCode + "\"\n}";
        intent.putExtra("ch.appquest.logmessage", logmessage);
        startActivity(intent);
    }

    //Will be started when button is pressed
    @Override
    public void onClick(View v) {
        if (v.getId() == btCalibrate.getId()){
            //If btCalibrate is pressed
            pbStaerke.setMin(pbStaerke.getProgress());
        } else if (v.getId() == btScanQR.getId()) {
            //If btScanQR is pressed
            scanQRCode();
        }
    }
}
