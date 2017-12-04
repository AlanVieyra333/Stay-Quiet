package androides.stayquiet;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by developer on 15/10/17.
 */

public class User implements Serializable {
    private String id;
    private String username;
    private String name;
    private String phoneNumber;
    private String email;
    private String password;
    private String photoUrl;
    private byte[] photo;

    public User() {
        this(null, null, null, null, null);
    }

    public User(String username, String name, String phoneNumber, String email, String password) {
        this(null, username, name, phoneNumber, email, password, null, null);
    }

    public User(String id, String username, String name, String phoneNumber, String email, String password, String photoUrl, byte[] photo) {
        super();
        setId(id);
        setUsername(username);
        setName(name);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setPassword(password);
        setPhotoUrl(photoUrl);
        setPhoto(photo);
    }

    @Override
    public String toString() {
        String toString = "";
        toString += getId() + ", ";
        toString += getUsername() + ", ";
        toString += getName() + ", ";
        toString += getPhoneNumber() + ", ";
        toString += getEmail() + ", ";
        toString += getPassword();

        return toString;
    }

    public void setId(String id) {
        if(id != null)
            this.id = id;
        else
            this.id = "";
    }

    public String getId() {
        return this.id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if(username != null)
            this.username = username;
        else
            this.username = "";
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

    public void setEmail(String email) {
        if(email != null)
            this.email = email;
        else
            this.email = "";
    }

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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        if(photoUrl != null)
            this.photoUrl = photoUrl;
        else
            this.photoUrl = "";
    }

    public void setPhoto(byte[]  photo) {
        this.photo = photo;
    }

    public byte[] getPhoto() {
        return this.photo;
    }
}
