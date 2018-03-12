package xyz.redbooks.kunvi;

import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.squareup.seismic.ShakeDetector;

public class HomeActivity extends AppCompatActivity implements ShakeDetector.Listener{
    String[] tips = {"In a lift with stranger at night, press all the floor number",
            "This is second quote",
            "This is third quote",
            "This is fourth quote"};

    TextView tipOfTheDay;
    SensorManager sensorManager;
    ShakeDetector sd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tipOfTheDay = findViewById(R.id.tipOfTheDay); //find the TextView
        tipOfTheDay.setText(tips[(int) ( Math.random() * (tips.length))]); //set TextView's text

        // add sensor Manager instance for using sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //add a shake detector in this context
        sd = new ShakeDetector(this);
        //start listening for shake gesture
        sd.start(sensorManager);
    }

    @Override
    public void hearShake() {
        tipOfTheDay.setText(tips[(int) ( Math.random() * (tips.length))]);
    }
}
