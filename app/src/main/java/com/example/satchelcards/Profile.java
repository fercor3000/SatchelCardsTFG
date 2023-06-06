package com.example.satchelcards;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class Profile extends ClassBlockOrientation {

    //#region VARIABLES
    ImageView gobackBtn;
    Button gestProfile;
    Button cerrarSesion;

    private ImageView helpBtn;

    //#endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        helpBtn = (ImageView) findViewById(R.id.helpBtn);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://drive.google.com/file/d/1QAMsVhlz7bGe04_j_qrhlYvHBaJQBigt/view?usp=sharing";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        //#region OBTIENE ELEMENTOS
        gobackBtn = (ImageView)findViewById(R.id.goBack);
        gestProfile = (Button) findViewById(R.id.gestionar_perfil);
        cerrarSesion = (Button) findViewById(R.id.btnCerrarSesion);
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
                Intent intent = new Intent(Profile.this, GestPerfil.class);
                startActivity(intent);
            }
        });
        //#endregion

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar sesión en FirebaseAuth
                FirebaseAuth.getInstance().signOut();

                // Redirigir a la página de inicio de sesión
                Intent intent = new Intent(Profile.this, Login.class);
                startActivity(intent);
                finish(); // Finaliza la actividad actual para que no se pueda volver atrás
            }
        });

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

                        // Realizar la carga de la imagen después de obtener los datos del usuario
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        String storagePath = "profileUserImages/";
                        String rute_storage_photo = storagePath + user.getUid();
                        StorageReference storageRef = storage.getReference().child(rute_storage_photo);
                        ImageView imageView = findViewById(R.id.profile);

                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(imageView);
                                showPage();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {}
                        });
                    }
                } else { //SI NO SE COMPLETA LA EJECUCIÓN DE LA QUERY...
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Error! No se pudo ejecutar la consulta!", Toast.LENGTH_SHORT).show();
                    atras(0);
                }
            });
            //#endregion

        } else { //SI EL USUARIO ES NULO...
            Context context = getApplicationContext();
            //ERROR USUARIO NO ENCONTRADO
            Toast.makeText(context, "Error!! Usuario no logueado!!", Toast.LENGTH_SHORT).show();
            atras(1);
        }

        //#region RESTABLECER CONTRASEÑA
        Button resetButton = findViewById(R.id.restablecerContraseña);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = firebaseAuth.getCurrentUser().getEmail();
                Context context = getApplicationContext();
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Se ha enviado un correo electrónico para restablecer la contraseña", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "No se pudo enviar el correo electrónico de restablecimiento de contraseña", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
        //#endregion
    }

    //#region FUNCIÓN PARA REDIRECCIONAR
    private void atras(int c) {
        Intent intent = new Intent(Profile.this, HomeMenu.class);
        if (c == 1) {
            intent = new Intent(Profile.this, Login.class);
        }
        startActivity(intent);
    }
    //#endregion

    private void showPage() {
        View mainContainer = findViewById(R.id.mainContainer);
        mainContainer.setVisibility(View.VISIBLE);
    }
}
