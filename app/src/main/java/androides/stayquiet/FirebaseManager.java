package androides.stayquiet;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import androides.stayquiet.tools.Tools;

/**
 * Created by developer on 26/11/17.
 */

public class FirebaseManager {
    private AppCompatActivity activity;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private User user;
    private OnSuccessListener<Void> callback;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    public FirebaseManager(AppCompatActivity activity) {
        setActivity(activity);
        setmAuth(FirebaseAuth.getInstance());
        setStorage(FirebaseStorage.getInstance());
        setCurrentUser(getmAuth().getCurrentUser());
        setUser(null);
        setCallback(null);
        setmCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                associatePhoneNumber(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Tools.showMessage(getActivity(), R.string.MSJ1_32);
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                Intent intentVerifyPhone = new Intent(getActivity(), VerifyPhoneActivity.class);
                intentVerifyPhone.putExtra("verificationId", verificationId);

                Tools.hideProgressbar(getActivity());
                getActivity().startActivity(intentVerifyPhone);
                getActivity().finish();
            }
        });
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void setmAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public void setStorage(FirebaseStorage storage) {
        this.storage = storage;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OnSuccessListener<Void> getCallback() {
        return callback;
    }

    public void setCallback(OnSuccessListener<Void> callback) {
        this.callback = callback;
    }

    public PhoneAuthProvider.OnVerificationStateChangedCallbacks getmCallbacks() {
        return mCallbacks;
    }

    public void setmCallbacks(PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks) {
        this.mCallbacks = mCallbacks;
    }
    /*  -----------------------------------------------------------------------------------   */
    public void login(String email, String password) {
        Tools.showProgressbar(getActivity());
        getmAuth().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        setCurrentUser(authResult.getUser());
                        getCallback().onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e.getMessage().indexOf("user") != -1)
                            Tools.showMessage(getActivity(), R.string.MSJ1_7);
                        else
                            Tools.showMessage(getActivity(), R.string.MSJ1_6);
                    }
                });
    }

    public void logout() {
        getmAuth().signOut();
        Intent intentLogin = new Intent(getActivity(), LoginActivity.class);

        intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intentLogin);
        getActivity().finish();
    }

    public void signUp(User user) {
        setUser(user);

        Tools.showProgressbar(getActivity());
        getmAuth().createUserWithEmailAndPassword(getUser().getEmail(), getUser().getPassword())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        setCurrentUser(authResult.getUser());
                        updateProfile(getUser());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e.getMessage().indexOf("user") != -1)
                            Tools.showMessage(getActivity(), R.string.MSJ1_7);
                        else
                            Tools.showMessage(getActivity(), R.string.MSJ1_6);
                    }
                });
    }

    public void updateProfile(User user) {
        setUser(user);

        updateName(getUser().getName());
    }

    public void updateName(String name) {
        if(name != null && name != "" && name != getCurrentUser().getDisplayName()) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            Tools.showProgressbar(getActivity());
            getCurrentUser().updateProfile(profile)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            updateEmail(getUser().getEmail());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tools.showMessage(getActivity(), R.string.MSJ1_6);
                        }
                    });
        } else {
            updateEmail(getUser().getEmail());
        }
    }

    public void updateEmail(String email) {
        if(email != null && email != "" && email != getCurrentUser().getEmail()) {
            Tools.showProgressbar(getActivity());
            getCurrentUser(). updateEmail(email)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            updatePhoto(getUser().getPhotoUrl());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tools.showMessage(getActivity(), R.string.MSJ1_6);
                        }
                    });
        } else {
            updatePhoto(getUser().getPhotoUrl());
        }
    }

    public void  updatePhoto(String photoUri) {
        if(photoUri != null && photoUri != "" && photoUri != getCurrentUser().getPhotoUrl().toString()) {
            String uid = getCurrentUser().getUid();
            final String url = StayQuietDBHelper.URL_IMAGES + uid + ".jpg";

            try {
                StorageReference photoRef = getStorage().getReference().child(url);
                InputStream stream = new FileInputStream(new File(photoUri));

                Tools.showProgressbar(getActivity());
                photoRef.putStream(stream)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (taskSnapshot.getDownloadUrl() != null) {
                                    updatePhotoUrl(url);
                                } else {
                                    Tools.showMessage(getActivity(), R.string.MSJ1_6);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Tools.showMessage(getActivity(), R.string.MSJ1_6);
                            }
                        });
            } catch (Exception e) {
                Tools.showMessage(getActivity(), R.string.MSJ1_6);
            }
        }else if(getCurrentUser().getPhotoUrl() == null || getCurrentUser().getPhotoUrl().toString() == "") {    // Default photo.
            String url = StayQuietDBHelper.URL_IMAGES + StayQuietDBHelper.PHOTO_DEFAULT;
            updatePhotoUrl(url);
        }else {
            updatePhoneNumber(getUser().getPhoneNumber());
        }
    }

    public void updatePhotoUrl(String downloadUrl) {
        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(downloadUrl))
                .build();

        Tools.showProgressbar(getActivity());
        getCurrentUser().updateProfile(profile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updatePhoneNumber(getUser().getPhoneNumber());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Tools.showMessage(getActivity(), R.string.MSJ1_6);
                    }
                });
    }

    public void updatePhoneNumber(String phoneNumber) {
        if(phoneNumber != null && phoneNumber != ""  && ("+52" + phoneNumber) != getCurrentUser().getPhoneNumber()) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+52" + phoneNumber,    // Phone number to verify
                    1,                      // Timeout duration
                    TimeUnit.MINUTES,       // Unit of timeout
                    getActivity(),          // Activity (for callback binding)
                    getmCallbacks());            // OnVerificationStateChangedCallbacks
        } else {
            getCallback().onSuccess(null);
        }
    }

    public void associatePhoneNumber(PhoneAuthCredential credential) {
        Tools.showProgressbar(getActivity());
        getCurrentUser().updatePhoneNumber(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getCallback().onSuccess(aVoid);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Tools.showMessage(getActivity(), R.string.MSJ1_31);
                    }
                });
    }
}
