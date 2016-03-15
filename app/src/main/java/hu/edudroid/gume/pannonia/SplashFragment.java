package hu.edudroid.gume.pannonia;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by gume7 on 9/12/2015.
 */
public class SplashFragment extends Fragment {

    public static SplashFragment newInstance() {
        SplashFragment fragment = new SplashFragment();
        return fragment;
    }

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_splash, container, false);
        ImageView image = (ImageView) rootView.findViewById(R.id.imageView);
        try {
            InputStream is = getContext().getAssets().open("pannonia_suli.jpg");
            Drawable d = Drawable.createFromStream(is, null);
            image.setImageDrawable(d);
        } catch (Exception e) {
            Log.e("SplashFragment", "Exception", e);
        }

        return rootView;
    }

}
