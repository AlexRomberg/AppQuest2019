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
import android.view.Menu;
import android.view.MenuItem;
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
import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.content.Context.ACTIVITY_SERVICE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MenuItem.OnMenuItemClickListener {

    LinearLayout llContentGroupHolder;
    ArrayList<ArrayList<ImageView>> images = new ArrayList<>();
    ArrayList<ArrayList<TextView>> texts = new ArrayList<>();
    int xView = 0, yView = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llContentGroupHolder = findViewById(R.id.llContentGroupHolder);
        createContentGroup();
    }

    //text of pictures getting prepared
    public String writeText() {
        String textLog = "";
        for (int i = 0; i < texts.size(); i++) {
            int lenth1 = texts.get(i).get(0).getText().length(), lenth2 = texts.get(i).get(1).getText().length();
            if (lenth1 > 0 && lenth2 > 0) {
                String textMemory = (String) texts.get(i).get(0).getText();
                String textMemory2 = (String) texts.get(i).get(1).getText();
                if (i != 0) {
                    textLog += ", ";
                }
                textLog += "[" + textMemory + "," + textMemory2 + "]";
            }
        }
        return textLog;
    }

    private void log() {
        if (images.size() > 1 && writeText().length() > 0) {
            Intent intent = new Intent("ch.appquest.intent.LOG");

            if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
                Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
                return;
            }

            String logmessage = "{\n  \"task\": \"Memory\",\n  \"solution\": [" + writeText() + "]\n}";
            intent.putExtra("ch.appquest.logmessage", logmessage);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Log-Nachricht ist leer", Toast.LENGTH_LONG).show();
        }
    }

    public void takeQrCodePicture() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(false);
        integrator.addExtra(Intents.Scan.BARCODE_IMAGE_ENABLED, true);
        integrator.initiateScan();
    }

    public int[] convertIdToXY(int id) {
        for (int x = 0; x < texts.size(); x++) {
            for (int y = 0; y <= 1; y++) {
                if (id == images.get(x).get(y).getId()) {
                    return new int[]{x, y};
                }
            }
        }
        return new int[]{-1, -1};
    }

    public void createContentGroup() {
        int x = images.size();

        LinearLayout contentGroups = new LinearLayout(this);
        contentGroups.setOrientation(LinearLayout.HORIZONTAL);
        contentGroups.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400));
        ArrayList<ImageView> TempI = new ArrayList<>(2);
        ArrayList<TextView> TempT = new ArrayList<>(2);
        for (int y = 0; y < 2; y++) {
            ImageView ivImage = new ImageView(this);
            ivImage.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            ivImage.setImageResource(R.drawable.plus);
            ivImage.setOnClickListener(this);
            ivImage.setId(x * 100 + y * 10 + 1);
            TempI.add(ivImage);
            TextView tvText = new TextView(this);
            //tvText.setText("TEXT");
            tvText.setLayoutParams(new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT));
            tvText.setId(x * 100 + y * 10 + 0);
            TempT.add(tvText);
        }
        images.add(TempI);
        texts.add(TempT);
        for (int y = 0; y < 2; y++) {
            LinearLayout imageTextGroups = new LinearLayout(this);
            imageTextGroups.setOrientation(LinearLayout.VERTICAL);
            imageTextGroups.setLayoutParams(new ViewGroup.LayoutParams(Resources.getSystem().getDisplayMetrics().widthPixels / 2, 400));
            imageTextGroups.addView(images.get(x).get(y));
            imageTextGroups.addView(texts.get(x).get(y));
            contentGroups.addView(imageTextGroups);
        }
        llContentGroupHolder.addView(contentGroups);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add("Log");
        menuItem.setOnMenuItemClickListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentIntegrator.REQUEST_CODE
                && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            String path = extras.getString(Intents.Scan.RESULT_BARCODE_IMAGE_PATH);

            // Ein Bitmap zur Darstellung erhalten wir so:
            Bitmap bmp = BitmapFactory.decodeFile(path);
            images.get(xView).get(yView).setImageBitmap(bmp);
            images.get(xView).get(yView).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));


            String code = extras.getString(Intents.Scan.RESULT);
            texts.get(xView).get(yView).setText(code);
            //Toast.makeText(getApplicationContext(), code, Toast.LENGTH_LONG).show();

            addImageSpace();
        }
    }

    @Override
    public void onClick(View v) {
        int[] searching = convertIdToXY(v.getId());
        if (searching[0] >= 0) {
            xView = searching[0];
            yView = searching[1];

            //if ImageView is not empty appears a Dialog
            if (texts.get(xView).get(yView).getText().length() > 0) {
                showMessagebox("Überschreiben", "Soll das gewählt Bild überschrieben werde?");
            } else {
                takeQrCodePicture();
            }
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        log();
        return true;
    }

    public void addImageSpace() {
        int empty = 0;
        for (int i = 0; i < texts.size(); i++) {
            int lenth1 = texts.get(i).get(0).getText().length();
            if (lenth1 <= 0) {
                empty++;
            }
        }
        if (empty == 0) {
            createContentGroup();
        }
    }

    public void showMessagebox(String title, String question) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle(title);
        dialog.setMessage(question);

        //If yes
        dialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                takeQrCodePicture();
            }
        });

        //If no
        dialog.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog adAlert = dialog.create();
        adAlert.show();
    }
}
