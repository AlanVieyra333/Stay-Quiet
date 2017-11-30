package androides.stayquiet.tools;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androides.stayquiet.R;
import androides.stayquiet.StayQuietDBHelper;
import androides.stayquiet.User;

/**
 * Created by developer on 23/11/17.
 */

public class Tools {
    // convert from bitmap to byte array
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap bytesToBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static String getPathFromURI(AppCompatActivity activity, Uri contentURI) {
        String result;
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static void showMessage(AppCompatActivity activity, int message) {
        Tools.hideProgressbar(activity);
        Toast.makeText(activity.getApplicationContext(), message,
                Toast.LENGTH_LONG).show();
    }

    public static void showProgressbar(AppCompatActivity activity) {
        if (activity.findViewById(R.id.progressBar) != null)
            activity.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    public static void hideProgressbar(AppCompatActivity activity) {
        if (activity.findViewById(R.id.progressBar) != null)
            activity.findViewById(R.id.progressBar).setVisibility(View.GONE);
    }
}
