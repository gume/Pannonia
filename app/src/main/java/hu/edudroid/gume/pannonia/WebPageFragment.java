package hu.edudroid.gume.pannonia;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import hu.edudroid.gume.pannoniaweb.LoadGradesTask;
import hu.edudroid.gume.pannoniaweb.LoadHomeworkTask;
import hu.edudroid.gume.pannoniaweb.LoadMessagesTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WebPageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WebPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebPageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TYPE = "type";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String type;
    private String param1;
    private String param2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WebPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WebPageFragment newInstance(String type, String param1, String param2) {
        WebPageFragment fragment = new WebPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WebPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
            param1 = getArguments().getString(ARG_PARAM1);
            param2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_webpage, container, false);

        WebView wv = (WebView) rootView.findViewById(R.id.webView);

        if (type.equals("HW")) {
            new LoadHomeworkTask(wv).execute(param1);
        }
        else if (type.equals("GR")) {
            new LoadGradesTask(wv).execute(param1, param2);
        }
        else if (type.equals("MS")) {
            new LoadMessagesTask(wv).execute(param1, param2);
        }
        else if (type.equals("SP")) {
            //wv.getSettings().setLoadWithOverviewMode(true);
            //wv.getSettings().setUseWideViewPort(true);
            wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            wv.loadUrl("file:///android_asset/pannonia_suli.html");
        }

        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setDisplayZoomControls(false);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }
        if (type.equals("HW")) {
            ((PannoniaMain) activity).onSectionAttached(getString(R.string.txt_navigation_homework));
        }
        else if (type.equals("GR")) {
            ((PannoniaMain) activity).onSectionAttached(getString(R.string.txt_navigation_grades));
        }
        else if (type.equals("MS")) {
            ((PannoniaMain) activity).onSectionAttached(getString(R.string.txt_navigation_messages));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
