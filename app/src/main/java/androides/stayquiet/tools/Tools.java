package androides.stayquiet.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

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
}
