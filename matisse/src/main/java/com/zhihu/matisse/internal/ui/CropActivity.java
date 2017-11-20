package com.zhihu.matisse.internal.ui;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.utils.BitmapUtils;
import com.zhihu.matisse.internal.utils.PathUtils;
import com.zhihu.matisse.internal.utils.Platform;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import java.io.File;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.zhihu.matisse.R.id.cropImageView;

public class CropActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String CROP_IMAGE_URI = "crop_image_uri";

    public static final String CROP_IMAGE_PATH = "crop_image_path";

    private static final String ARGS_ITEM = "args_uri";

    private CropImageView mCropImageView;

    private View mBottomBar;

    private Uri mUri;

    private Disposable mDisposable;

    public static Intent newIntent(Context context, Uri uri) {
        Intent intent = new Intent(context, CropActivity.class);
        intent.putExtra(ARGS_ITEM, uri);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(SelectionSpec.getInstance().themeId);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        if (Platform.hasKitKat()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (SelectionSpec.getInstance().needOrientationRestriction()) {
            setRequestedOrientation(SelectionSpec.getInstance().orientation);
        }

        mUri = getIntent().getParcelableExtra(ARGS_ITEM);
        mCropImageView = (CropImageView) findViewById(cropImageView);
        mBottomBar = findViewById(R.id.bottom_toolbar);
        findViewById(R.id.button_back).setOnClickListener(this);
        findViewById(R.id.button_apply).setOnClickListener(this);
        findViewById(R.id.rotate_left).setOnClickListener(this);
        findViewById(R.id.rotate_right).setOnClickListener(this);
        mCropImageView.setCompressFormat(Bitmap.CompressFormat.PNG);
        mCropImageView.load(mUri).execute(new LoadCallback() {
            @Override
            public void onSuccess() {
                mBottomBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Throwable throwable) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_back) {
            finish();
        } else if (v.getId() == R.id.button_apply) {
            cropImage();
        } else if (v.getId() == R.id.rotate_left) {
            mCropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
        } else if (v.getId() == R.id.rotate_right) {
            mCropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
        }
    }

    private void cropImage() {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        mDialog.setMessage(getString(R.string.crop_clipping));
        mDialog.show();
        mDisposable = mCropImageView.crop(mUri)
                .executeAsSingle()
                .flatMap(new Function<Bitmap, SingleSource<File>>() {
                    @Override
                    public SingleSource<File> apply(@NonNull Bitmap bitmap) throws Exception {
                        return Single.just(BitmapUtils.saveBitmap(CropActivity.this, bitmap,
                                PathUtils.getCropImagePath(CropActivity.this)));
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mDialog.dismiss();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(@NonNull File cropFile) throws Exception {
                        applyImpl(cropFile);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        finish();
                    }
                });
    }

    private void applyImpl(File cropFile) {
        Intent data = new Intent();
        data.putExtra(CROP_IMAGE_URI, Uri.fromFile(cropFile));
        data.putExtra(CROP_IMAGE_PATH, cropFile.getPath());
        setResult(RESULT_OK, data);
        finish();
    }
}
