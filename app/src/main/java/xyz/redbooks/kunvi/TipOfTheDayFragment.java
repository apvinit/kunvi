package xyz.redbooks.kunvi;


import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.seismic.ShakeDetector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.SENSOR_SERVICE;


public class TipOfTheDayFragment extends Fragment implements ShakeDetector.Listener {

    String[] tips = {"In a lift with stranger at night, press all the floor number",
            "Don't post anything private on internet",
            "Everything posted on internet remains forever, Beware! while posting.",
            "Carry Pepper spray while travelling!",
            "Learn the basics of martial arts",
            "Never answer door to unknown person",
            "The elbow is the strongest point on your body. If you are close enough to use it, do!",
    };

    TextView tipOfTheDay;
    SensorManager sensorManager;
    ShakeDetector sd;

    public TipOfTheDayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tip_of_the_day, container, false);


        tipOfTheDay = view.findViewById(R.id.tipOfTheDay); //find the TextView
        tipOfTheDay.setText(tips[(int) ( Math.random() * (tips.length))]); //set TextView's text

        DateFormat df = new SimpleDateFormat("EEE d MMM yyyy");
        String date = df.format(Calendar.getInstance().getTime());

        TextView dateAndTime = view.findViewById(R.id.dateAndTime);
        dateAndTime.setText("-- " + date);

        // add sensor Manager instance for using sensor
        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        //add a shake detector in this context
        sd = new ShakeDetector(this);
        //start listening for shake gesture
        sd.start(sensorManager);

        return view;
    }

    @Override
    public void hearShake() {
        tipOfTheDay.setText(tips[(int) ( Math.random() * (tips.length))]);
    }

}
