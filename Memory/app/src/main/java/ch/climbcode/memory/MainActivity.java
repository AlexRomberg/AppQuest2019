package ch.climbcode.memory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.LineNumberReader;

import static android.content.Context.ACTIVITY_SERVICE;

public class MainActivity extends AppCompatActivity {

    LinearLayout llContentGroupHolder;
    LinearLayout contentGroups[] = new LinearLayout[1];
    LinearLayout imageTextGroups[][] = new LinearLayout[1][2];
    ImageView images[][] = new ImageView[1][2];
    TextView texts[][] = new TextView[1][2];

    //is started when app gets created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llContentGroupHolder = findViewById(R.id.llContentGroupHolder);
        createContentGroup();
    }

    public void takeQrCodePicture() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        //integrator.setCaptureActivity(MyCaptureActivity.class);
        //integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setOrientationLocked(false);
        integrator.addExtra(Intents.Scan.BARCODE_IMAGE_ENABLED, true);
        integrator.initiateScan();
    }

    public void createContentGroup() {
        for (int i = 0; i < 2; i++) {
            ImageView ivImage = new ImageView(this);
            ivImage.setLayoutParams(new LinearLayout.LayoutParams(Resources.getSystem().getDisplayMetrics().widthPixels/2, ViewGroup.LayoutParams.MATCH_PARENT));
            ivImage.setImageResource(R.drawable.plus);
            images[0][i] = ivImage;
            contentGroups[0].addView(images[0][i]);
        }
        contentGroups[0]= new LinearLayout(this);
        contentGroups[0].setOrientation(LinearLayout.HORIZONTAL);
        contentGroups[0].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
        llContentGroupHolder.addView(contentGroups[0]);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentIntegrator.REQUEST_CODE
                && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            String path = extras.getString(Intents.Scan.RESULT_BARCODE_IMAGE_PATH);

            // Ein Bitmap zur Darstellung erhalten wir so:
            // Bitmap bmp = BitmapFactory.decodeFile(path)

            String code = extras.getString(Intents.Scan.RESULT);
        }
    }
}
