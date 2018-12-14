package tralafarlaw.miguel.find;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class List_Track extends AppCompatActivity {
    private DatabaseReference mDatabase;
    List<Items> v ;
    ValueEventListener vl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list__track);
        final RecyclerView rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mDatabase = FirebaseDatabase.getInstance().getReference();
        v= new ArrayList<>();
        vl = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Items> aux = new ArrayList<>();
                for (DataSnapshot dat :dataSnapshot.child("Rutas").getChildren()) {
                    System.out.println(dat.getKey());

                    Items it = new Items(dat.child("Cliente").getValue()+"", dat.child("Conductor").getValue()+"", dat.child("Estado").getValue()+"");


                    aux.add(it);

                }
                rv.setAdapter(new Adaptador(aux,getApplicationContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error");
            }
        };

            mDatabase.addListenerForSingleValueEvent(vl);

        }

}
