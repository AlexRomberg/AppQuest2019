package ch.codeclimb.dechiffrierer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;

public class editPicture extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    //global views
    Bitmap bmPictureOriginal, bmPictureFinished;
    ImageView ivPicture;
    SeekBar sbBrightnes;
    Button btBack;

    //is executed when app gets loaded
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture);

        //initializes Views
        ivPicture = findViewById(R.id.ivPicture);
        sbBrightnes = findViewById(R.id.sbBrightnes);
        btBack = findViewById(R.id.btBack);

        //gets Image from Intent and sets it in ivPicture
        Intent intent = getIntent();
        bmPictureOriginal = intent.getExtras().getParcelable("IMAGE");
        bmPictureFinished = bmPictureOriginal;
        ivPicture.setImageBitmap(bmPictureOriginal);
    }

    protected void onResume() {
        super.onResume();

        //Adds eventlisteners
        sbBrightnes.setOnSeekBarChangeListener(this);
        btBack.setOnClickListener(this);
    }

    private Bitmap applyFilter(Bitmap bitmap, int brightnes) {
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

            //reduces brightness
            R -= (255 - brightnes);
            if (R < 0) {
                R = 0;
            }

            //converts new 255 values back to ARGB-value
            data[i] = (A & 0xff) << 24 | (R & 0xff) << 16 | (R & 0xff) << 8 | (R & 0xff);
        }
        //creates bitmap
        return Bitmap.createBitmap(data, width, height, Bitmap.Config.ARGB_8888);
    }

    //apples filter to image if Progressbar changed
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        bmPictureFinished = applyFilter(bmPictureOriginal, progress);
        ivPicture.setImageBitmap(bmPictureFinished);
    }

    //returns Image
    @Override
    public void onClick(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("RETURNIMAGE", bmPictureFinished);
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
}
