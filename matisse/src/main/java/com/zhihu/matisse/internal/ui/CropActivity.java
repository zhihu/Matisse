package com.zhihu.matisse.internal.ui;

import com.isseiaoki.simplecropview.CropImageView;
import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.utils.BitmapUtils;
import com.zhihu.matisse.internal.utils.PathUtils;
import com.zhihu.matisse.internal.utils.Platform;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

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

    private static final int SIZE_DEFAULT = 2048;

    private static final int SIZE_LIMIT = 4096;

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
        InputStream is = null;
        try {
            int sampleSize = calculateBitmapSampleSize(mUri);
            is = getContentResolver().openInputStream(mUri);
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inSampleSize = sampleSize;
            Bitmap sizeBitmap = BitmapFactory.decodeStream(is, null, option);
            if (sizeBitmap == null) {
                finish();
                return;
            }
            mCropImageView.setImageBitmap(sizeBitmap);
            mBottomBar.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int calculateBitmapSampleSize(Uri bitmapUri) throws IOException {
        InputStream is = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            is = getContentResolver().openInputStream(bitmapUri);
            BitmapFactory.decodeStream(is, null, options); // Just get image size
        } finally {
            is.close();
        }

        int maxSize = getMaxImageSize();
        int sampleSize = 1;
        while (options.outHeight / sampleSize > maxSize
                || options.outWidth / sampleSize > maxSize) {
            sampleSize = sampleSize << 1;
        }
        return sampleSize;
    }

    private int getMaxImageSize() {
        int textureLimit = getMaxTextureSize();
        if (textureLimit == 0) {
            return SIZE_DEFAULT;
        } else {
            return Math.min(textureLimit, SIZE_LIMIT);
        }
    }

    private int getMaxTextureSize() {
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        return maxSize[0];
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
        mDisposable = Single.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                Bitmap crop = mCropImageView.getCroppedBitmap();
                return crop;
            }
        }).flatMap(new Function<Bitmap, SingleSource<File>>() {
            @Override
            public SingleSource<File> apply(@NonNull Bitmap bitmap) throws Exception {
                return Single.just(BitmapUtils.saveBitmap(bitmap,
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
