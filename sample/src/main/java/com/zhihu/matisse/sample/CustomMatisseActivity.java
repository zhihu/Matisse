package com.zhihu.matisse.sample;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.ImageEngine;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.List;
import java.util.Set;

/**
 * Custom Matisse
 */
public class CustomMatisseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_CHOOSE = 23;

    private List<Uri> mSelectedUris;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_matisse);

        findViewById(R.id.btn_go).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            // Use Uri result
            TextView resultTextView = (TextView) findViewById(R.id.tv_result);
            resultTextView.setText("");
            mSelectedUris = Matisse.obtainResult(data);
            for (Uri uri : mSelectedUris) {
                resultTextView.append(uri.toString());
                resultTextView.append("\n");
            }

            // Use absolute path result
            TextView pathTextView = (TextView) findViewById(R.id.tv_path_result);
            pathTextView.setText("");
            List<String> pathResult = Matisse.obtainPathResult(data);
            for (String path : pathResult) {
                pathTextView.append(path);
                pathTextView.append("\n");
            }
        }
    }

    @Override
    public void onClick(View v) {
        CheckBox imageCheckBox = (CheckBox) findViewById(R.id.cb_choice_image);
        CheckBox videoCheckBox = (CheckBox) findViewById(R.id.cb_choice_video);
        RadioButton zhihuRadioButton = (RadioButton) findViewById(R.id.rb_theme_zhihu);
        RadioButton draculaRadioButton = (RadioButton) findViewById(R.id.rb_theme_dracula);
        RadioButton customThemeButton = (RadioButton) findViewById(R.id.rb_theme_custom);
        RadioButton glideRadioButton = (RadioButton) findViewById(R.id.rb_glide);
        RadioButton picassoRadioButton = (RadioButton) findViewById(R.id.rb_picasso);
        RadioButton uilRadioButton = (RadioButton) findViewById(R.id.rb_uil);
        EditText selectCountEditor = (EditText) findViewById(R.id.et_selectable_count);
        CheckBox countableCheckBox = (CheckBox) findViewById(R.id.cb_countable);
        CheckBox captureCheckBox = (CheckBox) findViewById(R.id.cb_capture);

        Set<MimeType> mimeTypes;
        if (imageCheckBox.isChecked() && videoCheckBox.isChecked()) {
            mimeTypes = MimeType.ofAll();
        } else if (imageCheckBox.isChecked()) {
            mimeTypes = MimeType.ofImage();
        } else {
            mimeTypes = MimeType.ofVideo();
        }

        ImageEngine imageEngine = null;
        if (glideRadioButton.isChecked()) {
            imageEngine = new GlideEngine();
        } else if (picassoRadioButton.isChecked()) {
            imageEngine = new PicassoEngine();
        } else if (uilRadioButton.isChecked()) {
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
            imageEngine = new UILEngine();
        }

        String maxCount = selectCountEditor.getText().toString();
        int maxSelectable = Integer.parseInt(maxCount);

        int theme = R.style.Matisse_Dracula;
        if (zhihuRadioButton.isChecked()) {
            theme = R.style.Matisse_Zhihu;
        } else if (draculaRadioButton.isChecked()) {
            theme = R.style.Matisse_Dracula;
        } else if (customThemeButton.isChecked()) {
            theme = R.style.CustomTheme;
        } else {
            // custom theme
        }

        boolean countable = countableCheckBox.isChecked();
        boolean capture = captureCheckBox.isChecked();

        Matisse.from(this)
                .choose(mimeTypes, false)
                .showSingleMediaType(true)
                .capture(capture)
                .captureStrategy(
                        new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider"))
                .countable(countable)
                .maxSelectable(maxSelectable)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(
                        getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(imageEngine)
                .theme(theme)
                .forResult(REQUEST_CODE_CHOOSE, mSelectedUris);
    }
}
