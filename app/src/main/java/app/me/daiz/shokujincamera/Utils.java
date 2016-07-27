package app.me.daiz.shokujincamera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.List;

public class Utils {
    public static Camera.Size setCameraPreviewSize (Camera cam) {
        Camera.Parameters params = cam.getParameters();
        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        Camera.Size previewSize = previewSizes.get(0);
        params.setPictureSize(previewSize.width, previewSize.height);
        cam.setParameters(params);
        return previewSize;
    }

    // 縦長写真の中央正方形部分を切り出す．
    public static Bitmap cropCenterSquare (Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int margin = (height - width) / 2;
        bitmap = Bitmap.createBitmap(bitmap, 0, margin, width, width);
        return bitmap;
    }

    // landscapeモードで撮影された画像を,portraitに変換する
    public static Bitmap getPortraitPhoto (Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return bitmap;
    }

    public static FrameLayout.LayoutParams getCameraLayoutViewSize (int width, Camera.Size size) {
        double rate = (double) size.height / width;
        int height = (int) (size.width * rate);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        return layoutParams;
    }

    public static void autoMediaScan (Context appContext, String[] paths) {
        String[] mimeTypes = {"image/jpeg"};
        MediaScannerConnection.scanFile(
                appContext, paths, mimeTypes, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.d("Scanned FilePath", "-> path=" + path);
                        Log.d("ContentProvider URI", "-> uri=" + uri);
                    }
                }
        );
    }
}
