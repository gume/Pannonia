package hu.edudroid.gume.pannonia;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.Calendar;

import hu.edudroid.gume.pannoniaDB.PannoniaDBHelper;
import hu.edudroid.gume.timetableview.TimetableView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment implements TimetableView.CourseChangeListener, TabHost.OnTabChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String name;
    private String omid;

    private int currentTab = 1;
    private TabHost tabs = null;
    private TimetableView ttv[] = { null, null, null };

    private PannoniaDBHelper pDB = PannoniaDBHelper.getInstance();

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_PARAM1);
            omid = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

        tabs = (TabHost) rootView.findViewById(R.id.schedule_tabhost);
        tabs.setup();

        TabHost.TabSpec spec;

        spec = tabs.newTabSpec("tag3"); //Same thing here
        spec.setContent(R.id.tab3);
        spec.setIndicator("Mai");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag4"); //Same thing here
        spec.setContent(R.id.tab4);
        spec.setIndicator("Holnapi");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag5"); //Same thing here
        spec.setContent(R.id.tab5);
        spec.setIndicator("Heti");
        tabs.addTab(spec);

        tabs.setOnTabChangedListener(this);

        ArrayList<ArrayList<String>> heti = pDB.getCourses(name);

        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_WEEK) - 2;
        if (today < 0 || today > 4) {
            today = 4;
        }
        int tomorrow = (today + 1) % 5;

        ArrayList<String> courseList = (ArrayList<String>) pDB.getCourseList();

        ttv[0] = (TimetableView) rootView.findViewById(R.id.sch_table_mai);
        ttv[0].setOneDay(true);
        ttv[0].setToday(today);
        ttv[0].setCourses(heti);
        ttv[0].setCourseList(courseList);
        ttv[0].setCourseChangeListener(this);

        ttv[1] = (TimetableView) rootView.findViewById(R.id.sch_table_holnapi);
        ttv[1].setOneDay(true);
        ttv[1].setToday(tomorrow);
        ttv[1].setCourses(heti);
        ttv[1].setCourseList(courseList);
        ttv[1].setCourseChangeListener(this);

        ttv[2] = (TimetableView) rootView.findViewById(R.id.sch_table_heti);
        ttv[2].setOneDay(false);
        ttv[2].setToday(today);
        ttv[2].setCourses(heti);
        ttv[2].setCourseList(courseList);
        ttv[2].setCourseChangeListener(this);

        tabs.setCurrentTab(currentTab);

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
        ((PannoniaMain) activity).onSectionAttached(getString(R.string.txt_navigation_schedule));
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

    @Override
    public void onCourseChange(int day, int hour, String course) {

        pDB.setCourse(name, day, hour, course);
    }

    @Override
    public void onTabChanged(String arg0) {

        if (arg0.equals("tag3")) {
            ttv[0].setCourses(null);
        }
        else if (arg0.equals("tag4")) {
            ttv[1].setCourses(null);
        }
        else if (arg0.equals("tag5")) {
            ttv[2].setCourses(null);
        }
    }

}
