package xyz.redbooks.kunvi;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import xyz.redbooks.kunvi.database.AppDatabase;

public class MyService extends Service {

    static int count = 0;
    private String msg;
    private static BroadcastReceiver mybroadcast;
    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        registerReceiver();
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(mybroadcast);
    }

    private void registerReceiver(){
        mybroadcast = new BroadcastReceiver() {
            //When Event is published, onReceive method is called
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    Log.i("[BroadcastReceiver]", "Screen ON");
                    count++;
                }
                else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    Log.i("[BroadcastReceiver]", "Screen OFF");
                    count++;
                }

                Log.d("Count", Integer.toString(count));

                if(count == 2) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // this code will be executed after 2 seconds
                            count = 0;
                        }
                    }, 4000);
                }

                if(count == 4) {
                    Log.i("[BroadcastReceiver]", "Power button clicked Four times");

                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(400);

                    Location location = getLocation();
                    String longi = Double.toString(location.getLongitude());
                    String lati = Double.toString(location.getLatitude());

                    AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());

                    List<String> mobNumber = db.contactDao().getAllContactsNumber();
                    SmsManager smsManager = SmsManager.getDefault();

                    String gmapLink = "http://maps.google.com/maps?q=" + lati + ","+ longi;
                    msg = "I am here at " + gmapLink + ". I need your help! Get to me Soon.";

                    for(String number : mobNumber) {
                        smsManager.sendTextMessage(number,null, msg, null, null);
                        Log.d("MSG", "sent message to " + number);
                    }

                    // Start a call to first contact
                    Intent call = new Intent(Intent.ACTION_CALL);
                    call.setData(Uri.parse("tel:"+ mobNumber.get(0)));
                    call.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                        //handle it
                    } else{

                        getApplicationContext().startActivity(call);
                    }
                    count = 0;
                }

                // do your stuff with 2 counts(four presses) and set it to 0 again
                // after 3 seconds set it to 0

            }
        };

                /// Register the broadcast receiver
        registerReceiver(mybroadcast, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mybroadcast, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    public Location getLocation(){
        boolean gps_enabled = false;
        boolean network_enabled = false;

        LocationManager lm = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location net_loc = null, gps_loc = null, finalLoc = null;

        if (gps_enabled)
            gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (network_enabled)
            net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (gps_loc != null && net_loc != null) {

            //smaller the number more accurate result will
            if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                finalLoc = net_loc;
            else
                finalLoc = gps_loc;

            // I used this just to get an idea (if both avail, its upto you which you want to take as I've taken location with more accuracy)

        } else {

            if (gps_loc != null) {
                finalLoc = gps_loc;
            } else if (net_loc != null) {
                finalLoc = net_loc;
            }
        }
        return finalLoc;
    }
}
