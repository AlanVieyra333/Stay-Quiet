package androides.stayquiet.activities;

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
import androides.stayquiet.database.FirebaseManager;
import androides.stayquiet.database.SessionManager;
import androides.stayquiet.tools.Tools;
import androides.stayquiet.user.User;

/**
 * Created by developer on 17/12/17.
 */

public class UsersAdapter extends ArrayAdapter<User> {
    private ImageView ivImageProfile;
    private TextView tvName;
    private TextView tvLocation;
    private ImageView ivIconInfo, ivIconDelete;
    private FirebaseManager firebaseManager;
    private SessionManager session;
    private User user;

    public UsersAdapter(@NonNull Context context, ArrayList<User> users) {
        super(context, 0, users);
        firebaseManager = new FirebaseManager();
        // Session data.
        session = new SessionManager(context);
        user = session.getUser();
    }

    @Override
    public View getView(int position, View convertViewTmp, ViewGroup parent) {
        // Get the data item for this position
        final User userProtected = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertViewTmp == null) {
            convertViewTmp = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        final View convertView = convertViewTmp;

        // Lookup view for data population
        ivImageProfile = (ImageView) convertView.findViewById(R.id.ivImageProfile);
        tvName = (TextView) convertView.findViewById(R.id.tvName);
        tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);
        ivIconInfo = convertView.findViewById(R.id.icon_info);
        ivIconDelete = convertView.findViewById(R.id.icon_delete);

        // Populate the data into the template view using the data object
        tvName.setText(userProtected.getName());
        tvLocation.setText("México city");

        if ( userProtected.getPhoto() != null) {
            ivImageProfile.setImageBitmap(Tools.bytesToBitmap(userProtected.getPhoto()));
        }

        ivIconInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tools.showMessage(convertView, "Nombre de usuario: " + userProtected.getUsername());
            }
        });

        ivIconDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseManager.getDBReferenceProtection()
                        .child(user.getUsername())
                        .child(userProtected.getUsername())
                        .removeValue();
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
