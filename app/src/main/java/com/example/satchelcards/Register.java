package com.example.satchelcards;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Register extends ClassBlockOrientation {

    //#region VARIABLES
    Button btnCancel, btnRegister, btnCambiarImg;
    EditText username, email, password, telephone;
    ImageView imageViewPhoto;
    private FirebaseAuth mAuth;
    StorageReference storageReference;
    private static final int REQUEST_CODE_SELECT_IMAGE = 100;
    Uri selectedImageUri;
    //#endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user);

        //#region OBTIENE ELEMENTOS
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        mAuth = FirebaseAuth.getInstance();
        btnRegister = (Button) findViewById(R.id.register);
        btnCancel = (Button) findViewById(R.id.cancel);
        btnCambiarImg = (Button) findViewById(R.id.editImage);
        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        telephone = (EditText) findViewById(R.id.telephone);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        storageReference = FirebaseStorage.getInstance().getReference();
        selectedImageUri = null;
        //#endregion

        //#region MOSTRAR CONTRASEÑA
        ImageButton buttonViewPassword = findViewById(R.id.buttonViewPassword);
        final EditText passwordEditText = findViewById(R.id.password);

        buttonViewPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    passwordEditText.setTransformationMethod(null);
                    passwordEditText.setSelection(passwordEditText.getText().length());
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
                    passwordEditText.setSelection(passwordEditText.getText().length());
                }
                return false;
            }
        });
        //#endregion

        btnCambiarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
            }
        });

        //#region AL PULSAR CANCELAR...
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //VUELVE A INICIAR SESIÓN
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });
        //#endregion

        //#region REGISTRAR
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameSTR = username.getText().toString();
                String emailSTR = email.getText().toString().toLowerCase();
                String passwordSTR = password.getText().toString();
                String telephoneSTR = telephone.getText().toString();

                if (usernameSTR.isEmpty() || emailSTR.isEmpty() || passwordSTR.isEmpty() || telephoneSTR.isEmpty()) {
                    Toast.makeText(Register.this, "Por favor rellene todos los campos", Toast.LENGTH_SHORT).show();
                } else if (passwordSTR.length() < 6) {
                    Toast.makeText(Register.this, "La contraseña debe ser al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                } else if (telephoneSTR.length() != 9) {
                    Toast.makeText(Register.this, "Numero de teléfono inválido", Toast.LENGTH_SHORT).show();
                } else {
                    // TODOS LOS CHECKS COMPLETOS, INTENTAMOS REGISTRAR
                    if (validarEmail(emailSTR)) {
                        mAuth.fetchSignInMethodsForEmail(emailSTR).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                if (task.isSuccessful()) {
                                    SignInMethodQueryResult result = task.getResult();
                                    List<String> signInMethods = result.getSignInMethods();

                                    if (signInMethods == null || signInMethods.isEmpty()) {
                                        Intent intent = new Intent(Register.this, LoadingData.class);
                                        startActivity(intent);
                                        mAuth.createUserWithEmailAndPassword(emailSTR.toLowerCase(), passwordSTR).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    if (selectedImageUri != null) {
                                                        StorageReference imageRef = storageReference.child("profileUserImages/" + mAuth.getCurrentUser().getUid());

                                                        UploadTask uploadTask = imageRef.putFile(selectedImageUri);
                                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                //Toast.makeText(Register.this, "Imagen guardada con éxito", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(Register.this, LoadingData.class);
                                                                startActivity(intent);
                                                                meterdatosenBBDD(usernameSTR, emailSTR, passwordSTR, telephoneSTR);
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(Register.this, "Error al guardar imagen", Toast.LENGTH_SHORT).show();
                                                                meterdatosenBBDD(usernameSTR, emailSTR, passwordSTR, telephoneSTR);
                                                            }
                                                        });
                                                    } else {
                                                        meterdatosenBBDD(usernameSTR, emailSTR, passwordSTR, telephoneSTR);
                                                    }
                                                } else {
                                                    Toast.makeText(Register.this, "Registro fallido", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(Register.this, Register.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(Register.this, "este email ya está registrado", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(Register.this, "Error al comprobar email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(Register.this, "Error, email inválido", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //#endregion
    }

    //#region FUNCIÓN PARA METER DATOS EN LA BBDD
    private void meterdatosenBBDD(String username, String email, String password, String phoneNumber) {
        //RECOGE EL ID DISPONIBLE PARA EL USUARIO
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //RECOGE DATOS GENERICOS DE LA APP
        CollectionReference dataRef = db.collection("data");

        dataRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        if (document.exists()) {
                            //RECOGE EL SIGUIENTE USUARIO DISPONIBLE
                            Long userId = document.getLong("nextUserId");

                            //OBTIENE LA INSTANCIA
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            //GUARDA LA FECHA ACTUAL
                            Date tRegister = new Date();

                            //GUARDA LA COLECCIÓN
                            DocumentReference docRef = db.collection("user").document(email.toLowerCase());
                            //CREA EL DOCUMENTO Y AÑADE LOS CAMPOS
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("email", email.toLowerCase());
                            userMap.put("name", username);
                            userMap.put("phoneNumber", phoneNumber);
                            userMap.put("tRegister", tRegister);
                            userMap.put("cardNextId", 1);
                            userMap.put("userId", Long.valueOf(userId));

                            //AÑADE EL DOCUMENTO A LA COLECCIÓN
                            docRef.set(userMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) { //SI TODO VA BIEN
                                            //INICIA SESIÓN
                                            iniciarSesion(email.toLowerCase(), password);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) { //SI HAY ALGÚN ERROR
                                            Context context = getApplicationContext();
                                            Toast.makeText(context, "Error al registrar los datos del usuario!", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            //ACTUALIZA EL VALOR DE nextUserId + 1
                            Long nextUserIdValue = document.getLong("nextUserId");
                            if (nextUserIdValue != null) {
                                long nextUserId = nextUserIdValue + 1;
                                Map<String, Object> updateMap = new HashMap<>();
                                updateMap.put("nextUserId", nextUserId);
                                dataRef.document(document.getId()).update(updateMap);
                            }
                        } else {
                            // Handle non-existent document
                            // Show an error message or take appropriate action
                        }
                    }
                } else {
                    // Handle task exception
                    // Show an error message or take appropriate action
                }
            }
        });
    }
    //#endregion


    //#region FUNCIÓN PARA INICIAR SESIÓN
    private void iniciarSesion(String emailSTR, String passwordSTR) {
        //OBTIENE LA INSTANCIA DE FIREBASEAUTH
        mAuth = FirebaseAuth.getInstance();
        //INICIA SESIÓN
        mAuth.signInWithEmailAndPassword(emailSTR, passwordSTR).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Context context = getApplicationContext();
                if (task.isSuccessful()) {
                    if (selectedImageUri != null) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference imagenRef = storageRef.child("profileUserImages/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

                        UploadTask uploadTask = imagenRef.putFile(selectedImageUri);

                        uploadTask.addOnSuccessListener(taskSnapshot -> {

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Error al cargar la imagen de perfil!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    Toast.makeText(context, "Registro completado!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register.this, HomeMenu.class);
                    intent.putExtra("registro", true);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "Error!! Email o contraseña inválidos!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //#endregion

    //#region FUNCION PARA VALIDAR FORMATO DEL EMAIL
    public boolean validarEmail(String email) {
        String regex = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    //#endregion

    //#region IMAGEN ----
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                selectedImageUri = data.getData();
                imageViewPhoto.setImageURI(selectedImageUri);
            }
        }
    }
    //#endregion
}