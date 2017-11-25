package androides.stayquiet.tools;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;

import androides.stayquiet.LoginActivity;
import androides.stayquiet.R;
import androides.stayquiet.StayQuietDBHelper;
import androides.stayquiet.User;

/**
 * Created by developer on 24/11/17.
 */

public class ImageDownloader extends AsyncTask<Uri, Void, Bitmap> {
    private AppCompatActivity activity;
    private ProgressBar progressBar;
    private Intent intent;
    private User user;

    public ImageDownloader(AppCompatActivity activity, ProgressBar progressBar, User user, Intent intent) {
        this.progressBar = progressBar;
        this.activity = activity;
        this.intent = intent;
        this.user = user;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Bitmap doInBackground(Uri... urls) {
        Bitmap image = null;

        try {
            URL url = new URL(urls[0].toString());
            InputStream stream = url.openStream();
            image = BitmapFactory.decodeStream(stream);
        } catch (Exception e) { }

        return image;
    }

    @Override
    protected void onPostExecute(Bitmap image) {
        progressBar.setVisibility(View.GONE);

        if (image != null) {
            user.setPhoto(Tools.bitmapToBytes(image));

            intent.putExtra("user", user);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            activity.startActivity(intent);
            activity.finish();
        } else {
            Toast.makeText(activity.getApplicationContext(), R.string.MSJ1_6,
                    Toast.LENGTH_LONG).show();
        }
    }
}
