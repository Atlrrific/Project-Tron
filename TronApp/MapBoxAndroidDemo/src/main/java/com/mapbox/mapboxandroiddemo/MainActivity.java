package com.mapbox.mapboxandroiddemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import com.crashlytics.android.Crashlytics;
import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.tileprovider.tilesource.*;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.util.TilesLoadedListener;
import android.location.LocationManager;
import android.location.Criteria;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


import java.util.List;


public class MainActivity extends ActionBarActivity {

    private MapView mv;
	private UserLocationOverlay myLocationOverlay;
	private String currentMap = null;
    public static final String TAG = "GPS CHANGE";
    private static final String URL = "http://104.131.15.54:5000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        setContentView(R.layout.activity_main);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mv = (MapView) findViewById(R.id.mapview);
		mv.setMinZoomLevel(0);
		mv.setMaxZoomLevel(mv.getTileProvider().getMaximumZoomLevel());

		currentMap = getString(R.string.streetMapId);

		// Show user location (purposely not in follow mode)
        mv.setUserLocationEnabled(true);
        mv.setZoom(18);
        mv.getController().setCenter(mv.getUserLocation());

       /* GpsLocationProvider gps = new GpsLocationProvider(this);
        UserLocationOverlay myLocationOverlay = new UserLocationOverlay(gps, mv);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.setDrawAccuracyEnabled(true);
        mv.getOverlays().add(myLocationOverlay);*/

        ///GETTING GPS DATA FROM PHONE SENSOR


        LocationManager locationManager; // initialized elsewhere
        Context mContext =  this;

        locationManager = (LocationManager) getSystemService(mContext.LOCATION_SERVICE);

        LocationListener listener =  new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG,location.toString());

                //execute the POST
                CoordinatesTask coordinatesTask = new CoordinatesTask();
                coordinatesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, location.getLongitude() + "", location.getLatitude() + "", "test_name");
//                coordinatesTask.execute(location.getLongitude() + "", location.getLatitude() + "", "test_name");
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

// This example requests fine accuracy and requires altitude, but
// these criteria could be whatever you want.
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);

        List<String> providers = locationManager.getProviders(
                criteria, true /* enabledOnly */);

        for (String provider : providers) {
            locationManager.requestLocationUpdates(provider, 300,
                    3, listener);
        }

        replaceMapView(getString(R.string.spaceShipMapId));

		mv.loadFromGeoJSONURL("https://gist.githubusercontent.com/tmcw/10307131/raw/21c0a20312a2833afeee3b46028c3ed0e9756d4c/map.geojson");
//        setButtonListeners();
        Marker m = new Marker(mv, "Edinburgh", "Scotland", new LatLng(55.94629, -3.20777));
        m.setIcon(new Icon(this, Icon.Size.SMALL, "marker-stroked", "FF0000"));
        mv.addMarker(m);

        m = new Marker(mv, "Stockholm", "Sweden", new LatLng(59.32995, 18.06461));
        m.setIcon(new Icon(this, Icon.Size.MEDIUM, "city", "FFFF00"));
        mv.addMarker(m);

        m = new Marker(mv, "Prague", "Czech Republic", new LatLng(50.08734, 14.42112));
        m.setIcon(new Icon(this, Icon.Size.LARGE, "land-use", "00FFFF"));
        mv.addMarker(m);

        m = new Marker(mv, "Prague2", "Czech Republic", new LatLng(50.0875, 14.42112));
        m.setIcon(new Icon(getBaseContext(), Icon.Size.LARGE, "land-use", "00FF00"));
        mv.addMarker(m);

        m = new Marker(mv, "Athens", "Greece", new LatLng(37.97885, 23.71399));
        mv.addMarker(m);

        mv.setOnTilesLoadedListener(new TilesLoadedListener() {
            @Override
            public boolean onTilesLoaded() {
                return false;
            }

            @Override
            public boolean onTilesLoadStarted() {
                // TODO Auto-generated method stub
                return false;
            }
        });
//        mv.setVisibility(View.VISIBLE);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
			case R.id.menuItemStreets:
				replaceMapView(getString(R.string.streetMapId));
				return true;
			case R.id.menuItemSatellite:
				replaceMapView(getString(R.string.satelliteMapId));
				return true;
			case R.id.menuItemTerrain:
				replaceMapView(getString(R.string.terrainMapId));
				return true;
			case R.id.menuItemOutdoors:
				replaceMapView(getString(R.string.outdoorsMapId));
				return true;
			case R.id.menuItemWoodcut:
				replaceMapView(getString(R.string.woodcutMapId));
				return true;
			case R.id.menuItemPencil:
				replaceMapView(getString(R.string.pencilMapId));
				return true;
			case R.id.menuItemSpaceship:
				replaceMapView(getString(R.string.spaceShipMapId));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

    protected void replaceMapView(String layer) {

		if (TextUtils.isEmpty(layer) || TextUtils.isEmpty(currentMap) || currentMap.equalsIgnoreCase(layer)) {
			return;
		}

        ITileLayer source;
        BoundingBox box;

        source = new MapboxTileLayer(layer);

        mv.setTileSource(source);
        box = source.getBoundingBox();
        mv.setScrollableAreaLimit(box);
        mv.setMinZoomLevel(mv.getTileProvider().getMinimumZoomLevel());
        mv.setMaxZoomLevel(mv.getTileProvider().getMaximumZoomLevel());
		currentMap = layer;
/*
        mv.setCenter(mv.getTileProvider().getCenterCoordinate());
        mv.setZoom(0);
*/
    }

    private Button changeButtonTypeface(Button button) {
        return button;
    }

    public LatLng getMapCenter() {
        return mv.getCenter();
    }

    public void setMapCenter(ILatLng center) {
        mv.setCenter(center);
    }

    /**
     * Method to show settings  in alert dialog
     * On pressing Settings button will lauch Settings Options - GPS
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getBaseContext());

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getBaseContext().startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private class CoordinatesTask extends AsyncTask<String, String, String>{
        private JSONObject result;

        @Override
        protected String doInBackground(String... params){
            postData(params[0], params[1], params[2]);
            return "";
        }

        protected void onPostExecute(Double result){
            Log.d(TAG, "finished POST request");

        }

        public void postData(String longitude, String latitude, String name){
            DefaultHttpClient client = new DefaultHttpClient();
            JSONObject coordinate_json = new JSONObject();
            Log.d(TAG, "in postData longitude - " + longitude + " latitude - " + latitude + "name - " + name);
            try {
                coordinate_json.put("longitude", longitude);
                coordinate_json.put("latitude", latitude);
                coordinate_json.put("_id", name);
                HttpPost post_request = new HttpPost(URL + "coordinates");
                StringEntity params = new StringEntity(coordinate_json.toString());
                post_request.addHeader("content-type", "application/json");
                post_request.setEntity(params);

                HttpResponse response = client.execute(post_request);
                HttpEntity entity = response.getEntity();
                String entity_string = EntityUtils.toString(entity);

                result = new JSONObject(entity_string);

                Log.d(TAG, "success: " + result.getString("success"));

            } catch (Exception ex) {
                Log.d(TAG, "Exception: " + ex.toString());
            } finally {
                client.getConnectionManager().shutdown();
                Log.d(TAG, "Finished");
            }
        }

    }
}
