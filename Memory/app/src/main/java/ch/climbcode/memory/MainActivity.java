package ch.climbcode.memory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.LineNumberReader;

import static android.content.Context.ACTIVITY_SERVICE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout llContentGroupHolder;
    LinearLayout contentGroups[] = new LinearLayout[1];
    LinearLayout imageTextGroups[][] = new LinearLayout[1][2];
    ImageView images[][] = new ImageView[1][2];
    TextView texts[][] = new TextView[1][2];
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llContentGroupHolder = findViewById(R.id.llContentGroupHolder);
        createContentGroup();
    }

    //text of pictures getting prepared
    public String writeText(){
        String textLog = "";
        for (int i = 0; i < texts.length; i++) {
                if (texts[i][0].getTextSize() > 0 || texts[i][1].getTextSize() > 0) {
                   String textMemory = (String)texts[i][0].getText();
                   String textMemory2 = (String)texts[i][1].getText();
                   if(i != 0) {
                       textLog += "[" + textMemory + "," + textMemory2 + "]";
                   }
                    textLog += ", [" + textMemory + "," + textMemory2 + "]";
                }
        } return textLog;
    }

    //log message sent
    private void log(String textMemory) {
        Intent intent = new Intent("ch.appquest.intent.LOG");

        if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
            Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
            return;
        }

        String logmessage = "{\n  \"task\": \"Memory\",\n  \"solution\": ["+writeText()+"]\n}";
        intent.putExtra("ch.appquest.logmessage", logmessage);
        startActivity(intent);

    }
    public void takeQrCodePicture() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        //integrator.setCaptureActivity(MyCaptureActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(false);
        integrator.addExtra(Intents.Scan.BARCODE_IMAGE_ENABLED, true);
        integrator.initiateScan();
    }

    public int [] convertIdToXY (int id){
        for (int x = 0; x < texts.length; x++) {
            for (int y = 0; y <= 1; y++) {
                if (id == images[x][y].getId()){
                    return new int []{x,y};
                }
            }
        }
    }
    public void createContentGroup() {

        contentGroups[0]= new LinearLayout(this);
        contentGroups[0].setOrientation(LinearLayout.HORIZONTAL);
        contentGroups[0].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400));
        for (int i = 0; i < 2; i++) {
            ImageView ivImage = new ImageView(this);
            ivImage.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            ivImage.setImageResource(R.drawable.plus);
            ivImage.setOnClickListener(this);
            images[0][i] = ivImage;
            TextView tvText = new TextView(this);
            tvText.setText("TEXT");
            tvText.setLayoutParams(new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT));
            texts[0][i] = tvText;
            imageTextGroups[0][i] = new LinearLayout(this);
            imageTextGroups[0][i].setOrientation(LinearLayout.VERTICAL);
            imageTextGroups[0][i].setLayoutParams(new ViewGroup.LayoutParams(Resources.getSystem().getDisplayMetrics().widthPixels/2, 400));
            imageTextGroups[0][i].addView(images[0][i]);
            imageTextGroups[0][i].addView(texts[0][i]);
            contentGroups[0].addView(imageTextGroups[0][i]);
        }
        llContentGroupHolder.addView(contentGroups[0]);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentIntegrator.REQUEST_CODE
                && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            String path = extras.getString(Intents.Scan.RESULT_BARCODE_IMAGE_PATH);

            // Ein Bitmap zur Darstellung erhalten wir so:
            Bitmap bmp = BitmapFactory.decodeFile(path);
            ImageView iv = findViewById(id);
            iv.setImageBitmap(bmp);

            String code = extras.getString(Intents.Scan.RESULT);
            Toast.makeText(getApplicationContext(), code, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        takeQrCodePicture();
        id = v.getId();
    }
}
