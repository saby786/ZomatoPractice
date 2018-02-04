package com.example.saby7.zomatopractise;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView; //is for displaying restauraunts in row by row fashion
    List<NearbyRestaurant> nearbyRestaurantList= new ArrayList<>();//is used to hold restaurant details in array format
    MyAdapter myAdapter; //this pass restaurant details from arraylist to recycler view
//    MyTask mytask; //a background is created by async task to connect to zomato server get json data
    LinearLayoutManager linearLayoutManager; //used by recyclerview to display vertical list items.
    double latitude;
    double longitude;
    //for getting locations - declare variables
    LocationManager locationManager;
    LocationListener locationListener;
    boolean flag;
    String saby;


    //prepare an inner class for recycler view adapter
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //load row.xml
            View view = getLayoutInflater().inflate(R.layout.row, parent, false);
            ViewHolder viewHolder = new ViewHolder(view); //pass row xml to viewholder
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //get restaurant details based on position
            NearbyRestaurant nearbyRestaurant = nearbyRestaurantList.get(position);
            //first display restaurant image on the imageview
            Glide.with(MainActivity.this).load(nearbyRestaurant.getRestaurant().getThumb()).
                    placeholder(R.mipmap.ic_launcher).crossFade().into(holder.imageView);
            holder.textView1.setText(nearbyRestaurant.getRestaurant().getName()); //show restaraunt name
            holder.textView2.setText(nearbyRestaurant.getRestaurant().getLocation().getLocality()); //show restrnt locality
            holder.textView3.setText(nearbyRestaurant.getRestaurant().getLocation().getAddress()); //show restrnt address
        }

        @Override
        public int getItemCount() {
            return nearbyRestaurantList.size(); //return array list size
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView1, textView2, textView3;
            public LinearLayout linearLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.imageView1);
                textView1 = (TextView) itemView.findViewById(R.id.textView1);
                textView2 = (TextView) itemView.findViewById(R.id.textView2);
                textView3 = (TextView) itemView.findViewById(R.id.textView3);
                linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout1);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        Toast.makeText(MainActivity.this, "CLICKED.." + pos, Toast.LENGTH_SHORT).show();
                        //below is the restaurant where user clicked
                        NearbyRestaurant nearbyRestaurant = nearbyRestaurantList.get(pos);
                        //we have to pass that restaurant details to maps activity - for display
                        Intent i = new Intent(MainActivity.this, MapsActivity.class);
                        i.putExtra("name", nearbyRestaurant.getRestaurant().getName());

                    }
                });
            }
        }
    }

public  void Retrofit() {
    flag = true;
    String baseUrl = "https://developers.zomato.com/api/v2.1/";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    final InterfaceApi request = retrofit.create(InterfaceApi.class);
    Call<Zomato> zomatoCall = request.getZomato(String.valueOf(latitude),String.valueOf(longitude),"8c35b43b80354924682997cff4a22a0b");
zomatoCall.enqueue(new Callback<Zomato>() {
    @Override
    public void onResponse(Call<Zomato> call, Response<Zomato> response) {
        nearbyRestaurantList = response.body().getNearbyRestaurants();

        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(Call<Zomato> call, Throwable t) {

    }
});
}
    //prepare an inner class for async task
//    public class MyTask extends AsyncTask<String, Void, String> {
//        URL url;
//        HttpURLConnection connection;
//        InputStream inputStream;
//        InputStreamReader inputStreamReader;
//        BufferedReader bufferedReader;
//        String line;
//        StringBuilder stringBuilder;
//
//        @Override
//        protected String doInBackground(String... params) {
//            try {
//                url = new URL(params[0]);
//                connection = (HttpURLConnection) url.openConnection();
//                connection.addRequestProperty("user-key", "8c35b43b80354924682997cff4a22a0b");
//                connection.addRequestProperty("Accept", "application/json"); //INTERVIEW QUESTION
//                //end
//                inputStream = connection.getInputStream();
//                inputStreamReader = new InputStreamReader(inputStream);
//                bufferedReader = new BufferedReader(inputStreamReader);
//                //start reading restaurant data from buffer
//                line = bufferedReader.readLine();
//                stringBuilder = new StringBuilder();//if this is not there, crash
//                while (line != null) {
//                    stringBuilder.append(line);
//                    line = bufferedReader.readLine();
//                }
//                //now our string builder contains complete json data , so return to onpost execute
//                return stringBuilder.toString();
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            try {
//                JSONObject j = new JSONObject(s);
//                JSONArray rs = j.getJSONArray("nearby_restaurants");
//                for (int i = 0; i < rs.length(); i++) {
//                    JSONObject x = rs.getJSONObject(i);
//                    JSONObject res = x.getJSONObject("restaurant");
//                    String name = res.getString("name");
//                    JSONObject location = res.getJSONObject("location");
//                    String address = location.getString("address");
//                    String locality = location.getString("locality");
//                    String latitude = location.getString("latitude");
//                    String longitude = location.getString("longitude");
//                    String imageurl = res.getString("thumb");
//                    //now we have to pass these restaurant details to arraylist
//                    //create a restaurant bean class object with above data
//                    Restaurant restaurant = new Restaurant(name, locality, address, imageurl,
//                            latitude, longitude);
//                    //pass restaurant object to arraylist
//                    restaurants.add(restaurant);
//                    myAdapter.notifyDataSetChanged(); //tell to recycler view adapter.
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            super.onPostExecute(s);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //LATER U CAN CHECK IF INTERNET IS AVAILABLE OR NOT - ...connectivity manager

        //initialize all variables in oncreate() method
        recyclerView = (RecyclerView) findViewById(R.id.rc);

        myAdapter = new MyAdapter();
//        mytask = new MyTask();
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //establish link between adapter & layout manager with - recycler view
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        //start async task here
        //remove below code, start async task only once you get your phone location
        //mytask.execute("https://developers.zomato.com/api/v2.1/geocode?lat=12.8984&lon=77.6179");

        //getting phone locations - code START
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //CONTROL COMES HERE ONCE WE GET LATEST LOCATION
                 latitude = location.getLatitude();
                 longitude = location.getLongitude();
                 if(!flag) {
                     Retrofit();
                 }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }
}
