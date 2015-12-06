package com.directions.sample;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.directions.sample.adapter.GPSTracker;
import com.directions.sample.adapter.LocationsContentProvider;
import com.directions.sample.adapter.LocationsDB;
import com.directions.sample.model.AlertDialogManager;
import com.directions.sample.model.SoundManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ibm.bluelist.BlueListApplication;
import com.ibm.bluelist.HistoryTrip;
import com.ibm.bluelist.Item;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import bolts.Continuation;
import bolts.Task;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by RON on 10/15/2015.
 */
public class Map extends Fragment implements RoutingListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private MapView mapView;
    private boolean mapsSupported = true;
    protected GoogleMap map;
    protected LatLng start;
    protected LatLng end;
    @InjectView(R.id.start1)
    AutoCompleteTextView starting;
    @InjectView(R.id.destination1)
    AutoCompleteTextView destination;
    @InjectView(R.id.send2)
    ImageView send;
    private String LOG_TAG = "Map";
    public static final String CLASS_NAME = "Map";
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter mAdapter;
    private ProgressDialog progressDialog;
    private Polyline polyline;
    ArrayList<LatLng> markerPoints;
    private static final LatLngBounds BOUNDS_JAMAICA = new LatLngBounds(new LatLng(16.0429623, 108.1489704),
            new LatLng(16.0578578, 108.1801563));

    List<Item> itemList;
    BlueListApplication blApplication;
    ArrayAdapter<Item> lvArrayAdapter;
    List<HistoryTrip> historyTrips;
    LocationManager locationManager;
    String str, tien;
    Button huy;
    private GPSTracker gps;
    int stt, sttcumtomer;
    boolean noti = true;
    private SoundManager mSoundManager;
    AlertDialogManager alert = new AlertDialogManager();
    Vibrator rung;
    String sokm = "";
    boolean kiemtra= false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this.getActivity());
         /* Use application class to maintain global state. */
        blApplication = (BlueListApplication) getActivity().getApplication();
        itemList = blApplication.getItemList();
        historyTrips = blApplication.getHistoryTrips();
        rung = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getActivity().getBaseContext());
        mSoundManager.addSound(1, R.raw.click);
        mSoundManager.addSound(2, R.raw.warring);

        listItems();
        listHistoryTrip();
        //Toast.makeText(getActivity().getApplicationContext(), "Đăng nhập thành công !Xin chào: " + itemList.get(stt).getName().toString(), Toast.LENGTH_LONG).show();
        stt = getArguments().getInt("stt");
        String ten = itemList.get(stt).getNamecustmer();
        tien = itemList.get(stt).getMessage();
        for (int i = 0; i < itemList.size(); i++)
            if (ten.equals(itemList.get(i).getName()))
                sttcumtomer = i;
        gps = new GPSTracker(this.getActivity());
        if (!gps.canGetLocation())
            gps.showSettingsAlert();

        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        MapsInitializer.initialize(getActivity());
        mGoogleApiClient.connect();


        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        initializeMap();

        mAdapter = new PlaceAutoCompleteAdapter(getActivity(), android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_JAMAICA, null);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1000, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

        //nút phóng to thu nhỏ
        //map.getUiSettings().setZoomControlsEnabled(true); // true to enable
        //lấy gps khi mở wifi
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 5000, 0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

                        map.moveCamera(center);
                        map.animateCamera(zoom);
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
                });
        //lấy gps khi mở 3g
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

                        map.moveCamera(center);
                        map.animateCamera(zoom);

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
                });

          /*
        * Thêm điểm bắt đầu và kết thúc vào autocomplete
        * text views.
        * */
        starting.setAdapter(mAdapter);
        destination.setAdapter(mAdapter);

        /*
        * Sets the start and destination points based on the values selected
        * from the autocomplete text views.
        * */
        starting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(LOG_TAG, "Truy vấn địa điểm không thành công. lỗi: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);

                        start = place.getLatLng();


                    }
                });

            }
        });
        destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(LOG_TAG, "Truy vấn địa điểm không hoàn thành. lỗi: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);


                        end = place.getLatLng();
                    }
                });

            }
        });

        /*
        These text watchers set the start and end points to null because once there's
        * a change after a value has been selected from the dropdown
        * then the value has to reselected from dropdown to get
        * the correct location.
        * */
        starting.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                if (start != null) {
                    start = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        destination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (end != null) {
                    end = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void initializeMap() {
        if (map == null && mapsSupported) {
            mapView = (MapView) getActivity().findViewById(R.id.map1);
            map = mapView.getMap();
            //setup markers etc...
        /*
        * Updates the bounds being used by the auto complete adapter based on the position of the
        * map.
        * */
            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition position) {
                    LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
                    mAdapter.setBounds(bounds);
                }
            });

            // Enable MyLocation Button in the
            // Map
            map.setMyLocationEnabled(true);
            // Invoke LoaderCallbacks to retrieve and draw already saved locations in map
            getActivity().getSupportLoaderManager().initLoader(0, null, this);


            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(16.0568, 108.181));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);

            //map.moveCamera(center);
            map.animateCamera(zoom);
            //starting.setText("hello");


            //goi class comment va truyen du lieu phan biet cac dia diem
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    mSoundManager.playSound(1);
                    //sendRequest2();
                   /* if (kiemtra) {*/

                        String l = marker.getTitle();
                        String diemdau = starting.getText().toString();
                        String diemcuoi = destination.getText().toString();

                        Intent i = new Intent(getActivity(), ActivityComment.class);
                        i.putExtra("drivername", l);
                        i.putExtra("sotien", str);
                        i.putExtra("start", diemdau);
                        i.putExtra("destination", diemcuoi);
                        i.putExtra("stt", stt);
                        i.putExtra("km", sokm);
                        startActivity(i);
                   /* }
                    else {
                        Toast.makeText(getActivity().getApplicationContext(), "Hãy điền vào địa điểm cần đến.", Toast.LENGTH_SHORT).show();
                    }*/
                    return false;
                }
            });
            ThemDiaDiem();
        }
    }

    private void ThemDiaDiem() {
        double vido = 0;
        double kinhdo = 0;
        for (int i = 1; i < itemList.size(); i++) {
            if (itemList.get(i).getQuyen().equalsIgnoreCase("DRIVER")) {
                vido = Double.parseDouble(itemList.get(i).getVido().trim());
                kinhdo = Double.parseDouble(itemList.get(i).getKinhDo().trim());
                LatLng a = new LatLng(vido, kinhdo);
                Marker marker = map.addMarker(new MarkerOptions().position(a).title(itemList.get(i).getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.iconmap)));
            }
            vido = 0;
            kinhdo = 0;
        }


    }

    //xử lý nút send
    //@OnClick(R.id.send2)
    public void sendRequest2() {
        if (Util.Operations.isOnline(this.getActivity())) {
            route();
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            map.animateCamera(zoom);

        } else {
            mSoundManager.playSound(2);
            alert.showAlertDialog(this.getActivity(), "Cảnh báo", "Không có internet, không thể kết nối", false);
            //Toast.makeText(getActivity().getApplicationContext(), "Không có internet, không thể kết nối", Toast.LENGTH_SHORT).show();
        }
    }

    public void route() {
        if (start == null || end == null) {
            if (start == null) {
                if (starting.getText().length() > 0) {
                    starting.setError("Vui lòng chọn địa điểm trong danh sách.");
                    rung.vibrate(500);
                    Toast.makeText(getActivity().getApplicationContext(), "Địa điểm chưa được cập nhật", Toast.LENGTH_SHORT).show();
                } else {
                    rung.vibrate(500);
                    //alert.showAlertDialog(this.getActivity(), "Chú ý!", "Hãy điền vào địa điểm bắt đầu.", false);
                    Toast.makeText(getActivity().getApplicationContext(), "Hãy điền vào địa điểm bắt đầu.", Toast.LENGTH_SHORT).show();
                }
            }
            if (end == null) {
                if (destination.getText().length() > 0) {
                    rung.vibrate(500);
                    destination.setError("Vui lòng chọn địa điểm trong danh sách.");
                    Toast.makeText(getActivity().getApplicationContext(), "Địa điểm chưa được cập nhật", Toast.LENGTH_SHORT).show();
                } else {
                    rung.vibrate(500);
                    //alert.showAlertDialog(this.getActivity(), "Chú ý!", "Hãy điền vào địa điểm cần đến.", false);
                    Toast.makeText(getActivity().getApplicationContext(), "Hãy điền vào địa điểm cần đến.", Toast.LENGTH_SHORT).show();
                }
            }
            kiemtra=false;
        } else {
            progressDialog = ProgressDialog.show(getActivity(), "Please wait...",
                    "Vui lòng đợi giây lát.", true);
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .waypoints(start, end)
                    .build();
            routing.execute();


            //hiển thị số km
            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(start, end);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
            CameraUpdate center = CameraUpdateFactory.newLatLng(start);
            map.moveCamera(center);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            map.animateCamera(zoom);
            kiemtra=true;
        }
    }


    @Override
    public void onRoutingFailure() {
        // The Routing request failed
        progressDialog.dismiss();
        Toast.makeText(getActivity().getApplicationContext(), "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(PolylineOptions mPolyOptions, Route route) {
        progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);

        //map.moveCamera(center);


        if (polyline != null)
            polyline.remove();

        polyline = null;
        //adds route to the map.
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(getResources().getColor(R.color.primary_dark));
        polyOptions.width(10);
        polyOptions.addAll(mPolyOptions.getPoints());
        polyline = map.addPolyline(polyOptions);

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
        map.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
        map.addMarker(options);
    }

    @Override
    public void onRoutingCancelled() {
        Log.i(LOG_TAG, "Routing was cancelled.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.v(LOG_TAG, connectionResult.toString());
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Uri to the content provider LocationsContentProvider
        Uri uri = LocationsContentProvider.CONTENT_URI;

        // Fetches all the rows from locations table
        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int locationCount = 0;
        double lat = 0;
        double lng = 0;
        float zoom = 0;

        // Number of locations available in the SQLite database table
        locationCount = data.getCount();

        // Move the current record pointer to the first row of the table
        data.moveToFirst();

        for (int i = 0; i < locationCount; i++) {

            // Get the latitude
            lat = data.getDouble(data.getColumnIndex(LocationsDB.FIELD_LAT));

            // Get the longitude
            lng = data.getDouble(data.getColumnIndex(LocationsDB.FIELD_LNG));

            // Get the zoom level
            zoom = data.getFloat(data.getColumnIndex(LocationsDB.FIELD_ZOOM));

            // Creating an instance of LatLng to plot the location in Google Maps
            LatLng location = new LatLng(lat, lng);

            // Drawing the marker in the Google Maps
            drawMarker(location);

            // Traverse the pointer to the next row
            data.moveToNext();
        }

        if (locationCount > 0) {
            // Moving CameraPosition to last clicked position
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));

            // Setting the zoom level in the map on last position  is clicked
            map.animateCamera(CameraUpdateFactory.zoomTo(15));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void drawMarker(LatLng point) {
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);

        // Adding marker on the Google Map
        map.addMarker(markerOptions);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //gọi clas LocationAddress để lấy địa chỉ
        /*LocationAddress locationAddress = new LocationAddress();
        locationAddress.getAddressFromLocation(location.getLatitude(), location.getLongitude(),
                getApplicationContext(), new GeocoderHandler());*/
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        map.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);

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

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            markerPoints = null;
            float sotien = 0.0f;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";


            if (result.size() < 1) {
                Toast.makeText(getActivity().getBaseContext(), "Không có địa điểm nào cả", Toast.LENGTH_SHORT).show();
                return;
            }

            // Đi ngang qua tất cả các tuyến đường
            for (int i = 0; i < result.size(); i++) {
                markerPoints = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Lấy đường thứ i
                List<HashMap<String, String>> path = result.get(i);

                // Lấy tất cả các điểm trong đường thứ i
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Nhận khoảng cách từ danh sách
                        distance = (String) point.get("distance");

                        continue;
                    } else if (j == 1) { // Nhận thời gian từ danh sách
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    markerPoints.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(markerPoints);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }
            //distance ="4.9 km"
            //cắt distance để lấy 4.9
            String[] parts = distance.split("k");
            sokm = parts[0];
            //ép kiểu int cho sokm
            sotien = Float.parseFloat(sokm.trim()) * Float.parseFloat(itemList.get(stt).getPRICE());

            //chuyển kiểu int thành string
            str = Float.toString(sotien);

            //alert.showAlertDialog(getActivity(), "Thông báo", "Khoảng Cách:\t" + distance + "\nThời Gian:\t" + duration + "\nSố tiền: " + str + "\tVNĐ", true);
            Toast.makeText(getActivity().getApplicationContext(), "Khoảng Cách: " + distance + ", Thời Gian: " + duration + " Số tiền: " + str + " VNĐ", Toast.LENGTH_LONG).show();


            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);


        }
    }

    private class LocationInsertTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... contentValues) {

            /** Setting up values to insert the clicked location into SQLite database */
            getActivity().getContentResolver().insert(LocationsContentProvider.CONTENT_URI, contentValues[0]);
            return null;
        }
    }

    private class LocationDeleteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            /** Deleting all the locations stored in SQLite database */
            getActivity().getContentResolver().delete(LocationsContentProvider.CONTENT_URI, null, null);
            return null;
        }
    }

    public void listItems() {
        try {
            IBMQuery<Item> query = IBMQuery.queryForClass(Item.class);
            // Query all the Item objects from the server.
            query.find().continueWith(new Continuation<List<Item>, Void>() {

                @Override
                public Void then(Task<List<Item>> task) throws Exception {
                    final List<Item> objects = task.getResult();
                    // Log if the find was cancelled.
                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                    }
                    // Log error message, if the find task fails.
                    else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                    }

                    // If the result succeeds, load the list.
                    else {
                        // Clear local itemList.
                        // We'll be reordering and repopulating from DataService.
                        itemList.clear();
                        for (IBMDataObject item : objects) {
                            itemList.add((Item) item);
                        }
                        sortItems(itemList);
                        lvArrayAdapter.notifyDataSetChanged();
                    }
                    return null;
                }
            }, Task.UI_THREAD_EXECUTOR);

        } catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception : " + error.getMessage());
        }
    }

    private void sortItems(List<Item> theList) {
        // Sort collection by case insensitive alphabetical order.
        Collections.sort(theList, new Comparator<Item>() {
            public int compare(Item lhs,
                               Item rhs) {
                String lhsName = lhs.getName() + lhs.getPass();
                String rhsName = rhs.getName() + lhs.getPass();
                return lhsName.compareToIgnoreCase(rhsName);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final FrameLayout parent = (FrameLayout) inflater.inflate(R.layout.map_layout, container, false);
        mapView = (MapView) parent.findViewById(R.id.map1);


        starting = (AutoCompleteTextView) parent.findViewById(R.id.start1);
        starting.setHintTextColor(getResources().getColor(R.color.primary));
        destination = (AutoCompleteTextView) parent.findViewById(R.id.destination1);
        destination.setHintTextColor(getResources().getColor(R.color.primary));


        huy = (Button) parent.findViewById(R.id.btn_huyyc);
        huy.setVisibility(View.INVISIBLE);
        send = (ImageView) parent.findViewById(R.id.send2);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSoundManager.playSound(1);
                sendRequest2();
            }
        });


        return parent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        initializeMap();
        listItems();
        if (itemList.get(stt).getMessage().trim().equals("0")) {
            noti = false;
        } else if (itemList.get(stt).getMessage().trim().equals("1")) {
            ambao("Yêu cầu bị từ chối", "Vui lòng chọn tài xế khác");
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Thông báo");
            alertDialog.setIcon(R.drawable.icon_fail);
            alertDialog.setMessage("Yêu cầu của bạn đã bị tài xế từ chối! Vui lòng chọn tài xế khác");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mSoundManager.playSound(1);

                            Item item = itemList.get(stt);
                            item.setMessage("0");


                            item.save().continueWith(new Continuation<IBMDataObject, Void>() {

                                @Override
                                public Void then(Task<IBMDataObject> task) throws Exception {
                                    if (task.isCancelled()) {
                                        Log.e(CLASS_NAME, "Exception : " + task.toString() + " was cancelled.");
                                    } else if (task.isFaulted()) {
                                        Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                                    } else {
                                        Intent returnIntent = new Intent();
                                        getActivity().setResult(BlueListApplication.EDIT_ACTIVITY_RC, returnIntent);
                                        getActivity().finish();
                                    }
                                    return null;
                                }

                            }, Task.UI_THREAD_EXECUTOR);


                            //dialog.dismiss();
                            dialog.cancel();
                        }
                    });
            alertDialog.show();
            noti = false;
        } else if (itemList.get(stt).getMessage().trim().equals("2")) {
            ambao("Yêu cầu đã được chấp nhận", "Vui lòng đợi trong giây lát...");

            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Yêu cầu đã được chấp nhận !");
            alertDialog.setIcon(R.drawable.icon_success);
            alertDialog.setMessage("Vui lòng đợi trong giây tài xế sẽ đến đón.\nChúc bạn có chuyến đi vui vẻ! \nChú ý:Nhớ kiểm tra biển số xe trước khi đi");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mSoundManager.playSound(1);

                            Item item = itemList.get(stt);
                            item.setMessage("0");

                            item.save().continueWith(new Continuation<IBMDataObject, Void>() {

                                @Override
                                public Void then(Task<IBMDataObject> task) throws Exception {
                                    if (task.isCancelled()) {
                                        Log.e(CLASS_NAME, "Exception : " + task.toString() + " was cancelled.");
                                    } else if (task.isFaulted()) {
                                        Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                                    } else {
                                        Intent returnIntent = new Intent();
                                        getActivity().setResult(BlueListApplication.EDIT_ACTIVITY_RC, returnIntent);
                                        getActivity().finish();
                                    }
                                    return null;
                                }

                            }, Task.UI_THREAD_EXECUTOR);
                            done();

                            //dialog.dismiss();
                            dialog.cancel();
                        }
                    });
            alertDialog.show();
            noti = false;
        }
        if (noti)
            showNotification();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        listItems();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void showNotification() {
        ambao("Thông báo", "Bạn có một cuốc xe với giá " + itemList.get(stt).getMessage() + " VNĐ");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());

        // set title
        alertDialogBuilder.setTitle("Thực Hiện Chuyến Đi");

        // set dialog message
        alertDialogBuilder
                .setMessage("Bạn có chấp nhận chuyến đi giá " + itemList.get(stt).getMessage() + " VNĐ của " + itemList.get(stt).getNamecustmer())
                .setCancelable(false)
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        mSoundManager.playSound(1);
                        starting.setText(itemList.get(stt).getStart());
                        destination.setText(itemList.get(stt).getDestination());
                        createItem1(stt);
                        createItem1(sttcumtomer);
                        addmoneyfordriver();
                        trutiencustomer();
                        huyyeucau();

                        dialog.dismiss();

                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        guithongbaohuy();
                        Item item1 = itemList.get(stt);

                        item1.setMessage("0");
                        item1.setMoney2("0");
                        item1.setStart("0");
                        item1.setDestination("0");
                        /**
                         * IBMObjectResult is used to handle the response from the server after
                         * either creating or saving an object.
                         *
                         * onResult is called if the object was successfully saved.
                         * onError is called if an error occurred saving the object.
                         */
                        item1.save().continueWith(new Continuation<IBMDataObject, Void>() {

                            @Override
                            public Void then(Task<IBMDataObject> task) throws Exception {
                                if (task.isCancelled()) {
                                    Log.e(CLASS_NAME, "Exception : " + task.toString() + " was cancelled.");
                                } else if (task.isFaulted()) {
                                    Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                                } else {
                                    Intent returnIntent = new Intent();
                                    getActivity().setResult(BlueListApplication.EDIT_ACTIVITY_RC, returnIntent);
                                    getActivity().finish();
                                }
                                return null;
                            }

                        }, Task.UI_THREAD_EXECUTOR);
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void listHistoryTrip() {
        try {
            IBMQuery<HistoryTrip> query = IBMQuery.queryForClass(HistoryTrip.class);
            // Query all the Item objects from the server.
            query.find().continueWith(new Continuation<List<HistoryTrip>, Void>() {
                @Override
                public Void then(Task<List<HistoryTrip>> task) throws Exception {
                    final List<HistoryTrip> objects = task.getResult();
                    // Log if the find was cancelled.
                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                    }
                    // Log error message, if the find task fails.
                    else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                    }

                    // If the result succeeds, load the list.
                    else {
                        // Clear local itemList.
                        // We'll be reordering and repopulating from DataService.
                        historyTrips.clear();
                        for (IBMDataObject item : objects) {
                            historyTrips.add((HistoryTrip) item);
                        }
                        sortItems1(historyTrips);
                        lvArrayAdapter.notifyDataSetChanged();
                    }
                    return null;
                }
            }, Task.UI_THREAD_EXECUTOR);

        } catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception : " + error.getMessage());
        }
    }

    private void sortItems1(List<HistoryTrip> theList) {
        // Sort collection by case insensitive alphabetical order.
        Collections.sort(theList, new Comparator<HistoryTrip>() {
            public int compare(HistoryTrip lhs,
                               HistoryTrip rhs) {
                String lhsName = lhs.getNAME();
                String rhsName = rhs.getNAME();
                return lhsName.compareToIgnoreCase(rhsName);
            }
        });
    }

    public void createItem1(int i) {
        String namecustomer = itemList.get(i).getName();
        String batdau = itemList.get(stt).getStart();
        String ketthuc = itemList.get(stt).getDestination();
        String km=itemList.get(stt).getKm();
        HistoryTrip item = new HistoryTrip();
        if (!tien.equals("") && !namecustomer.equals("") && !batdau.equals("") && !ketthuc.equals("")&& !km.equals("")) {
            item.setNAME(namecustomer);
            item.setSTART(batdau);
            item.setDESTINATION(ketthuc);
            item.setMONEY(tien);
            item.setKm(km);
            // Use the IBMDataObject to create and persist the Item object.
            item.save().continueWith(new Continuation<IBMDataObject, Void>() {

                @Override
                public Void then(Task<IBMDataObject> task) throws Exception {
                    // Log if the save was cancelled.
                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                    }
                    // Log error message, if the save task fails.
                    else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                    }

                    // If the result succeeds, load the list.
                    else {
                        listHistoryTrip();
                    }
                    return null;
                }
            });
        }
    }

    public void addmoneyfordriver() {

        Item item = itemList.get(stt);

        String diemdau = itemList.get(stt).getStart();
        String diemcuoi = itemList.get(stt).getDestination();
        Float sotien = Float.parseFloat(itemList.get(stt).getMoney().trim()) + Float.parseFloat(tien.trim());
        String str = Float.toString(sotien);

        item.setMoney(str);
        item.setMessage("0");
        item.setStart(diemdau);
        item.setDestination(diemcuoi);
        item.setMoney2(tien);
        item.setKm("0");

        /**
         * IBMObjectResult is used to handle the response from the server after
         * either creating or saving an object.
         *
         * onResult is called if the object was successfully saved.
         * onError is called if an error occurred saving the object.
         */
        item.save().continueWith(new Continuation<IBMDataObject, Void>() {

            @Override
            public Void then(Task<IBMDataObject> task) throws Exception {
                if (task.isCancelled()) {
                    Log.e(CLASS_NAME, "Exception : " + task.toString() + " was cancelled.");
                } else if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                } else {
                    Intent returnIntent = new Intent();
                    getActivity().setResult(BlueListApplication.EDIT_ACTIVITY_RC, returnIntent);
                    getActivity().finish();
                }
                return null;
            }

        }, Task.UI_THREAD_EXECUTOR);
    }

    public void trutiencustomer() {

        Item item = itemList.get(sttcumtomer);

        String diemdau = itemList.get(stt).getStart();
        String diemcuoi = itemList.get(stt).getDestination();
        Float sotien = Float.parseFloat(itemList.get(sttcumtomer).getMoney().trim()) - Float.parseFloat(tien.trim());
        String str = Float.toString(sotien);

        item.setMoney(str);
        item.setStart(diemdau);
        item.setDestination(diemcuoi);
        item.setMessage("2");
        item.setMoney2(tien);
        item.setKm("0");

        /**
         * IBMObjectResult is used to handle the response from the server after
         * either creating or saving an object.
         *
         * onResult is called if the object was successfully saved.
         * onError is called if an error occurred saving the object.
         */
        item.save().continueWith(new Continuation<IBMDataObject, Void>() {

            @Override
            public Void then(Task<IBMDataObject> task) throws Exception {
                if (task.isCancelled()) {
                    Log.e(CLASS_NAME, "Exception : " + task.toString() + " was cancelled.");
                } else if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                } else {
                    Intent returnIntent = new Intent();
                    getActivity().setResult(BlueListApplication.EDIT_ACTIVITY_RC, returnIntent);
                    getActivity().finish();
                }
                return null;
            }

        }, Task.UI_THREAD_EXECUTOR);
    }

    public void guithongbaohuy() {
        Item item = itemList.get(sttcumtomer);
        item.setMessage("1");
        item.setMoney2("0");
        item.setStart("0");
        item.setDestination("0");
        item.setKm("0");
        /**
         * IBMObjectResult is used to handle the response from the server after
         * either creating or saving an object.
         *
         * onResult is called if the object was successfully saved.
         * onError is called if an error occurred saving the object.
         */
        item.save().continueWith(new Continuation<IBMDataObject, Void>() {

            @Override
            public Void then(Task<IBMDataObject> task) throws Exception {
                if (task.isCancelled()) {
                    Log.e(CLASS_NAME, "Exception : " + task.toString() + " was cancelled.");
                } else if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                } else {
                    Intent returnIntent = new Intent();
                    getActivity().setResult(BlueListApplication.EDIT_ACTIVITY_RC, returnIntent);
                    getActivity().finish();
                }
                return null;
            }

        }, Task.UI_THREAD_EXECUTOR);
    }

    public void huyyeucau() {
        boolean test = false;
        if (itemList.get(sttcumtomer).getDONE().trim().equals("0")) {
            test = false;
        } else if (itemList.get(sttcumtomer).getDONE().trim().equals("1")) {
            test = true;
        }
        if (test)
            huy.setVisibility(View.INVISIBLE);
        huy.setVisibility(View.VISIBLE);
        huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                // set title
                alertDialogBuilder.setTitle("Cảnh Báo!");

                // set dialog message
                alertDialogBuilder
                        .setIcon(R.drawable.icon_fail)
                        .setMessage("Nếu bạn hủy yêu cầu lúc này phải chịu phạt 10% tổng số tiền của chuyển đi.\nBạn thật sự muốn hủy ?")
                        .setCancelable(false)
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mSoundManager.playSound(1);
                                // if this button is clicked, close
                                // current activity
                                hoantiencustomer();
                                phattien();
                                huy.setVisibility(View.INVISIBLE);
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
    }

    public void hoantiencustomer() {
        Item item = itemList.get(sttcumtomer);

        Float sotien = Float.parseFloat(itemList.get(sttcumtomer).getMoney().trim()) + Float.parseFloat(tien.trim());
        String str = Float.toString(sotien);

        item.setMoney(str);
        item.setStart("0");
        item.setDestination("0");
        item.setMessage("1");
        item.setMoney2("0");
        item.setKm("0");


        item.save().continueWith(new Continuation<IBMDataObject, Void>() {

            @Override
            public Void then(Task<IBMDataObject> task) throws Exception {
                if (task.isCancelled()) {
                    Log.e(CLASS_NAME, "Exception : " + task.toString() + " was cancelled.");
                } else if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                } else {
                    Intent returnIntent = new Intent();
                    getActivity().setResult(BlueListApplication.EDIT_ACTIVITY_RC, returnIntent);
                    getActivity().finish();
                }
                return null;
            }

        }, Task.UI_THREAD_EXECUTOR);
    }

    public void phattien() {
        createItem1(stt);
        Item item = itemList.get(stt);

        Float sotien = Float.parseFloat(itemList.get(stt).getMoney().trim()) - Float.parseFloat(tien.trim()) - (Float.parseFloat(tien.trim()) * 10 / 100);
        String str = Float.toString(sotien);

        item.setMoney(str);
        item.setMessage("0");
        item.setStart("0");
        item.setDestination("0");
        item.setMoney2("0");
        item.setKm("0");


        item.save().continueWith(new Continuation<IBMDataObject, Void>() {

            @Override
            public Void then(Task<IBMDataObject> task) throws Exception {
                if (task.isCancelled()) {
                    Log.e(CLASS_NAME, "Exception : " + task.toString() + " was cancelled.");
                } else if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                } else {
                    Intent returnIntent = new Intent();
                    getActivity().setResult(BlueListApplication.EDIT_ACTIVITY_RC, returnIntent);
                    getActivity().finish();
                }
                return null;
            }

        }, Task.UI_THREAD_EXECUTOR);
    }

    public void done() {
        huy.setVisibility(View.VISIBLE);
        huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                // set title
                alertDialogBuilder.setTitle("Thông Báo!");

                // set dialog message
                alertDialogBuilder
                        .setIcon(R.drawable.icon_success)
                        .setMessage("Chuyến đi của bạn đã hoàn thành")
                        .setCancelable(false)
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mSoundManager.playSound(1);

                                Item item = itemList.get(stt);
                                item.setDone("1");

                                item.save().continueWith(new Continuation<IBMDataObject, Void>() {

                                    @Override
                                    public Void then(Task<IBMDataObject> task) throws Exception {
                                        if (task.isCancelled()) {
                                            Log.e(CLASS_NAME, "Exception : " + task.toString() + " was cancelled.");
                                        } else if (task.isFaulted()) {
                                            Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                                        } else {
                                            Intent returnIntent = new Intent();
                                            getActivity().setResult(BlueListApplication.EDIT_ACTIVITY_RC, returnIntent);
                                            getActivity().finish();
                                        }
                                        return null;
                                    }

                                }, Task.UI_THREAD_EXECUTOR);
                                huy.setVisibility(View.INVISIBLE);
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
    }

    public void ambao(String title, String mess) {
        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // prepare intent which is triggered if the
        // notification is selected

        Intent intent = new Intent(this.getActivity(), Map.class);
        intent.putExtra("stt", stt);
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this.getActivity(), (int) System.currentTimeMillis(), intent, 0);

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n = new Notification.Builder(this.getActivity())
                .setContentTitle(title)
                .setContentText(mess)
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pIntent)
                .setSound(soundUri)
                .setAutoCancel(true)
                .build();


        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        n.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, n);
    }
}