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

        try {
        init_mapa(true);
        //empezamos con firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
        User user1 = new User(fbuser.getEmail(),yo.getLongitude(),yo.getLatitude(),true,"pnaranja");
        }catch (Exception e){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            Toast.makeText(getApplicationContext(),"Error de GPS porfavor active la funcin gps e intente de nuevo", Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
        final FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.switch_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(islogued){
                    btn.setBackground(getResources().getDrawable(R.drawable.ilogin));

                    islogued = false;
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
                   // init_mapa(false);
                    User user1 = new User(fbuser.getDisplayName(),yo.getLongitude(), yo.getLatitude(),false,"inaranja");

                    databaseReference.child(fbuser.getDisplayName()).child("visible").setValue(false);
                } else{
                    btn.setBackground(getResources().getDrawable(R.drawable.ilogout));
                    islogued = true;
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
                    User user1 = new User(fbuser.getDisplayName(),yo.getLongitude(), yo.getLatitude(),true,"inaranja");
                   // init_mapa(true);
                    databaseReference.child(fbuser.getDisplayName()).child("visible").setValue(true);
                }
            }
        });






    }
    public void init_mapa(boolean sw){
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
        MyLocationListener mlocListener = new MyLocationListener(sw);
        mlocListener.setMainActivity(this);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,(LocationListener) mlocListener);

        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            yo = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Toast.makeText(getApplicationContext(),""+String.valueOf(yo.getLatitude())+" \n"+String.valueOf(yo.getLongitude()), Toast.LENGTH_SHORT).show();

            GeoPoint starPoint = new GeoPoint(yo.getLatitude(),yo.getLongitude());

            mapDriver.setCenter(starPoint);
            mapDriver.setZoom(17.5);



    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
        marcadores();
        }catch (Exception e){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            Toast.makeText(getApplicationContext(),"Error de GPS porfavor active la funcin gps e intente de nuevo", Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
    }

    public void marcadores (){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ItemizedIconOverlay.OnItemGestureListener<OverlayItem> gestlis = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

                    @Override
                    public boolean onItemLongPress(int arg0, OverlayItem arg1) {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public boolean onItemSingleTapUp(int index, OverlayItem item) {
                        Toast.makeText(
                                getApplicationContext(),
                                item.getSnippet() + "\n" + item.getTitle() + "\n"
                                        + item.getPoint().getLatitude() + " : "
                                        + item.getPoint().getLongitude(),
                                Toast.LENGTH_LONG).show();
                        return true;
                    }

                };
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    //User user = data.getValue(User.class);
                    Marker mk  = new Marker(map);
                    mk.setIcon(getResources().getDrawable(R.drawable.inaranja));
                    mk.setTitle(data.child("email").getValue()+"");
                    mk.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    mk.setPosition(new GeoPoint(Double.valueOf(data.child("lat").getValue()+""), Double.valueOf(""+data.child("lon").getValue())));
                    mk.setVisible(Boolean.parseBoolean(data.child("visible").getValue()+""));
                    map.invalidate();

                    boolean sw = false;
                    for (Overlay o : map.getOverlays()){
                        final Marker aux = (Marker) o ;
                        if(aux.getTitle().equals(mk.getTitle())){
                            Polyline line = new Polyline();
                            List<GeoPoint> v = new ArrayList<>();
                            v.add(aux.getPosition());
                            v.add(mk.getPosition());
                            line.setPoints(v);
                            aux.setVisible(Boolean.parseBoolean(data.child("visible").getValue()+""));
                            aux.setPosition(mk.getPosition());

                            /*inicio de la animacion
                            final double latf = mk.getPosition().getLatitude();
                            final double lonf = mk.getPosition().getLongitude();
                            final double lats = aux.getPosition().getLatitude();
                            final double lons = aux.getPosition().getLongitude();
                            final boolean up, left;
                            if(lats > latf){
                                up = false;
                            }else{
                                up=false;
                            }
                            if(lons > lonf){
                                left = true;
                            }else{
                                left = false;
                            }
                            final double distLat , distLon , constLat, constLon ;

                            distLat = Math.abs(lats-latf);
                            distLon = Math.abs(lons-lonf);

                            constLat = distLat/100;
                            constLon = distLon/100;
                            if(aux.getTitle().equals("test")){
                                Toast.makeText(getApplicationContext(),constLat+" "+constLon,Toast.LENGTH_SHORT).show();}
                            long delay = 1000;
                            final GeoPoint point = aux.getPosition();
                            for (int i = 0; i < 100; i++) {
                                //Thread.sleep(delay);
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Do something after 5s = 5000ms
                                        double e = point.getLatitude(),r = point.getLongitude();
                                        if(!up){
                                            e -= constLat;
                                        }else {
                                            e += constLat;
                                        }
                                        if(left){
                                            r -= constLon;
                                        }else {
                                            r += constLon;
                                        }
                                        point.setLatitude(e);
                                        point.setLongitude(r);
                                        aux.setPosition(point);
                                    }
                                }, delay);
                                //a.setPosition(b.getPosition());
                                // Toast.makeText(getApplicationContext(),"index: "+ i+" \nconstlat = "+constLat+"\ndistlat = "+distLat+"\ne = "+point.getLatitude()+constLat+"\nb = "+point.getLongitude()+constLon,Toast.LENGTH_SHORT).show();

                            }
                           // aux.setPosition(new GeoPoint(latf, lonf));
                            //final de la animacion
*/

                            sw = true;
                        }
                    }
                    if(!sw){

                        map.getOverlays().add(mk);
                    }
                    //map.getController().animateTo((IGeoPoint) mk.getPosition(), 20.4, 5);
                  //  anotherOverlayItemArray.add(new OverlayItem(user.getEmail(),"",new GeoPoint(user.getLat(),user.getLon())));
                }
        //        ItemizedIconOverlay<OverlayItem> overlay = new ItemizedIconOverlay<>(getApplicationContext(),anotherOverlayItemArray, gestlis);
      //          map.getOverlays().add(overlay);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
     public void animar (final Marker a, Marker b) throws InterruptedException {
         final double latf = b.getPosition().getLatitude();
         final double lonf = b.getPosition().getLongitude();

         final double distLat, distLon, constLat, constLon;

         distLat = (a.getPosition().getLatitude() - b.getPosition().getLatitude());
         distLon = (a.getPosition().getLongitude() - b.getPosition().getLongitude());

         constLat = distLat * Math.pow(10, -3);
         constLon = distLon * Math.pow(10, -3);
         if (a.getTitle().equals("test")) {
             //Toast.makeText(getApplicationContext(),constLat+" "+constLon,Toast.LENGTH_SHORT).show();}
             long delay = 10;
             final GeoPoint point = a.getPosition();
             for (int i = 0; i < 1000; i++) {
                 //Thread.sleep(delay);
                 final Handler handler = new Handler();
                 handler.postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         // Do something after 5s = 5000ms
                         double e = point.getLatitude() + constLat;
                         double r = point.getLongitude() + constLon;
                         //      point.setLatitude(e);
                         //       point.setLongitude(r);
                         a.setPosition(new GeoPoint(e, r));
                     }
                 }, delay);
                 //a.setPosition(b.getPosition());
                 // Toast.makeText(getApplicationContext(),"index: "+ i+" \nconstlat = "+constLat+"\ndistlat = "+distLat+"\ne = "+point.getLatitude()+constLat+"\nb = "+point.getLongitude()+constLon,Toast.LENGTH_SHORT).show();
                 if (a.getPosition().equals(b.getPosition())) {
                     return;
                 }
             }
             a.setPosition(new GeoPoint(latf, lonf));
             return;
         }
     }



    @Override
    public void onDestroy(){
        super.onDestroy();

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
