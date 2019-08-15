package us.brisksoft.mobilefun

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener

class LocationActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLocationCallback: LocationCallback? = null

    private var mLocationRequest: LocationRequest? = null
    private var mLastLocation: Location? = null
    private var mMap: GoogleMap? = null

    private val MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 9
    protected var mAddressRequested: Boolean = false
    protected var mAddressOutput: String? = null

    protected var mLatitudeText: TextView? = null
    protected var mLongitudeText: TextView? = null
    protected var mLocationText: TextView? = null

    private var mAddressReceiver: AddressResultReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mAddressRequested = true
        mAddressOutput = ""

        mLatitudeText = findViewById(R.id.textLatitude) as TextView
        mLongitudeText = findViewById(R.id.textLongitude) as TextView
        mLocationText = findViewById(R.id.textLocation) as TextView

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        val mapFragment = fragmentManager
            .findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationCallback()
        getLocation()

    }

    /**
     * Creates a callback for receiving location events.
     */
    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                Log.d("LOCATION", "onLocationResult")
                if (locationResult == null) {
                    return
                }
                for (location in locationResult!!.getLocations()) {
                    mLastLocation = location
                    updateUI()
                }
            }
        }
    }

    fun getLocation() {
        Log.d("LOCATION", "getLocation")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("LOCATION", "permissionGranted")
            mFusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    Log.d("LOCATION", "gotLocation")
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        mLastLocation = location
                        updateUI()
                    }
                }
        } else {
            Log.d("LOCATION", "permissionNotGranted")
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                Log.d("LOCATION", "requestPermission")

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION
                )

            }
        }

    }

//    public fun onConnected(connectionHint: Bundle) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        Log.d("LOCATION", "onRequestPermissionsResult")
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    updateUI()

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // store map object for use once location is available
        mMap = googleMap
        Log.d("LOCATION", "onMapReady")
    }


    fun updateUI() {
        if (mLastLocation == null) {
            // get location updates
            Log.d("LOCATION", "startLocationUpdates")
            startLocationUpdates()
        } else {

            // initiate geocode request
            if (mAddressRequested) {
                startIntentService()
            }

            mLatitudeText?.text = mLastLocation?.latitude.toString()
            mLongitudeText?.text = mLastLocation?.longitude.toString()

            val myLocation = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
            mMap!!.setMinZoomPreference(10.0f) // zoom to city level
            mMap!!.addMarker(
                MarkerOptions().position(myLocation)
                    .title("My current location")
            )
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
        }
    }

    /**
     * Shows a toast with the given text.
     */
    protected fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    protected fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.setInterval(10000)
        mLocationRequest!!.setFastestInterval(5000)
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
    }

    protected fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback, null/* Looper */
            )
        }
    }

    override fun onLocationChanged(location: Location) {
        Log.d("LOCATION", "onLocationChanged")
        mLastLocation = location
        updateUI()
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected fun startIntentService() {
        val intent = Intent(this, FetchAddressIntentService::class.java)
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mAddressReceiver)
        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, mLastLocation)
        startService(intent)
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    internal inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {
        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */

        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            Log.d("receivedResult", resultData.toString())
            if (resultData == null) {
                return
            }

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY)
            Log.d("LOCATION", mAddressOutput!!)
            mLocationText?.text = mAddressOutput

            // Show a toast message if an address was found.
            if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found))
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false
        }
    }


    override fun onConnectionFailed(result: ConnectionResult) {
        showToast("Connection failed.")
    }

    override fun onConnectionSuspended(i: Int) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            showToast("Disconnected. Please re-connect.")
        } else if (i == CAUSE_NETWORK_LOST) {
            showToast("Network lost. Please re-connect.")
        }
    }

    override fun onConnected(p0: Bundle?) {
        // TBD
    }
    override fun onResume() {
        super.onResume()
        //        if (mRequestingLocationUpdates) {
        //            startLocationUpdates();
        //        }
        mAddressReceiver = AddressResultReceiver(Handler())
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

}
