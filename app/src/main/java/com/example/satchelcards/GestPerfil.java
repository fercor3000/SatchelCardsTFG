package com.example.satchelcards;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GestPerfil extends ClassBlockOrientation {

    ImageView gobackBtn, profileImg;
    Button editImage, btn_save;
    private static final int REQUEST_CODE_SELECT_IMAGE = 100;
    private ImageView helpBtn;
    EditText new_name, new_telephone;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestionar_perfil);
        selectedImageUri = null;

        //#region PONER FOTO
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String storagePath = "profileUserImages/";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String rute_storage_photo = storagePath + user.getUid();
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

        profileImg = findViewById(R.id.profile);
        editImage = findViewById(R.id.editImage);
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
            }
        });

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

        gobackBtn = (ImageView)findViewById(R.id.goBack);

        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REDIRIGE
                atras(0);
            }
        });

        new_name  = findViewById(R.id.new_name);
        new_telephone = findViewById(R.id.new_telephone);
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference userCollectionRef = db.collection("user");
                DocumentReference userDocumentRef = userCollectionRef.document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                Map<String, Object> updates = new HashMap<>();
                Boolean modificar = true;
                String nuevoNombre = new_name.getText().toString();
                if (!nuevoNombre.equals("")) {
                    updates.put("name", new_name.getText().toString());
                }
                String nuevoTelefono = new_telephone.getText().toString();
                if (!nuevoTelefono.equals("")) {
                    if (new_telephone.getText().toString().length() != 9) {
                        Toast.makeText(GestPerfil.this, "Numero de teléfono inválido", Toast.LENGTH_SHORT).show();
                        modificar = false;
                    } else {
                        updates.put("phoneNumber", new_telephone.getText().toString());
                    }
                }

                if (modificar) {
                    updates.put("tDataModification", new Date());
                    userDocumentRef.update(updates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if (selectedImageUri != null) {
                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                        StorageReference storageRef = storage.getReference();
                                        StorageReference imagenRef = storageRef.child("profileUserImages/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

                                        UploadTask uploadTask = imagenRef.putFile(selectedImageUri);

                                        uploadTask.addOnSuccessListener(taskSnapshot -> {
                                            Intent intent = new Intent(GestPerfil.this, Profile.class);
                                            startActivity(intent);
                                        });
                                    } else {
                                        Intent intent = new Intent(GestPerfil.this, Profile.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                }
            }
        });
    }
        //#region FUNCIÓN PARA REDIRECCIONAR
        private void atras(int c) {
        Intent intent = new Intent(GestPerfil.this, Profile.class);
        if (c == 1) {
            intent = new Intent(GestPerfil.this, Login.class);
        }
        startActivity(intent);
    }
    //#endregion

    //#region IMAGEN ----
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                profileImg.setImageURI(selectedImageUri);
            }
        }
    }
    //#endregion
}