package com.example.satchelcards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Register extends AppCompatActivity {

    //#region VARIABLES
    Button btnCancel, btnRegister, btnCambiarImg;
    EditText username, email, password, telephone;
    ImageView imageViewPhoto;
    private FirebaseAuth mAuth;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    StorageReference storageReference;
    String storagePath = "profileUserImages/*";
    private static final int COD_SEL_STORAGE = 200;
    private static final int COD_SEL_IMAGE = 300;
    private Uri image_url;
    String tipoFoto = "profileUserImg";
    ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> cropImageLauncher;
    //#endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user);

        //#region OBTIENE ELEMENTOS
        mAuth = FirebaseAuth.getInstance();
        btnRegister = (Button)findViewById(R.id.register);
        btnCancel = (Button)findViewById(R.id.cancel);
        btnCambiarImg = (Button) findViewById(R.id.editImage);
        username = (EditText)findViewById(R.id.username);
        email = (EditText)findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        telephone = (EditText) findViewById(R.id.telephone);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        storageReference = FirebaseStorage.getInstance().getReference();
        //#endregion

        //#region IMAGE PICKER
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                image_url = data.getData();
                                startCropActivity(image_url);
                                //Bitmap bitmap = BitmapFactory.decodeFile(image_url.getPath());
                                //imageViewPhoto.setImageBitmap(bitmap);
                            }
                        }
                    }
                });
        //#endregion

        //#region AL PULSAR EDITAR IMAGEN
        btnCambiarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPhoto();
            }
        });
        //#endregion

       //#region AL PULSAR CANCELAR...
       btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //VUELVE A INICIAR SESIÓN
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //#endregion

        //#region AL PULSAR REGISTRAR
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //#region VARIABLES DE REGISTRO
                String usernameSTR = username.getText().toString();
                String emailSTR = email.getText().toString();
                String passwordSTR = password.getText().toString();
                String telephoneSTR = telephone.getText().toString();
                //#endregion

                //COMPRUEBA SI LOS DATOS SON VÁLIDOS
                if((!usernameSTR.equals("")) && (!emailSTR.equals("")) && (!passwordSTR.equals("")) && (passwordSTR.length() >= 6) && (!telephoneSTR.equals("")) && (telephoneSTR.length() == 9)) {
                    //COMPRUEBA EL FORMATO DEL EMAIL
                    if (validarEmail(emailSTR)) {
                        //INSTANCIA FIREBASEAUTH
                        mAuth = FirebaseAuth.getInstance();
                        //BUSCA EL EMAIL EN LA AUTENTIFICACION
                        mAuth.fetchSignInMethodsForEmail(emailSTR).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                if (task.isSuccessful()) {
                                    SignInMethodQueryResult result = task.getResult();
                                    List<String> signInMethods = result.getSignInMethods();

                                    if (signInMethods == null || signInMethods.isEmpty()) { //SI EL CORREO NO EXISTE...
                                        //CREA EL USUARIO
                                        mAuth.createUserWithEmailAndPassword(emailSTR, passwordSTR).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    //GUARDA LOS DATOS EN LA BBDD
                                                    meterdatosenBBDD(usernameSTR, emailSTR, passwordSTR, telephoneSTR);
                                                    //REDIRIGE A LA PÁGINA PRINCIPAL
                                                    Intent intent = new Intent(Register.this, LogIn.class);
                                                    startActivity(intent);
                                                } else { //SI HAY ALGÚN ERROR EN EL REGISTRO...
                                                    Context context = getApplicationContext();
                                                    Toast.makeText(context, "Registro abortado!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else { //SI EL CORREO YA EXISTE...
                                        Context context = getApplicationContext();
                                        Toast.makeText(context, "Este correo electrónico ya está registrado!", Toast.LENGTH_SHORT).show();
                                    }
                                } else { //SI NO SE CONSIGUE BUSCAR EL EMAIL...
                                    Context context = getApplicationContext();
                                    Toast.makeText(context, "Error al comprobar el correo electrónico", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else { //SI EL FORMARTO DEL EMAIL ES INVÁLIDO...
                        Context context = getApplicationContext();
                        Toast.makeText(context, "Error!! Email inválido", Toast.LENGTH_SHORT).show();
                    }
                } else { //SI HAY ALGÚN DATO INVÁLIDO...
                    Context context = getApplicationContext();
                    if (usernameSTR.equals("")) {
                        Toast.makeText(context, "El nombre no puede estar vacío!", Toast.LENGTH_SHORT).show();
                    } else if (emailSTR.equals("") || passwordSTR.equals("")){
                        if((emailSTR.equals(""))) {
                            Toast.makeText(context, "El email no puede estar vacío!", Toast.LENGTH_SHORT).show();
                        } else if (passwordSTR.equals("")) {
                            Toast.makeText(context, "La contraseña no puede estar vacía!", Toast.LENGTH_SHORT).show();
                        }
                    }else if (passwordSTR.length() < 6) {
                        Toast.makeText(context, "La contraseña tiene que tener un tamaño mínimo de 6!", Toast.LENGTH_SHORT).show();
                    } else if (telephoneSTR.equals("")) {
                        Toast.makeText(context, "El teléfono no puede estár vacío!", Toast.LENGTH_SHORT).show();
                    } else if (telephoneSTR.length() != 9) {
                        Toast.makeText(context, "El teléfono tiene que tener un tamaño de 9!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error en los datos!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //#endregion

        //#region CROPIMAGELAUNCHER
        cropImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        // Obtén la imagen recortada de los datos de resultado
                        // Llama a la función subirPhoto con la imagen recortada
                        image_url = data.getData();
                        Bitmap bitmap = BitmapFactory.decodeFile(image_url.getPath());
                        imageViewPhoto.setImageBitmap(bitmap);
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                    }
                }
        );
        //#endregion

    }

    //#region FUNCIÓN UPLOAD PHOTO
    private void uploadPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        imagePickerLauncher.launch(intent);
    }
    //#endregion

    private static final int COD_CROP_IMAGE = 1001;

    //#region FUNCION PARA RECORTAR IMAGEN
    private void startCropActivity(Uri imageUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);

        cropImageLauncher.launch(intent);
    }
    //#endregion

    //#region FUNCIÓN SUBIR PHOTO
    private void subirPhoto(Uri image_url) {
        String rute_storage_photo = storagePath + "" + tipoFoto + "" + mAuth.getUid();
        StorageReference reference = storageReference.child(rute_storage_photo);
        UploadTask uploadTask = reference.putFile(image_url);

        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("photo", downloadUri.toString());
                    reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Obtener la URL de descarga de la imagen subida
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    Toast.makeText(Register.this, "Foto subida exitosamente", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Register.this, "Error al obtener la URL de descarga", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Error al subir la foto", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Error al cargar la foto!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //#endregion

    //#region FUNCIÓN PARA METER DATOS EN LA BBDD
    private void meterdatosenBBDD(String username, String email, String password, String phoneNumber) {
        //OBTIENE LA INSTANCIA
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //GUARDA LA FECHA ACTUAL
        Date tRegister = new Date();

        //GUARDA LA COLECCIÓN
        DocumentReference docRef = db.collection("user").document(email);
        //CREA EL DOCUMENTO Y AÑADE LOS CAMPOS
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("name", username);
        userMap.put("phoneNumber", phoneNumber);
        userMap.put("tRegister", tRegister);

        //AÑADE EL DOCUMENTO A LA COLECCIÓN
        docRef.set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) { //SI TODO VA BIEN
                        //INICIA SESIÓN
                        iniciarSesion(email, password);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { //SI HAY ALGÚN ERROR
                        Context context = getApplicationContext();
                        Toast.makeText(context, "Error al registrar los datos del usuario!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //#endregion

    //#region FUNCIÓN PARA INICIAR SESIÓN
    private void iniciarSesion(String emailSTR, String passwordSTR) {
        //OBTIENE LA INSTANCIA DE FIREBASEAUTH
        mAuth = FirebaseAuth.getInstance();
        //INICIA SESIÓN
        mAuth.signInWithEmailAndPassword(emailSTR,passwordSTR).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Context context = getApplicationContext();
                if (task.isSuccessful()) {
                    Drawable profileDrawable = getResources().getDrawable(R.drawable.profile);
                    Drawable imageViewDrawable = imageViewPhoto.getDrawable();
                    if (imageViewDrawable != null && imageViewDrawable.getConstantState() != null) {
                        if (imageViewDrawable.getConstantState().equals(profileDrawable.getConstantState())) {
                        } else {
                            subirPhoto(image_url);
                        }
                    }
                    Intent intent = new Intent(Register.this, LogIn.class);
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

}