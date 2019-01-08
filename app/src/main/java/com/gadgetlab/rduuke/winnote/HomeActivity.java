package com.gadgetlab.rduuke.winnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppComponentFactory;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


import com.gadgetlab.rduuke.winnote.adapters.adapterMaterias;
import com.gadgetlab.rduuke.winnote.entity.Calculate;
import com.gadgetlab.rduuke.winnote.entity.MateriasModel;
import com.gadgetlab.rduuke.winnote.entity.Note;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;


public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private ArrayList<MateriasModel> subjectArrayList;
    private RecyclerView recyclerMateria;
    private static final String TAG = "HomeActivity";
    private GoogleSignInAccount account;
    private DatabaseReference materiaFirebase;
    private adapterMaterias adapterMaterias;
    private FloatingActionButton addMateria;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;
        account = GoogleSignIn.getLastSignedInAccount(this);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        materiaFirebase = database.getReference("subject")
                .child(account.getId());

        recyclerMateria = findViewById(R.id.recylerMaterias);
        recyclerMateria.setHasFixedSize(true);

        addMateria = findViewById(R.id.addmateria);

        addMateria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                View view = layoutInflater.inflate(R.layout.add_materia, null);
                final EditText name = view.findViewById(R.id.name_materia);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setView(view)
                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String materiaNombre = name.getText().toString();

                                String raizMateria = null;
                                try {
                                    raizMateria = materiaNombre.replaceAll("^\\s", "") +"-"+ sha1Name(String.valueOf(System.currentTimeMillis()));
                                    HashMap<String, String > map = new HashMap<>();
                                    map.put("name", materiaNombre.toLowerCase());

                                    database.getReference("subject").child(account.getId())
                                            .child(raizMateria.toLowerCase()).setValue(map);
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                }


                            }
                        })
                        .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                Dialog dialog = builder.create();
                dialog.show();
            }
        });

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        recyclerMateria.setLayoutManager(linearLayout);

        ValueEventListener eventMateriaFirebase  = new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subjectArrayList = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    final MateriasModel materia = data.getValue(MateriasModel.class);
                    Log.w("DataSet", materia.getName());
                    final ArrayList<Double> n = new ArrayList<>();
                    final ArrayList<Double> p = new ArrayList<>();
                    for( DataSnapshot notas: data.child("notas").getChildren()) {
                        Note note = notas.getValue(Note.class);
                        n.add(Double.valueOf(note.getNota()));
                        p.add(Double.valueOf(note.getPromedio()));
                    }
                    Calculate calculate = new Calculate(n, p);
                    materia.setNote_final(calculate.currentNote());
                    materia.setNote_necessary(calculate.necessaryNote());
                    materia.setKey(data.getKey());
                    subjectArrayList.add(materia);
                }
                adapterMaterias = new adapterMaterias(getApplicationContext(), subjectArrayList);
                recyclerMateria.setAdapter(adapterMaterias);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        materiaFirebase.addValueEventListener(eventMateriaFirebase);
    }


    @Override
    protected void onStart() {
        super.onStart();
        //apterSubject adapterSubject = new AdapterSubject(dataset, this);
        //mRecyclerView.setAdapter(adapterSubject);
    }


    protected String sha1Name(String txt) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
        byte[] array = messageDigest.digest(txt.getBytes());
        StringBuffer stringBuffer = new StringBuffer();
        for ( int i = 0; i < array.length; i++) {
            stringBuffer.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                    .substring(1, 3));
        }
        return stringBuffer.toString();
    }

}
