package androides.stayquiet;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by developer on 15/10/17.
 */

public class User implements Account, Serializable {
    private String name, phoneNumber, email, password;
    private byte[] photo;

    public User(String name, String phoneNumber, String email, String password, byte[] photo) {
        super();
        setName(name);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setPassword(password);
        setPhoto(photo);
    }

    public User(String email) {
        super();
        setName("");
        setPhoneNumber("");
        setEmail(email);
        setPassword("");
    }

    public User() {
        super();
        setName("");
        setPhoneNumber("");
        setEmail("");
        setPassword("");
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
}
