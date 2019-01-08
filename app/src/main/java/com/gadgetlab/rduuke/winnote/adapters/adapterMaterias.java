package com.gadgetlab.rduuke.winnote.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gadgetlab.rduuke.winnote.DetallesMateriaActivity;
import com.gadgetlab.rduuke.winnote.R;
import com.gadgetlab.rduuke.winnote.entity.MateriasModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class adapterMaterias extends RecyclerView.Adapter<adapterMaterias.MateriasViewHolder> {

    private Context mContext;
    private ArrayList<MateriasModel> materiasModelArrayList;

    public adapterMaterias(Context mContext, ArrayList<MateriasModel> materiasModelArrayList) {
        this.mContext = mContext;
        this.materiasModelArrayList = materiasModelArrayList;
    }

    @NonNull
    @Override
    public MateriasViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).
                inflate(R.layout.materia_row, viewGroup, false);

        return new MateriasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MateriasViewHolder materiasViewHolder, int position) {
        MateriasModel materiasModel = materiasModelArrayList.get(position);
        Log.w("AdapterMateria", materiasModel.getName());
        materiasViewHolder.nombre.setText(materiasModel.getName().toUpperCase());
        materiasViewHolder.notaFinal.setText("Nota final: "+materiasModel.getNoteFinal());
        materiasViewHolder.notaParcial.setText("Nota necesaria: "+materiasModel.getNoteNecessary());
        materiasViewHolder.position.setText(String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        if (materiasModelArrayList.size() > 0 ) {
            return materiasModelArrayList.size();
        }
        return 0;
    }

    public class MateriasViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nombre, notaFinal, notaParcial, position;
        Button eliminar;
        public MateriasViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            nombre = itemView.findViewById(R.id.materia);
            notaFinal = itemView.findViewById(R.id.note_final);
            notaParcial = itemView.findViewById(R.id.note_necessary);
            position = itemView.findViewById(R.id.position);
            eliminar = itemView.findViewById(R.id.eliminar_materia);

            eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    Integer i = Integer.parseInt(position.getText().toString());
                    database.getReference("subject").child(account.getId())
                            .child(materiasModelArrayList.get(i).getKey())
                            .removeValue();
                    Toast.makeText(mContext, "Materia "+ nombre.getText().toString() + " Eliminada", Toast.LENGTH_LONG)
                            .show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            Intent detalleIntent = new Intent(mContext, DetallesMateriaActivity.class);
            MateriasModel materiasModel = materiasModelArrayList.get(Integer.parseInt((String) this.position.getText()));

            detalleIntent.putExtra("materia", materiasModel);

            mContext.startActivity(detalleIntent);
        }
    }
}
