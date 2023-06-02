package com.example.satchelcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CardNotes extends AppCompatActivity {

    EditText noteBlock;
    Button btnSaveNotes;
    ImageView goBackBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_notes);

        noteBlock = findViewById(R.id.noteBlock);
        btnSaveNotes = findViewById(R.id.btn_save_notes);

        String itemId = getIntent().getStringExtra("itemId");

        // Cargar nota de la base de datos y rellenar el bloque de notas en el EditText noteBlock
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            DocumentReference cardRef = db.collection("user")
                    .document(email)
                    .collection("loyalty")
                    .document(itemId);

            cardRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String note = document.getString("notes");
                            if (note != null && !note.isEmpty()) {
                                noteBlock.setText(note);
                            }
                        }
                    }
                }
            });
        }

        goBackBtn = (ImageView) findViewById(R.id.go_back);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardNotes.this, SeleccionarGift.class);
                intent.putExtra("itemId",itemId);
                startActivity(intent);
            }
        });

        btnSaveNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = noteBlock.getText().toString();

                // Save the note to Firebase
                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser != null) {
                    String email = currentUser.getEmail();
                    DocumentReference cardRef = db.collection("user")
                            .document(email)
                            .collection("loyalty")
                            .document(itemId);

                    cardRef.update("notes", note)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Nota Guardada correctamente
                                        Context context = getApplicationContext();
                                        Toast.makeText(context, "Notas guardadas", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(CardNotes.this, SeleccionarGift.class);
                                        intent.putExtra("itemId",itemId);
                                        startActivity(intent);
                                    } else {
                                        // Error al guardar nota
                                        Context context = getApplicationContext();
                                        Toast.makeText(context, "Error al guardar notas", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(CardNotes.this, SeleccionarGift.class);
                                        intent.putExtra("itemId",itemId);
                                        startActivity(intent);
                                    }
                                }
                            });
                }
            }
        });
    }
}