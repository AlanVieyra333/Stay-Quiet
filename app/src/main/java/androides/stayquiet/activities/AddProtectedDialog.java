package androides.stayquiet.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androides.stayquiet.R;

/**
 * Created by developer on 17/12/17.
 */

public class AddProtectedDialog extends AppCompatDialogFragment {
    private EditText etUsername;
    private addProtectedDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_protected, null);

        builder.setView(view)
                .setTitle("Agregar protegido")
                .setNegativeButton("CALCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("AGREGAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String username = etUsername.getText().toString();
                        listener.applyText(username);
                    }
                });

        etUsername = view.findViewById(R.id.etUsername);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (addProtectedDialogListener) context;
        } catch (Exception e) {}
    }

    public interface addProtectedDialogListener {
        void applyText(String username);
    }
}
