package com.example.satchelcards;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
/*
public class ListDNI_JD extends AppCompatActivity {

    ImageView gobackBtn;

    Button pulsarTarjetaDniBtn;

    //ScrollView scrollView;
    LinearLayout linearLYActual;
    Boolean noFoto;

    protected void onCreate(Bundle savedInstanceState) {

        // ESTO LO HIZO JD PERO ESTA SIN TERMINAR
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listdni);

        gobackBtn = (ImageView)findViewById(R.id.go_back);

        pulsarTarjetaDniBtn = findViewById(R.id.pulsarTarjetaDni);

        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListDNI_JD.this, LogIn.class);
                startActivity(intent);
            }
        });

        pulsarTarjetaDniBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListDNI_JD.this, seleccionarDni.class);
                startActivity(intent);
            }
        });

        /*
        Context context = getApplicationContext();

        scrollView = findViewById(R.id.scrollView);
        linearLYActual = findViewById(R.id.linearLYActual);

        //OBTENER COLECCIÓN
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        String email = auth.getCurrentUser().getEmail();
        CollectionReference collectionRef = db.collection("/user/" + email + "/dni/");

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            // Se realiza una consulta a una referencia de colección en Firestore y se registra un escuchador para manejar el resultado de la operación.
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) { // Este método se ejecuta cuando se completa la tarea de obtener los documentos de la colección.
                if (task.isSuccessful()) { // Se verifica si la tarea se completó con éxito sin errores.
                    QuerySnapshot querySnapshot = task.getResult(); // Se obtiene el resultado de la tarea, que es un objeto QuerySnapshot que contiene los documentos de la colección.
                    if (querySnapshot != null) { // Se verifica si el QuerySnapshot no es nulo.
                        //POR CADA DOCUMENTO
                        int contador = 0; // Se inicializa una variable contador para realizar un seguimiento del número de documentos procesados.
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) { // Se itera sobre los documentos del QuerySnapshot.
                            if (contador == 2) { // Si el contador es igual a 2, se crea un nuevo LinearLayout y se reinicia el contador. Esto sugiere que cada dos documentos se desea crear un nuevo LinearLayout.
                                linearLYActual = new LinearLayout(context);
                                contador = 0;
                            }
                            contador++; // Se incrementa el contador.
                            ImageView imageView = new ImageView(context); // Se crea una nueva instancia de ImageView.
                            //OBTENER IMG
                            // Se obtiene la instancia de FirebaseStorage y se obtiene la referencia de almacenamiento para la imagen de perfil del usuario actual.
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String storagePath = "profileUserImages/*";
                            String tipoFoto = "profileUserImg";
                            String rute_storage_photo = storagePath + "" + tipoFoto + "" + user.getUid();
                            StorageReference storageRef = storage.getReference().child(rute_storage_photo);

                            noFoto = true; // Se establece la variable noFoto en true, indicando que no hay una foto disponible actualmente.

                            // Se obtiene la URL de descarga de la imagen de perfil del usuario actual desde Firebase Storage utilizando la
                            // referencia de almacenamiento. Si la descarga es exitosa, la imagen se carga en el ImageView utilizando Picasso y se establece noFoto en false.
                            // Si falla la descarga, no se realiza ninguna acción.
                            ImageView finalImageView = imageView;
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(uri).into(finalImageView);
                                    noFoto = false;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //Toast.makeText(context, "Error!! Foto no encontrada!", Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Si noFoto es true, se asigna una imagen de reemplazo al ImageView indicando que el usuario no tiene una imagen de perfil. De lo contrario,
                            // se mantiene la imagen cargada desde Firebase Storage.
                            if (noFoto) { //SI EL USUARIO NO TIENE IMÁGEN
                                imageView = new ImageView(context);
                                imageView.setImageResource(R.drawable.picdnicard);
                            } else {
                                imageView = finalImageView;
                            }
                            // Se crean los parámetros de diseño para el ImageView y se establecen en el ImageView.
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.HORIZONTAL
                            );
                            imageView.setLayoutParams(layoutParams);
                            //AÑADIR ONCLICK
                            // Se establece un escuchador de clics en el ImageView. Cuando se hace clic en el ImageView, se crea un
                            // intento para abrir la clase MostrarUnDNI y se agregan algunos datos extra, como la ruta de almacenamiento
                            // de la foto, el ID del documento y un indicador de si hay o no una foto disponible. Luego se inicia la actividad.
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, MostrarUnDNI.class);

                                    String documentId = document.getId();
                                    intent.putExtra("rute_storage_photo", rute_storage_photo);
                                    intent.putExtra("documentId", document.getId());
                                    intent.putExtra("nofoto", noFoto.toString());

                                    startActivity(intent);
                                }
                            });
                            linearLYActual.addView(imageView); // Se agrega el ImageView al LinearLayout actual.
                        }
                    }
                } else {
                    // Si la tarea no fue exitosa, se muestra un mensaje de error utilizando un Toast.
                    Toast.makeText(context, "Error!! DNI no encontrado!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}*/
