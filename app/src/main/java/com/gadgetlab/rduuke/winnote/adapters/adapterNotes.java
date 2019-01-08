package com.gadgetlab.rduuke.winnote.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gadgetlab.rduuke.winnote.R;
import com.gadgetlab.rduuke.winnote.entity.Note;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class adapterNotes extends RecyclerView.Adapter<adapterNotes.NotesViewHolder> {
    protected Context mContext;
    protected ArrayList<Note> noteArrayList;

    public adapterNotes(Context mContext, ArrayList<Note> noteArrayList) {
        this.mContext = mContext;
        this.noteArrayList = noteArrayList;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.notes_row, viewGroup, false);

        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder notesViewHolder, int position) {
        Note note = noteArrayList.get(position);
        notesViewHolder.promedio.setText(String.valueOf(note.getPromedio()) + "%");
        notesViewHolder.note.setText(String.valueOf(note.getNota()));
        notesViewHolder.p.setText(String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        if (noteArrayList.size() > 0) {
            return noteArrayList.size();
        }
        return 0;
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView close;
        TextView note, promedio, p;
        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            note = itemView.findViewById(R.id.note);
            promedio = itemView.findViewById(R.id.promedio);
            p = itemView.findViewById(R.id.codigo_materia);
            close = itemView.findViewById(R.id.close);

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    Integer i = Integer.parseInt(p.getText().toString());

                    Note n = noteArrayList.get(i);

                    database.getReference("subject")
                            .child(account.getId())
                            .child(n.getMateria())
                            .child("notas")
                            .child(n.getKey()).removeValue();

                    Toast.makeText(mContext,"Nota eliminada",Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onClick(View v) {

        }
    }

}
