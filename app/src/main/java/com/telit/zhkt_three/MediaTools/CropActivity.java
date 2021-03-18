package com.telit.zhkt_three.MediaTools;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.edmodo.cropper.CropImageView;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CropActivity extends BaseActivity {
    private Unbinder unbinder;
    @BindView(R.id.crop_btn_save)
    Button crop_btn_save;
    @BindView(R.id.crop_cropView)
    CropImageView crop_cropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        unbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        String type = intent.getType();

        if (!type.startsWith("image/")) {
            finish();
            return;
        }

        Uri originalUri = intent.getData();

        Bitmap bitmap = getBitmapFromUri(originalUri);
        crop_cropView.setImageBitmap(bitmap);

        crop_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri savedUri = intent.getParcelableExtra("save_path");

                Bitmap cropBitmap = crop_cropView.getCroppedImage();

                if (savedUri != null) {
                    try {
                        cropBitmap.compress(Bitmap.CompressFormat.JPEG, 100, getContentResolver().openOutputStream(savedUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        QZXTools.logE("savedUri=" + savedUri + ";FileNotFoundException", null);
                    }
                }

                Intent intent1 = new Intent();
                intent1.setData(savedUri);
                setResult(Activity.RESULT_OK, intent1);

                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    /**
     * 将Uri转Bitmap
     */
    private Bitmap getBitmapFromUri(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            QZXTools.logE("uriToBitmap FileNotFoundException", null);
        }
        return bitmap;
    }
}
