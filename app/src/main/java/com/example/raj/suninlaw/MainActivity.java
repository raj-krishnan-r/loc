package com.example.raj.suninlaw;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    Location locc = null;
    int zooml=15;
    int mapytype = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocationManager locman = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Boolean locStat = false;
        Boolean netStat = false;
        try
        {
            locStat = locman.isProviderEnabled(LocationManager.GPS_PROVIDER);

        }
        catch(Exception e)
        {
        }
        try
        {
            netStat = locman.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        }
        catch(Exception e)
        {

        }
        if(!locStat&&!netStat)
        {
            Toast toast = Toast.makeText(getApplicationContext(),"Mars requires GPS to be on !", Toast.LENGTH_SHORT);
            toast.show();
            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(myIntent);
            getLoc(locman);
                    //get gps

        }
        else {
            getLoc(locman);
        }


        final SeekBar birthdaySlider = (SeekBar) findViewById(R.id.zoomLevel);
        final Switch maptypeSwitch = (Switch) findViewById(R.id.mapType);

        birthdaySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                double lat,lng;
                lat = locc.getLatitude();
                lng = locc.getLongitude();
                String latS = String.valueOf(lat);
                String lngS = String.valueOf(lng);
                String zoom = String.valueOf(progress);
                zooml=progress;

                Toast toast = Toast.makeText(getApplicationContext(), "Zooming level : "+zoom, Toast.LENGTH_SHORT);
                toast.show();
                if(mapytype==1)
                {
                    String url = "https://maps.googleapis.com/maps/api/staticmap?maptype=roadmap&center="+latS+","+lngS+"&zoom="+zoom+"&size=400x400&markers=color:blue|"+latS+","+lngS+"&key=AIzaSyBqpM4FCdaD3dz4MuYv1sE3f0iHDYqPBNw";
                    new DownloadImageTask((ImageView) findViewById(R.id.mapStatic)).execute(url);                }
                else
                {
                    String url = "https://maps.googleapis.com/maps/api/staticmap?maptype=hybrid&center="+latS+","+lngS+"&zoom="+zoom+"&size=400x400&markers=color:blue|"+latS+","+lngS+"&key=AIzaSyBqpM4FCdaD3dz4MuYv1sE3f0iHDYqPBNw";
                    new DownloadImageTask((ImageView) findViewById(R.id.mapStatic)).execute(url);
                }




            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        maptypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    mapytype=2;
                    double lat,lng;
                    lat = locc.getLatitude();
                    lng = locc.getLongitude();
                    String latS = String.valueOf(lat);
                    String lngS = String.valueOf(lng);
                    String url = "https://maps.googleapis.com/maps/api/staticmap?maptype=hybrid&center="+latS+","+lngS+"&zoom="+zooml+"&size=400x400&markers=color:blue|"+latS+","+lngS+"&key=AIzaSyBqpM4FCdaD3dz4MuYv1sE3f0iHDYqPBNw";
                    new DownloadImageTask((ImageView) findViewById(R.id.mapStatic)).execute(url);
                }
                else
                {
                    mapytype=1;
                    double lat,lng;
                    lat = locc.getLatitude();
                    lng = locc.getLongitude();
                    String latS = String.valueOf(lat);
                    String lngS = String.valueOf(lng);
                    String url = "https://maps.googleapis.com/maps/api/staticmap?maptype=roadmap&center="+latS+","+lngS+"&zoom="+zooml+"&size=400x400&markers=color:blue|"+latS+","+lngS+"&key=AIzaSyBqpM4FCdaD3dz4MuYv1sE3f0iHDYqPBNw";
                    new DownloadImageTask((ImageView) findViewById(R.id.mapStatic)).execute(url);
                }
            }
        });


    }

    public void getLoc(LocationManager locman) {
        final ImageView imageView = (ImageView) findViewById(R.id.mapStatic);
        final TextView tv = (TextView) findViewById(R.id.tv);
        final TextView acc = (TextView) findViewById(R.id.accuracy);
        final TextView ti = (TextView) findViewById(R.id.time);
        final TextView sped = (TextView) findViewById(R.id.speed);
        final TextView intStatus = (TextView) findViewById(R.id.intStatus);

        locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {

      if(isNetworkConnected())
      {
          intStatus.setText("");
      }
        else
      {
          intStatus.setText("Can't load maps since Internet not available.");

      }
        locc = location;
        double lat,lng;
        double accuracy;
        long tim;
        float speed;
        accuracy=Math.round(location.getAccuracy());
        tim=location.getTime();
        String timeS = String.valueOf(tim);
        lat = location.getLatitude();
        lng = location.getLongitude();
        speed = location.getSpeed();

        double flat = (lat);
        double flon = (lng);
        String latS = String.valueOf(lat);
        String lngS = String.valueOf(lng);

        String accurate = String.valueOf(accuracy+"meters");

        if(accuracy<50)
        {
            acc.setText("Accuracy : "+accurate +" (Good)");

        }
        else if(accuracy<100)
        {
            acc.setText("Accuracy : "+accurate +" (Good enough)");

        }
        else if(accuracy<500)
        {
            acc.setText("Accuracy : "+accurate +" (Bad)");

        }
        else
        {
            acc.setText("Accuracy : "+accurate +" (Poor)");

        }

        float mps2kph = Math.round(speed*(18/5));
                 String speeder = String.valueOf(mps2kph) + "Kmph";
            sped.setText("Speed "+speeder);

        tv.setText("Coords : "+String.format("%.4f",flat)+","+String.format("%.4f",flon));
        //Toast toast = Toast.makeText(getApplicationContext(), accurate, Toast.LENGTH_LONG);
        //toast.show();


        ti.setText("Regional Time :"+epochTo(timeS));


        //Toast toast = Toast.makeText(getApplicationContext(), location.toString(), Toast.LENGTH_SHORT);
        //toast.show();
        try {


            String zoom = String.valueOf(zooml);

            if(mapytype==1)
            {
                String url = "https://maps.googleapis.com/maps/api/staticmap?maptype=roadmap&center="+latS+","+lngS+"&zoom="+zoom+"&size=400x400&markers=color:blue|"+latS+","+lngS+"&key=AIzaSyBqpM4FCdaD3dz4MuYv1sE3f0iHDYqPBNw";
                new DownloadImageTask((ImageView) findViewById(R.id.mapStatic)).execute(url);                }
            else
            {
                String url = "https://maps.googleapis.com/maps/api/staticmap?maptype=hybrid&center="+latS+","+lngS+"&zoom="+zoom+"&size=400x400&markers=color:blue|"+latS+","+lngS+"&key=AIzaSyBqpM4FCdaD3dz4MuYv1sE3f0iHDYqPBNw";
                new DownloadImageTask((ImageView) findViewById(R.id.mapStatic)).execute(url);
            }



        }
        catch(Exception ex)
        {
            Toast toastr = Toast.makeText(getApplicationContext(),ex.toString(), Toast.LENGTH_LONG);
            //toastr.show();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
});

    }

    public String epochTo(String args) {
        String x = args;
        try {
            DateFormat formatter = new SimpleDateFormat("dd MMMM yyyy hh:mm:ss a");
            long milliSeconds = Long.parseLong(x);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            return(formatter.format(calendar.getTime()));

        }
        catch(Exception e)
        {
            return args;
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


}
 class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;


    public DownloadImageTask(ImageView bmImage) {


        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}