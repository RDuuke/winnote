package com.gadgetlab.rduuke.winnote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gadgetlab.rduuke.winnote.adapters.adapterNotes;
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

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class DetallesMateriaActivity extends AppCompatActivity {
    TextView nombre, notaFinal, notaParcial, id_materia;
    private GoogleSignInAccount account;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private RecyclerView notesRecycler;
    private DatabaseReference noteFirebase;
    private ArrayList<Note> arrayNotes;
    private FloatingActionButton addnote;
    private MateriasModel materiasModel;
    private Context mContext;
    private ArrayList<Double> arrayListNotes;
    private ArrayList<Double> arrayListPromedios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_materia);
        mContext = this;
        Intent intentBefore = getIntent();
        materiasModel = (MateriasModel) intentBefore.getSerializableExtra("materia");
        account = GoogleSignIn.getLastSignedInAccount(this);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        nombre = findViewById(R.id.materia);
        notaFinal = findViewById(R.id.note_final);
        notaParcial = findViewById(R.id.note_necessary);

        nombre.setText(materiasModel.getName().toUpperCase());

        notesRecycler = findViewById(R.id.recyclerDetailsMateria);
        notesRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        notesRecycler.setLayoutManager(linearLayout);
        noteFirebase = database.getReference("subject").child(account.getId())
                .child(materiasModel.getKey()).child("notas");

        ValueEventListener valueEventNotes = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayNotes = new ArrayList<>();
                arrayListPromedios = new ArrayList<>();
                arrayListNotes = new ArrayList<>();
                for(DataSnapshot notes: dataSnapshot.getChildren()) {
                    Note note = notes.getValue(Note.class);
                    arrayListNotes.add(note.getNota());
                    arrayListPromedios.add(note.getPromedio());
                    note.setKey(notes.getKey());
                    note.setMateria(materiasModel.getKey());
                    arrayNotes.add(note);
                }
                Calculate calculate = new Calculate(arrayListNotes, arrayListPromedios);
                notaFinal.setText("Nota final: "+String.format("%.2f", calculate.currentNote()));
                notaParcial.setText("Nota necesaria: "+String.format("%.2f", calculate.necessaryNote()));
                adapterNotes adapterNotes = new adapterNotes(getApplicationContext(), arrayNotes);
                notesRecycler.setAdapter(adapterNotes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        noteFirebase.addValueEventListener(valueEventNotes);

        addnote = findViewById(R.id.addnota);
        addnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                View view = layoutInflater.inflate(R.layout.add_nota, null);
                final EditText note = view.findViewById(R.id.add_note);
                final EditText promedio = view.findViewById(R.id.add_promedio);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setView(view)
                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Double n = Double.valueOf(note.getText().toString());
                                Double p = Double.valueOf(promedio.getText().toString());
                                if (n > 5 || n < 0) {
                                    Toast.makeText(mContext, "La nota debe ser un valor entre 0 y 5", Toast.LENGTH_LONG)
                                            .show();
                                    dialog.dismiss();
                                } else if (p > 100 || p < 0) {
                                    Toast.makeText(mContext, "el promedio debe ser un valor entre 0 y 100", Toast.LENGTH_LONG)
                                            .show();
                                    dialog.dismiss();
                                } else {
                                    String raizNota;
                                    try {
                                        raizNota = "nota-"+ sha1Name(String.valueOf(System.currentTimeMillis()));
                                        HashMap<String, Double > map = new HashMap<>();
                                        map.put("nota", n);
                                        map.put("promedio", p);
                                        database.getReference("subject").child(account.getId())
                                                .child(materiasModel.getKey())
                                                .child("notas").child(raizNota).setValue(map);
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    }
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
    };

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
