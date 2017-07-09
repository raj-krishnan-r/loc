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
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    Location locc = null;
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
            Toast toast = Toast.makeText(getApplicationContext(),"SunInLaw Requires GPS to be on !", Toast.LENGTH_SHORT);
            toast.show();
            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(myIntent);
                    //get gps

        }
        else {
            getLoc(locman);
        }


        final SeekBar birthdaySlider = (SeekBar) findViewById(R.id.zoomLevel);

        birthdaySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                double lat,lng;
                lat = locc.getLatitude();
                lng = locc.getLongitude();
                String latS = String.valueOf(lat);
                String lngS = String.valueOf(lng);
                String zoom = String.valueOf(progress);

                Toast toast = Toast.makeText(getApplicationContext(), "Zooming level : "+zoom, Toast.LENGTH_SHORT);
                toast.show();

                String url = "https://maps.googleapis.com/maps/api/staticmap?center="+latS+","+lngS+"&zoom="+zoom+"&size=400x400&key=AIzaSyBqpM4FCdaD3dz4MuYv1sE3f0iHDYqPBNw";
                new DownloadImageTask((ImageView) findViewById(R.id.mapStatic)).execute(url);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }

    public void getLoc(LocationManager locman) {
        final ImageView imageView = (ImageView) findViewById(R.id.mapStatic);
        final TextView tv = (TextView) findViewById(R.id.tv);

        LocationListener loclis = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locc = location;
                double lat,lng;
                lat = location.getLatitude();
                lng = location.getLongitude();
                String latS = String.valueOf(lat);
                String lngS = String.valueOf(lng);
                Toast toast = Toast.makeText(getApplicationContext(), location.toString(), Toast.LENGTH_SHORT);
                //toast.show();
                try {
                  /*
                    URL url = new URL("https://maps.googleapis.com/maps/api/staticmap?center="+latlng+"&zoom=12&size=400x400&key=AIzaSyBqpM4FCdaD3dz4MuYv1sE3f0iHDYqPBNw");
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    imageView.setImageBitmap(bmp);
*/

                    //URL url = new URL("https://maps.googleapis.com/maps/api/staticmap?center="+latlng+"&zoom=12&size=400x400&key=AIzaSyBqpM4FCdaD3dz4MuYv1sE3f0iHDYqPBNw")
                   /* URL url = new URL("https://maps.googleapis.com/maps/api/staticmap?center="+latlng+"&zoom=12&size=400x400&key=AIzaSyBqpM4FCdaD3dz4MuYv1sE3f0iHDYqPBNw");
                    InputStream content = (InputStream)url.getContent();
                    Drawable d = Drawable.createFromStream(content , "src");
                    imageView.setImageDrawable(d);
*/
                    String url = "https://maps.googleapis.com/maps/api/staticmap?center="+latS+","+lngS+"&zoom=20&size=400x400&key=AIzaSyBqpM4FCdaD3dz4MuYv1sE3f0iHDYqPBNw";
                    Toast toastr = Toast.makeText(getApplicationContext(),url, Toast.LENGTH_LONG);
                   // toastr.show();
                    new DownloadImageTask((ImageView) findViewById(R.id.mapStatic)).execute(url);


                }
                catch(Exception ex)
                {
                    Toast toastr = Toast.makeText(getApplicationContext(),ex.toString(), Toast.LENGTH_LONG);
                    //toastr.show();
                }

                tv.setText(location.toString());


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
                toast.show();
               // tv.setText(s);


            }

            @Override
            public void onProviderEnabled(String s) {
                Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
                toast.show();

            }

            @Override
            public void onProviderDisabled(String s) {
                Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
                toast.show();
            }
        };

        locman.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, loclis);

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