package com.example.satchelcards;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    //#region VARIABLES
    ImageView gobackBtn;
    ImageView gestProfile;
    //#endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        //#region OBTIENE ELEMENTOS
        gobackBtn = (ImageView)findViewById(R.id.goBack);
        gestProfile = (ImageView)findViewById(R.id.gestionar_perfil);
        //#endregion

        //#region AL PULSAR VOLVER
        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REDIRIGE
                atras(0);
            }
        });
        //#region AL PULSAR VOLVER
        gestProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, ListDNI.class);
                startActivity(intent);
            }
        });
        //#endregion


        String email = null;
        //OBTIENE EL USUARIO ACTUAL
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) { //SI EL USUARIO NO ES NULO...
            email = user.getEmail(); //GUARDA EL MAIL DEL USUARIO
            //INSTANCIA FIRESTORE
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            //INSTANCIA LA COLECCIÓN USER
            CollectionReference collectionRef = db.collection("user");
            //CREA UNA QUERY CUANDO EMAIL SEA IGUAL AL EMAIL DEL USUARIO ACTUAL
            Query query = collectionRef.whereEqualTo("email", email);

            //#region EJECUTAR QUERY
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) { //SI SE COMPLETA LA EJECUCIÓN DE LA QUERY...
                    //OBTIENE EL RESULTADO
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot.isEmpty()) { //SI EL RESULTADO ESTÁ VACÍO...
                        Context context = getApplicationContext();
                        Toast.makeText(context, "Error! No se encontráron los datos del usuario!", Toast.LENGTH_SHORT).show();
                        atras(1);
                    } else { //SI EL RESULTADO NO ESTÁ VACÍO...
                        //OBTIENE EL PRIMER DOCUMENTO
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        //OBTIENE EL NAME
                        String name = documentSnapshot.get("name").toString();
                        //INSTANCIA EL TEXTVIEW
                        TextView tVName = (TextView) findViewById(R.id.emailTextView);
                        //GUARDA EL NAME OBTENIDO EN EL TEXTVIEW
                        tVName.setText(name);
                    }
                } else { //SI NO SE COMPLETA LA EJECUCIÓN DE LA QUERY...
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Error! No se pudo ejecutar la consulta!", Toast.LENGTH_SHORT).show();
                    atras(0);
                }
            });
            //#endregion

            //#region PONER FOTO
            FirebaseStorage storage = FirebaseStorage.getInstance();
            String storagePath = "profileUserImages/*";
            String tipoFoto = "profileUserImg";
            String rute_storage_photo = storagePath + "" + tipoFoto + "" + user.getUid();
            StorageReference storageRef = storage.getReference().child(rute_storage_photo);
            ImageView imageView = findViewById(R.id.profile);

            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(imageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Ocurrió un error al obtener la URL de descarga
                }
            });
            //#endregion

        } else { //SI EL USUARIO ES NULO...
            Context context = getApplicationContext();
            //ERROR USUARIO NO ENCONTRADO
            Toast.makeText(context, "Error!! Usuario no logueado!!", Toast.LENGTH_SHORT).show();
            atras(1);
        }
    }

    //#region FUNCIÓN PARA REDIRECCIONAR
    private void atras(int c) {
        Intent intent = new Intent(Profile.this, LogIn.class);
        if (c == 1) {
            intent = new Intent(Profile.this, MainActivity.class);
        }
        startActivity(intent);
    }
    //#endregion

}
