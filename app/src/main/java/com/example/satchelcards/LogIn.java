package com.example.satchelcards;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class LogIn extends AppCompatActivity {

    //#region VARIABLES
    ImageView profileBtn, addCardBtn, dnisList, persoList;
    ImageView displayMenuButton;
    private DrawerLayout drawerLayout;
    private TextView menuItem1;
    private TextView menuItem2;
    //#endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_login);

        persoList = (ImageView) findViewById(R.id.CustomCards);
        dnisList = (ImageView) findViewById(R.id.DniCards);
        profileBtn = (ImageView) findViewById(R.id.profile);
        addCardBtn = (ImageButton) findViewById(R.id.addCard);
        displayMenuButton = (ImageView)findViewById(R.id.displayMenu);
        drawerLayout = findViewById(R.id.drawer_layout);
        menuItem1 = findViewById(R.id.menu_item1);
        menuItem2 = findViewById(R.id.menu_item2);

        //#region AL PULSAR LISTA DE DNIS
        dnisList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REDIRIGE A LA PÁGINA DE LISTADO
                Intent intent = new Intent(LogIn.this, ListDNI.class);
                startActivity(intent);
            }
        });
        //#endregion

        persoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REDIRIGE A LA PÁGINA DE LISTADO
                Intent intent = new Intent(LogIn.this, pruebaNFC.class);
                startActivity(intent);
            }
        });



        //#region AL PULSAR PERFIL...
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REDIRIGE A LA PÁGINA DEL PERFIL
                Intent intent = new Intent(LogIn.this, Profile.class);
                startActivity(intent);
            }
        });
        //#endregion

        //#region AL PULSAR AÑADIR TARJETA
        addCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(LogIn.this, addCardBtn);
                popup.getMenuInflater().inflate(R.menu.menu_aniadir_tarjetas, popup.getMenu());



                // Agrega el listener para el menú
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Obtener los valores enteros de los recursos
                        final int aniadirTarjetaPersonalizadaId = getResources().getIdentifier("aniadirtarjetapersonalizada", "id", getPackageName());
                        final int aniadirTarjetaExistenteId = getResources().getIdentifier("aniadirtarjetaexistente", "id", getPackageName());
                        int itemId = item.getItemId();
                        if (itemId == aniadirTarjetaPersonalizadaId) {
                            // Iniciar la actividad correspondiente a "Añadir tarjeta personalizada"
                            Intent intentPersonalizada = new Intent(LogIn.this, addCardPersonalizada.class);
                            startActivity(intentPersonalizada);
                            return true;
                        } else if (itemId == aniadirTarjetaExistenteId) {
                            // Iniciar la actividad correspondiente a "Añadir tarjeta existente"
                            Intent intentExistente = new Intent(LogIn.this, AddCards.class);
                            startActivity(intentExistente);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                popup.show();
            }
        });
        //#endregion

        //#region AL PULSAR EL MENÚ
        menuItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción de añadir tarjeta
                Intent intent = new Intent(LogIn.this, AddCards.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        //#endregion

        //#region MENÚ 2
        menuItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción de eliminar tarjeta
                //drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // Configura el botón para abrir el menú lateral
        displayMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        //#endregion
    }
}
