package com.example.satchelcards;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MostrarUnDNI extends AppCompatActivity {

    String nombre;
    String apellidos;
    String sexo;
    String nacionalidad;
    String numSoport;
    String can;
    String dni;
    String equipo;
    String provincia;
    String domicilio;
    String lugarNacimiento;
    String nombrePadre;
    String nombreMadre;
    String DNacimiento;
    String DValidez;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrar_un_dni);

        //#region DATOS
        TextView nombreEditText = findViewById(R.id.dni_nombre);
        TextView apellidosEditText = findViewById(R.id.dni_apellidos);
        TextView sexoEditText = findViewById(R.id.dni_sexo);
        TextView nacionalidadEditText = findViewById(R.id.dni_nacionalidad);
        TextView numSoportEditText = findViewById(R.id.dni_num_soport);
        TextView canEditText = findViewById(R.id.dni_can);
        TextView dniEditText = findViewById(R.id.dni_dni);
        TextView equipoEditText = findViewById(R.id.dni_equipo);
        TextView provinciaEditText = findViewById(R.id.dni_provincia);
        TextView domicilioEditText = findViewById(R.id.dni_domicilio);
        TextView lugarNacimientoEditText = findViewById(R.id.dni_lugar_nacimiento);
        TextView nombrePadreEditText = findViewById(R.id.dni_nombre_padre);
        TextView nombreMadreEditText = findViewById(R.id.dni_nombre_madre);
        TextView fechaNacimiento = findViewById(R.id.dni_FechaNacimiento);
        TextView fechaValidez = findViewById(R.id.dni_FechaValidez);

        // Obtener los valores del intent
        String documentId = getIntent().getStringExtra("documentId");
        String rute_storage_photo = getIntent().getStringExtra("rute_storage_photo");
        String noFoto = getIntent().getStringExtra("nofoto");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        String email = auth.getCurrentUser().getEmail();
        CollectionReference collectionRef = db.collection("/user/" + email + "/dni/");

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                  if (task.isSuccessful()) {
                      QuerySnapshot querySnapshot = task.getResult();
                      if (querySnapshot != null) {
                          DocumentSnapshot dS = querySnapshot.getDocuments().get(0);
                          nombre = dS.get("nombre").toString();
                          apellidos = dS.get("apellidos").toString();
                          sexo = dS.get("sexo").toString();
                          nacionalidad = dS.get("nacionalidad").toString();
                          numSoport = dS.get("numSoport").toString();
                          can = dS.get("can").toString();
                          dni = dS.get("dni").toString();
                          equipo = dS.get("equipo").toString();
                          provincia = dS.get("provincia").toString();
                          domicilio = dS.get("domicilio").toString();
                          lugarNacimiento = dS.get("lugarNacimiento").toString();
                          nombrePadre = dS.get("nombrePadre").toString();
                          nombreMadre = dS.get("nombreMadre").toString();
                          DNacimiento = dS.get("DNacimiento").toString();
                          DValidez = dS.get("DValidez").toString();
                      }
                  }
              }
          });

         nombreEditText.setText(nombre);
         apellidosEditText.setText(apellidos);
         sexoEditText.setText(sexo);
         nacionalidadEditText.setText(nacionalidad);
         numSoportEditText.setText(numSoport);
         canEditText.setText(can);
         dniEditText.setText(dni);
         equipoEditText.setText(equipo);
         provinciaEditText.setText(provincia);
         domicilioEditText.setText(domicilio);
         lugarNacimientoEditText.setText(lugarNacimiento);
         nombrePadreEditText.setText(nombrePadre);
         nombreMadreEditText.setText(nombreMadre);
         fechaNacimiento.setText(DNacimiento);
         fechaValidez.setText(DValidez);
        //#endregion
    }
}
