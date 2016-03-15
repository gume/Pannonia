package hu.edudroid.gume.timetableview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;

public class LessonPicker extends DialogFragment {

    public interface LessonPickerListener {
        public void onDialogChoice(DialogFragment dialog);
    }

    private LessonPickerListener mListener = null;
    public String choices[] = { "<üres>", "<új>" };
    public int selection = -1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Tárgyválasztás");
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                selection = which;

                if (selection == 1) {

                    final EditText input = new EditText(getActivity());

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Új tárgy")
                            .setMessage("Adja meg az új tárgy nevét:")
                            .setView(input)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    choices[1] = input.getText().toString();
                                    mListener.onDialogChoice(LessonPicker.this);
                                }
                            }).setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            choices[0] = null;
                            mListener.onDialogChoice(LessonPicker.this);
                        }
                    }).show();
                }

                else if (selection == 0) {
                    choices[0] = "";
                    mListener.onDialogChoice(LessonPicker.this);
                }

                else {
                    mListener.onDialogChoice(LessonPicker.this);
                }
            }
        });

        return builder.create();
    }

    public void setListener(LessonPickerListener lpl) {
        mListener = lpl;
    }

    public void setChoices(ArrayList<String> c) {
        ArrayList<String> ch = new ArrayList<String>();
        ch.add("<üres>");
        ch.add("<új>");

        Collections.sort(c);
        ch.addAll(c);

        choices = new String[ch.size()];
        ch.toArray(choices);
    }

    public String getSelection() {

        return choices[selection];
    }

}
