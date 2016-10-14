package hu.edudroid.gume.pannonia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

import hu.edudroid.gume.pannoniaDB.PannoniaDBHelper;
import hu.edudroid.gume.pannoniaweb.AsyncResponse;
import hu.edudroid.gume.pannoniaweb.CheckOMIDTask;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener, AsyncResponse {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private PannoniaDBHelper pDB = PannoniaDBHelper.getInstance();

    private String omidCache = null;
    private String passwordCache = null;

    private static final int FILE_SELECT_CODE = 0;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    private static final String TEXT1 = "text1";
    private static final String TEXT2 = "text2";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    final List<Map<String, String>> studentList = new ArrayList<Map<String, String>>();
    final String[] fromMapKey = new String[] {TEXT1, TEXT2};
    final int[] toLayoutId = new int[] {android.R.id.text1, android.R.id.text2};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        List <String> omids = pDB.getStudents();
        for (String omid : omids) {
            HashMap<String, String> student = pDB.getStudent(omid);
            Map<String, String> le;
            le = new HashMap<String, String>();
            le.put(TEXT1, student.get("name") + " (" + student.get("class") + ")");
            le.put(TEXT2, omid);
            studentList.add(le);
        }
        ListView lv = (ListView) v.findViewById(R.id.listView);
        lv.setAdapter(new SimpleAdapter(getContext(), studentList, android.R.layout.simple_list_item_2, fromMapKey, toLayoutId));

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                final SimpleAdapter listAdapter = (SimpleAdapter) parent.getAdapter();
                PopupMenu popup = new PopupMenu(parent.getContext(), view);
                popup.getMenuInflater().inflate(R.menu.popup_setting_students, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Map<String, String> map = studentList.get(position);
                        final String omid = map.get(TEXT2);
                        switch (item.getItemId()) {
                            case R.id.one:
                                studentList.remove(position);
                                pDB.delStudent(omid);
                                listAdapter.notifyDataSetChanged();
                                break;
                            case R.id.two:
                                AlertDialog.Builder ab = new AlertDialog.Builder(parent.getContext());
                                ab.setTitle(R.string.txt_settings_class);
                                final Spinner spc1 = new Spinner(parent.getContext());
                                final Spinner spc2 = new Spinner(parent.getContext());
                                String c1[] = {"1", "2", "3", "4", "5", "6", "7", "8"};
                                String c2[] = {"a", "b"};
                                spc1.setAdapter(new ArrayAdapter<String>(parent.getContext(),
                                        android.R.layout.simple_spinner_item, c1));
                                spc2.setAdapter(new ArrayAdapter<String>(parent.getContext(),
                                        android.R.layout.simple_spinner_item, c2));
                                LinearLayout ll = new LinearLayout(getContext());
                                ll.setOrientation(LinearLayout.HORIZONTAL);
                                ll.addView(spc1);
                                ll.addView(spc2);
                                ab.setView(ll);
                                ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String clas = spc1.getSelectedItem().toString()
                                                + spc2.getSelectedItem().toString();
                                        HashMap student = pDB.getStudent(omid);
                                        pDB.setStudent((String) student.get("name"), omid,
                                                clas, (String) student.get("password"));
                                        Map<String, String> le;
                                        le = new HashMap<String, String>();
                                        le.put(TEXT1, student.get("name") + " (" + clas + ")");
                                        le.put(TEXT2, omid);
                                        studentList.remove(position);
                                        studentList.add(position, le);
                                        listAdapter.notifyDataSetChanged();
                                    }
                                });
                                ab.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                ab.show();

                                //Toast.makeText(parent.getContext(), "You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
                popup.show();
                return true;
            }
        });

        Button bns = (Button) v.findViewById(R.id.button_newstudent);
        bns.setOnClickListener(this);

        Button bdbd = (Button) v.findViewById(R.id.button_deletedb);
        bdbd.setOnClickListener(this);

        Button bdbe = (Button) v.findViewById(R.id.button_exportdb);
        bdbe.setOnClickListener(this);

        Button bdbi = (Button) v.findViewById(R.id.button_importdb);
        bdbi.setOnClickListener(this);

        return v;

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        ((PannoniaMain) activity).onSectionAttached(getString(R.string.action_settings));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    protected void onClick_newStudent(final View v) {

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(R.string.txt_settings_newstudent);
        final EditText omid = new EditText(getContext());
        omid.setInputType(InputType.TYPE_CLASS_NUMBER);
        omid.setHint(R.string.txt_settings_hint_omid);
        final EditText password = new EditText(getContext());
        password.setInputType(InputType.TYPE_CLASS_TEXT);
        password.setHint(R.string.txt_settings_hint_password);
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(omid);
        ll.addView(password);
        alert.setView(ll);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String omidStr = omid.getText().toString();
                String passwordStr = password.getText().toString();

                ListView lv = (ListView) (((View) v.getParent()).findViewById(R.id.listView));
                omidCache = omidStr;
                passwordCache = passwordStr;
                new CheckOMIDTask(lv.getAdapter(), studentList, SettingsFragment.this).execute(omidStr, passwordStr);

                if (mListener != null) {
                    //mListener.onFragmentInteraction(uri);
                }
            }
        });
        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    public void onClick_exportDB(final View v) {
        pDB.exportDB();
        Toast.makeText(getActivity(), "Database exported to SD card", Toast.LENGTH_SHORT).show();
    }

    public void onClick_importDB(final View v) {

        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android. Manifest.permission.READ_EXTERNAL_STORAGE },
                    0);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getContext(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    // Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    try {
                        String path = getPath(getContext(), uri);
                        // Log.d(TAG, "File Path: " + path);
                        pDB.importDB(path);
                    } catch (Exception e) {

                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public void onClick_deleteDB(final View v) {
        pDB.deleteDB();
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.button_newstudent) {
            onClick_newStudent(v);
        }
        else if (v.getId() == R.id.button_exportdb) {
            onClick_exportDB(v);
        }
        else if (v.getId() == R.id.button_importdb) {
            onClick_importDB(v);
        }
        else if (v.getId() == R.id.button_deletedb) {
            onClick_deleteDB(v);
        }
    }

    @Override
    public void processFinish(String id, String result) {

        if (result == null || result.equals("")) return;
        pDB.setStudent(result, omidCache, "?", passwordCache);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
