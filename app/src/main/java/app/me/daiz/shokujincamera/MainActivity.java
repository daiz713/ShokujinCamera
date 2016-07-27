package app.me.daiz.shokujincamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import java.util.List;

public class MainActivity extends Activity {
    SurfaceView   sview;
    SurfaceHolder sholder;
    FrameLayout frame;
    Camera camera;
    String CAM_DIR = "ShokujinCamera";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        frame = new FrameLayout(this);
        setContentView(frame);

        sview = new SurfaceView(this);
        sholder = sview.getHolder();
        sholder.addCallback(new SurfaceHolderCallback());

        Button btn = new Button(this);
        btn.setText("カシャ");
        btn.setLayoutParams(new LayoutParams(200, 150));
        btn.setOnClickListener(new TakePhotoClickListener());

        frame.addView(btn);
        frame.addView(sview);

    }

    class TakePhotoClickListener implements View.OnClickListener {
        @Override
        public void onClick (View view) {
            camera.autoFocus(autoFocusCallback);
        }

        public Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus (boolean success, Camera camer) {
                camera.takePicture(null, null, new TakePhoto());
            }

        };
    }

    class TakePhoto implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);
            // landscapeモードで撮影されるので,portraitに変換する
            bitmap = Utils.getPortraitPhoto(bitmap);
            // 正方形に切り抜く
            bitmap = Utils.cropCenterSquare(bitmap);
            // 写真の中央に文字を追加する
            bitmap = Utils.drawTextInPhoto(bitmap, "5", Color.WHITE);
            // 写真をローカルに保存
            String fname = "img"+Math.floor(Math.random()*10000)+".jpg";
            Utils.savePhotoToLocalStorage(getApplicationContext(), bitmap, CAM_DIR, fname);
            camera.startPreview();
        }
    }

    class SurfaceHolderCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            camera = Camera.open();
            Parameters param = camera.getParameters();
            List<Size> ss = param.getSupportedPictureSizes();
            Size pictureSize = ss.get(0);
            param.setPictureSize(pictureSize.width, pictureSize.height);
            camera.setParameters(param);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
            try {
                // プレビューの向きを調節
                camera.setDisplayOrientation(90);
                camera.setPreviewDisplay(sview.getHolder());

                Camera.Size previewSize = Utils.setCameraPreviewSize(camera);
                LayoutParams lp = Utils.getCameraLayoutViewSize(w, previewSize);
                sview.setLayoutParams(lp);

                Grid grid = new Grid(getApplicationContext(), lp.width, lp.height);
                frame.addView(grid);

                camera.startPreview();

            } catch (Exception e) {
                System.out.print(e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            camera.stopPreview();
            camera.release();
        }
    }
}

