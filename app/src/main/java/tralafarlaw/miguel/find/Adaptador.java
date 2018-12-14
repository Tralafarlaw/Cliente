package tralafarlaw.miguel.find;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Adaptador extends RecyclerView.Adapter<Adaptador.MyViewHolder> {
    List<Items> v;
    Context context;
    public Adaptador (List<Items> V, Context ctx){
        this.v = V;
        this.context= ctx;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.Nombre.setText(v.get(position).Cliente);
        holder.Estado.setText(v.get(position).Estado);
        holder.Conductor.setText(v.get(position).Conductor);
        final String id = v.get(position)+"";
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent it = new Intent(context,mapaosm.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Rutas/"+id);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        it.putExtra("Lati", dataSnapshot.child("Lati").getValue(Double.class));
                        it.putExtra("Loni", dataSnapshot.child("Loni").getValue(Double.class));
                        it.putExtra("Latf", dataSnapshot.child("Latf").getValue(Double.class));
                        it.putExtra("Lonf", dataSnapshot.child("Lonf").getValue(Double.class));
                        it.putExtra("Name", dataSnapshot.child("Conductor").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("Error","Algo salio mal");
                    }
                });
                context.startActivity(it);
            }
        });

    }

    @Override
    public int getItemCount() {
        return v.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView Nombre, Conductor, Estado;
        CardView cv;
        public MyViewHolder(View itemView) {
            super(itemView);
            Nombre = itemView.findViewById(R.id.txt_nombre);
            Conductor = itemView.findViewById(R.id.txt_conductor);
            Estado = itemView.findViewById(R.id.txt_activo);
            cv = itemView.findViewById(R.id.card_view_rv);
        }
    }

}
