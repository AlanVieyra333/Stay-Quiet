package androides.stayquiet;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by developer on 15/10/17.
 */

public class User implements Account, Serializable {
    private String name, phoneNumber, email, password, photoUrl;
    private byte[] photo;

    public User(String name, String phoneNumber, String email, String password, String photoUrl) {
        super();
        setName(name);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setPassword(password);
        setPhotoUrl(photoUrl);
    }

    public User(String name, String phoneNumber, String email, String password, byte[] photo) {
        super();
        setName(name);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setPassword(password);
        setPhoto(photo);
    }

    public User(String email, String password) {
        super();
        setName("");
        setPhoneNumber("");
        setEmail(email);
        setPassword(password);
    }

    public User() {
        super();
        setName("");
        setPhoneNumber("");
        setEmail("");
        setPassword("");
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public int createAccount() {
        return 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhotoUrl(String  photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String  getPhotoUrl() {
        return this.photoUrl;
    }

    public void setPhoto(byte[]  photo) {
        this.photo = photo;
    }

    public byte[] getPhoto() {
        return this.photo;
    }
}
