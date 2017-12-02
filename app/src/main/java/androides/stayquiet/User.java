package androides.stayquiet;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by developer on 15/10/17.
 */

public class User implements Account, Serializable {
    private String name;
    private String phoneNumber;
    private String email;
    private String password;
    private String id;

    private String photoUrl;
    private byte[] photo;

    public User(String name, String phoneNumber, String email, String password, byte[] photo, String id, String photoUrl) {
        super();
        setName(name);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setPassword(password);
        setPhoto(photo);
        setId(id);
        setPhotoUrl(photoUrl);
    }

    public User(String name, String phoneNumber, String email, String password, byte[] photo, String id) {
        super();
        setName(name);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setPassword(password);
        setPhoto(photo);
        setId(id);
        setPhotoUrl("");
    }

    public User(String name, String phoneNumber, String email, String password) {
        super();
        setName(name);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setPassword(password);
        setPhoto(null);
        setId("");
        setPhotoUrl("");
    }

    @Override
    public String toString() {
        String toString = "";
        toString += getName() + ", ";
        toString += getPhoneNumber() + ", ";
        toString += getEmail() + ", ";
        toString += getPassword();

        return toString;
    }

    @Override
    public void setEmail(String email) {
        if(email != null)
            this.email = email;
        else
            this.email = "";
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    public void setPassword(String password) {
        if(password != null)
            this.password = password;
        else
            this.password = "";
    }

    public String getPassword() {
        return this.password;
    }

    public void setName(String name) {
        if(name != null)
            this.name = name;
        else
            this.name = "";
    }

    public String getName() {
        return this.name;
    }

    public void setPhoneNumber(String phoneNumber) {
        if(phoneNumber != null)
            this.phoneNumber = phoneNumber;
        else
            this.phoneNumber = "";
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoto(byte[]  photo) {
        this.photo = photo;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
