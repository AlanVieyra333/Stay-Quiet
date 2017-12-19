package androides.stayquiet.services;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androides.stayquiet.database.FirebaseManager;
import androides.stayquiet.database.SessionManager;
import androides.stayquiet.database.StayQuietDBHelper;
import androides.stayquiet.user.User;

/**
 * Created by developer on 18/12/17.
 */

public class SQLocationListener implements LocationListener {
    private Location mLastLocation;
    private FirebaseManager firebaseManager;
    private User user;

    public SQLocationListener(String provider)
    {
        this.user = new User();

        mLastLocation = new Location(provider);
        firebaseManager = new FirebaseManager();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation.set(location);
        user.setLocation(mLastLocation);

        firebaseManager.getDBReferenceLocation()
                .child(user.getUsername())
                .setValue(mLastLocation);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
