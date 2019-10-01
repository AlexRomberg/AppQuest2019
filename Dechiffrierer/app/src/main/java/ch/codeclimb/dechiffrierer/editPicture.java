package ch.codeclimb.dechiffrierer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;

public class editPicture extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, Switch.OnCheckedChangeListener {

    //global objects
    Bitmap bmPictureOriginal, bmPictureFinished;
    ImageView ivPicture;
    SeekBar sbBrightnes;
    Button btBack;
    Switch swFilter;
    boolean RedFilter;
    int Brightnes;

    //is executed when app gets loaded
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture);

        //initializes Views
        ivPicture = findViewById(R.id.ivPicture);
        sbBrightnes = findViewById(R.id.sbBrightnes);
        btBack = findViewById(R.id.btBack);
        swFilter = findViewById(R.id.swFilter);

        //gets Values from Intent and sets it in ivPicture
        Intent intent = getIntent();
        Brightnes = intent.getExtras().getInt("BRIGHTNES");
        RedFilter = intent.getExtras().getBoolean("REDFILTER");
        bmPictureOriginal = intent.getExtras().getParcelable("ORIGINAL");
        bmPictureFinished = intent.getExtras().getParcelable("IMAGE");
        ivPicture.setImageBitmap(bmPictureFinished);
    }

    //is executed when app gets started
    protected void onResume() {
        super.onResume();

        //Sets Views to correct value
        sbBrightnes.setProgress(Brightnes);
        swFilter.setChecked(RedFilter);

        //Adds eventlisteners
        sbBrightnes.setOnSeekBarChangeListener(this);
        btBack.setOnClickListener(this);
        swFilter.setOnCheckedChangeListener(this);
    }

    private Bitmap applyFilter() {
        //takes image dimensions
        int width = bmPictureOriginal.getWidth();
        int height = bmPictureOriginal.getHeight();

        //creates array and fills pixels in it
        int[] data = new int[width * height];
        bmPictureOriginal.getPixels(data, 0, width, 0, 0, width, height);

        //edits evey pixel
        for (int i = 0; i < data.length; i++) {
            //converts ARGB-value to A, R, G, B 255 values
            int A = (data[i] >> 24) & 0xff;
            int R = (data[i] >> 16) & 0xff;

            //reduces brightness
            R -= (255 - Brightnes);
            if (R < 0) {
                R = 0;
            }
            int G = 0, B = 0;
            if (!RedFilter) {
                G = R;
                B = R;
            }

            //converts new 255 values back to ARGB-value
            data[i] = (A & 0xff) << 24 | (R & 0xff) << 16 | (G & 0xff) << 8 | (B & 0xff);
        }
        //creates bitmap
        return Bitmap.createBitmap(data, width, height, Bitmap.Config.ARGB_8888);
    }

    //apples filter to image if Progressbar changed
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Brightnes = progress;
        bmPictureFinished = applyFilter();
        ivPicture.setImageBitmap(bmPictureFinished);
    }

    //returns Image, Brightnes and Filterstyle
    //Brightnes and Filterstyle for later edits.
    @Override
    public void onClick(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("IMAGE", bmPictureFinished);
        returnIntent.putExtra("BRIGHTNES", Brightnes);
        returnIntent.putExtra("REDFILTER", RedFilter);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    //without function
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    //without function
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    //is started when Filter-Switch changed state
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        RedFilter = isChecked;
        bmPictureFinished = applyFilter();
        ivPicture.setImageBitmap(bmPictureFinished);
    }
}
