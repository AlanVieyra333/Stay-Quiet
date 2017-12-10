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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
 *
 * @author author Alan Vieyra
 */
public class FirebaseManager {
    private AppCompatActivity activity;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private User user;
    private OnSuccessListener<Void> callback;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    public FirebaseManager(AppCompatActivity activity) {
        setActivity(activity);
        setmAuth(FirebaseAuth.getInstance());
        setStorage(FirebaseStorage.getInstance());
        setCurrentUser(getmAuth().getCurrentUser());
        setUser(null);
        setCallback(null);
        setDatabase(FirebaseDatabase.getInstance());
        setDatabaseReference(getDatabase().getReference());
        setmCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                associatePhoneNumber(credential, getUser().getPhoneNumber());
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Tools.showMessage(getActivity(), R.string.MSJ1_32);
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                Intent intentVerifyPhone = new Intent(getActivity(), VerifyPhoneActivity.class);
                intentVerifyPhone.putExtra("verificationId", verificationId);
                intentVerifyPhone.putExtra("phoneNumber", getUser().getPhoneNumber());
                getUser().setPhoto(null);
                intentVerifyPhone.putExtra("user", getUser());

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

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public void setDatabase(FirebaseDatabase database) {
        this.database = database;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference
                .child(StayQuietDBHelper.USER_TABLE);
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }
    /*  -----------------------------------------------------------------------------------   */
    /**
     * Método para iniciar sesion con un nombre de usuario y contraseña.
     * Crea un objeto User.
     * Se hace una petición a la BD de firebase con el nombre de usuario ingresado, para obtener
     * su correo.
     * Despues se autentica a firebase con su email y contraseña.
     * Finalmente se ejecuta el callback que se haya establecido desde fuera del objeto. (Esto se
     * hace debido a que las funciones anteriores son asícronas)
     *
     * @param username Nombre de usuario.
     * @param password Contraseña.
     */
    public void login(String username, String password) {
        Tools.showProgressbar(getActivity());

        setUser(new User(username, password));

        getDatabaseReference().child(getUser().getUsername())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setUser(dataSnapshot.getValue(User.class));

                getmAuth().signInWithEmailAndPassword(getUser().getEmail(), getUser().getPassword())
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

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(databaseError.toString().indexOf("user") != -1)
                    Tools.showMessage(getActivity(), R.string.MSJ1_7);
                else
                    Tools.showMessage(getActivity(), R.string.MSJ1_6);
            }
        });
    }

    public void logout() {
        getmAuth().signOut();
    }

    /**
     * Método para registrar un usuario.
     * Crea un usuario en firebase para autenticacion con email y pass.
     * Despues guarda todo el usuario en la base de datos de firebase.
     * Despues actualiza el perfil. (Para actualizar los datos en el usuario authFirebase)
     *
     * @param user Usuario a registrar.
     */
    public void signUp(User user) {
        setUser(user);

        Tools.showProgressbar(getActivity());
        getmAuth().createUserWithEmailAndPassword(getUser().getEmail(), getUser().getPassword())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        setCurrentUser(authResult.getUser());
                        getUser().setId(getCurrentUser().getUid());     // Save id user.

                        createUserDBFirebase(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                updateProfile(getUser());
                            }
                        });
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

    public void updateName(final String name) {
        if(name != null && !name.equals("") && !name.equals(getCurrentUser().getDisplayName())) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            Tools.showProgressbar(getActivity());
            getCurrentUser().updateProfile(profile)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            getUser().setName(name);

                            getDatabaseReference()
                                    .child(getUser().getUsername())
                                    .child(StayQuietDBHelper.USER_COLUMN_NAME)
                                    .setValue(name)
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

    public void updateEmail(final String email) {
        String currentEmail = getCurrentUser().getEmail();
        if(email != null && !email.equals("") && !email.equals(currentEmail)) {
            Tools.showProgressbar(getActivity());
            getCurrentUser().updateEmail(email)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            getUser().setEmail(email);

                            getDatabaseReference()
                                    .child(getUser().getUsername())
                                    .child(StayQuietDBHelper.USER_COLUMN_EMAIL)
                                    .setValue(email)
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

    public void updatePhoto(String photoUri) {
        if(photoUri != null && !photoUri.equals("") && !photoUri.equals(getCurrentUser().getPhotoUrl().toString())) {
            String uid = getCurrentUser().getUid();
            final String url = StayQuietDBHelper.URL_IMAGES + uid + ".jpg";

            try {
                StorageReference photoRef = getStorage().getReference().child(url);
                final InputStream stream = new FileInputStream(new File(photoUri));

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
        }else if(getCurrentUser().getPhotoUrl() == null || getCurrentUser().getPhotoUrl().toString().equals("")) {    // Default photo.
            String url = StayQuietDBHelper.URL_IMAGES + StayQuietDBHelper.PHOTO_DEFAULT;
            updatePhotoUrl(url);
        }else {
            getUser().setPhotoUrl(getCurrentUser().getPhotoUrl().toString());
            updatePhoneNumber(getUser().getPhoneNumber());
        }
    }

    public void updatePhotoUrl(final String photoUrl) {
        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(photoUrl))
                .build();

        Tools.showProgressbar(getActivity());
        getCurrentUser().updateProfile(profile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getUser().setPhotoUrl(photoUrl);

                        getDatabaseReference()
                                .child(getUser().getUsername())
                                .child(StayQuietDBHelper.USER_COLUMN_PHOTO_URL)
                                .setValue(photoUrl)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        getUser().setPhotoUrl(photoUrl);
                                        updatePhoneNumber(getUser().getPhoneNumber());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Tools.showMessage(getActivity(), R.string.MSJ1_6);
                                    }
                                });
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
        phoneNumber = "+52" + phoneNumber;
        String currentPhoneNumber = getCurrentUser().getPhoneNumber();

        if(phoneNumber != null && !phoneNumber.equals("")  && !phoneNumber.equals(currentPhoneNumber)) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,    // Phone number to verify
                    1,                      // Timeout duration
                    TimeUnit.MINUTES,       // Unit of timeout
                    getActivity(),          // Activity (for callback binding)
                    getmCallbacks());            // OnVerificationStateChangedCallbacks
        } else {
            getCallback().onSuccess(null);
        }
    }

    public void associatePhoneNumber(PhoneAuthCredential credential, final String phoneNumber) {
        Tools.showProgressbar(getActivity());
        getCurrentUser().updatePhoneNumber(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getUser().setPhoneNumber(phoneNumber);

                        getDatabaseReference()
                                .child(getUser().getUsername())
                                .child(StayQuietDBHelper.USER_COLUMN_PHONE_NUMBER)
                                .setValue(phoneNumber)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        getCallback().onSuccess(aVoid);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Tools.showMessage(getActivity(), R.string.MSJ1_6);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Tools.showMessage(getActivity(), R.string.MSJ1_31);
                    }
                });
    }

    public void createUserDBFirebase(OnSuccessListener<Void> onSuccessListener) {
        getDatabaseReference()
                .child(getUser().getUsername())
                .setValue(getUser())
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Tools.showMessage(getActivity(), R.string.MSJ1_6);
                    }
                });
    }
}
