package com.example.satchelcards;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddCredit extends AppCompatActivity {

    ImageView gobackBtn;
    Button btn_guardar;

    private NfcAdapter nfcAdapter;
    private ImageView nfcLogoImageView;
    private PendingIntent nfcPendingIntent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_credit_card);

        gobackBtn = (ImageView) findViewById(R.id.go_back);
        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCredit.this, AddCards.class);
                startActivity(intent);
            }
        });

        btn_guardar = (Button) findViewById(R.id.btn_save);

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Guardar en la BBDD la tarjeta de crédito
            }
        });

        nfcLogoImageView = findViewById(R.id.nfc_logo);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Crear PendingIntent para la detección de NFC
        Intent nfcIntent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, PendingIntent.FLAG_MUTABLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            // Aquí puedes realizar acciones adicionales con la etiqueta NFC, si es necesario

            // Cambia la imagen a nfc_check.png
            nfcLogoImageView.setImageResource(R.drawable.nfc_check);

            // Muestra un mensaje de éxito
            Toast.makeText(this, "Tarjeta registrada correctamente", Toast.LENGTH_SHORT).show();
        }
    }
}
