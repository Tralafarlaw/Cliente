package tralafarlaw.miguel.find;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class mapaosm extends AppCompatActivity {

    LocationManager locationManager;
    MapView map;
    Context ctx;
    IMapController mapDriver;
    Location yo;

    //database firbase
    private DatabaseReference databaseReference;

    public void setLocation(Location loc){
        this.yo = loc;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_mapaosm);
        TextView tv =(TextView) findViewById(R.id.Nombre);
//        tv.setText(getIntent().getExtras().getString("Nombre"));
        init_mapa();
        //empezamos con firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
        User user1 = new User(fbuser.getEmail(),yo.getLongitude(),yo.getLatitude());







    }
    public void init_mapa(){
        map = (MapView) findViewById(R.id.mapaOSM);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        mapDriver = map.getController();


        ActivityCompat.requestPermissions(mapaosm.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(),"No se dieron Permisos",Toast.LENGTH_SHORT).show();
        }


        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener mlocListener = new MyLocationListener();
        mlocListener.setMainActivity(this);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,(LocationListener) mlocListener);

        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        yo = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        Toast.makeText(getApplicationContext(),""+String.valueOf(yo.getLatitude())+" "+String.valueOf(yo.getLongitude()), Toast.LENGTH_SHORT).show();

        GeoPoint starPoint = new GeoPoint(yo.getLatitude(),yo.getLongitude());

        mapDriver.setCenter(starPoint);
        mapDriver.setZoom(15.0);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
