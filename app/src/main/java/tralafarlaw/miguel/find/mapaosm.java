package tralafarlaw.miguel.find;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class mapaosm extends AppCompatActivity {


    LocationManager locationManager;
    LocationListener locationListener;
    MapView map;
    Context ctx;
    IMapController mapDriver;
    Location yo;
    AlertDialog alert = null;
    boolean islogued = true;

    private GoogleApiClient mGoogleApiClient;
    private static final int PLAY_SERVICES_RES_REQUEST = 7172;
    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private LocationRequest mLocationRequest;

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
       /// TextView tv =(TextView) findViewById(R.id.Nombre);
       // tv.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        databaseReference = FirebaseDatabase.getInstance().getReference("/Conductores/"+getIntent().getStringExtra("Name"));
        final FloatingActionButton log = (FloatingActionButton) findViewById(R.id.logout_button);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        map = findViewById(R.id.mapaOSM);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setBuiltInZoomControls(true);
        map.getController().setZoom(18.0);
/*
        final FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.switch_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(islogued){
                    btn.setBackgroundResource(R.drawable.ilogin);

                    islogued = false;
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
                   // init_mapa(false);
                    User user1 = new User(fbuser.getDisplayName(),yo.getLongitude(), yo.getLatitude(),false,"inaranja");

                    databaseReference.child(fbuser.getDisplayName()).child("visible").setValue(false);
                } else{
                    btn.setBackgroundResource(R.drawable.ilogout);
                    //btn.setBackground(getResources().getDrawable(R.drawable.ilogout));
                    islogued = true;
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
                    User user1 = new User(fbuser.getDisplayName(),yo.getLongitude(), yo.getLatitude(),true,"inaranja");
                   // init_mapa(true);
                    databaseReference.child(fbuser.getDisplayName()).child("visible").setValue(true);
                }
            }
        });*/
        final FloatingActionButton fab3 = findViewById(R.id.list_button);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy();
                onBackPressed();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        marcadores();

    }

    public void marcadores (){
        final Marker ini, fin, mk;
        mk  = new Marker(map);
        ini = new Marker(map);
        fin = new Marker(map);

        ini.setPosition(new GeoPoint(getIntent().getDoubleExtra("Lati",1.0), getIntent().getDoubleExtra("Loni", 1.0)));
        fin.setPosition(new GeoPoint(getIntent().getDoubleExtra("Latf",-1.0), getIntent().getDoubleExtra("Lonf", -1.0)));

        ini.setIcon(getResources().getDrawable(R.drawable.pppnaranja));
        fin.setIcon(getResources().getDrawable(R.drawable.pppnaranja));
        mk.setIcon(getResources().getDrawable(R.drawable.pppnaranja));

        ini.setTitle("Inicio");
        fin.setTitle("Fin");
        mk.setTitle("Conductor");

        ini.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        fin.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mk.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);

        map.getOverlays().add(ini);
        map.getOverlays().add(fin);
        map.getOverlays().add(mk);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ini.setPosition(new GeoPoint(getIntent().getDoubleExtra("Lat",0.0), getIntent().getDoubleExtra("Lon", 0.0)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void onBackPressed() {
        onDestroy();
        super.onBackPressed();
    }
}
