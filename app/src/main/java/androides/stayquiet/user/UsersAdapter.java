package androides.stayquiet.user;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androides.stayquiet.R;
import androides.stayquiet.tools.Tools;
import androides.stayquiet.user.User;

/**
 * Created by developer on 17/12/17.
 */

public class UsersAdapter extends ArrayAdapter<User> {
    public UsersAdapter(@NonNull Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        // Lookup view for data population
        ImageView ivImageProfile = (ImageView) convertView.findViewById(R.id.ivImageProfile);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);

        // Populate the data into the template view using the data object
        tvName.setText(user.getName());
        tvLocation.setText("México city");

        if ( user.getPhoto() != null) {
            ivImageProfile.setImageBitmap(Tools.bytesToBitmap(user.getPhoto()));
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
