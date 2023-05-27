package com.example.satchelcards;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Locale;

public class WriteNfcActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private String nfcData; // Aquí deberías tener el código NFC guardado en una variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_nfc);

        // Obtener el valor del NFC del Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nfcData = extras.getString("nfcData");
        }

        // Inicializar el adaptador NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Verificar si el dispositivo tiene soporte NFC
        if (nfcAdapter == null || !nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Este dispositivo no tiene soporte NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Habilitar el modo de escritura NFC en primer plano
        enableNfcWriteMode();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Deshabilitar el modo de escritura NFC en primer plano
        disableNfcWriteMode();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Verificar si la intención es para escribir en una etiqueta NFC
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            // Escribir el código NFC en la etiqueta
            writeNfcTag(tag);
        }
    }

    private void enableNfcWriteMode() {
        // Crear una intención para escribir en una etiqueta NFC
        Intent nfcIntent = new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, PendingIntent.FLAG_MUTABLE);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    private void disableNfcWriteMode() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void writeNfcTag(Tag tag) {
        NdefRecord record = createTextRecord(nfcData);
        NdefMessage message = new NdefMessage(new NdefRecord[]{record});

        // Escribir los datos en la etiqueta NFC
        if (writeToNfc(tag, message)) {
            Toast.makeText(this, "Datos escritos en la etiqueta NFC", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al escribir en la etiqueta NFC", Toast.LENGTH_SHORT).show();
        }
    }

    private NdefRecord createTextRecord(String text) {
        byte[] languageBytes = Locale.getDefault().getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = Charset.forName("UTF-8");
        byte[] textBytes = text.getBytes(utfEncoding);

        final int languageLength = languageBytes.length;
        final int textLength = textBytes.length;
        final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageLength + textLength);

        payload.write((byte) (languageLength & 0x1F));
        payload.write(languageBytes, 0, languageLength);
        payload.write(textBytes, 0, textLength);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0],
                payload.toByteArray());
    }

    private boolean writeToNfc(Tag tag, NdefMessage message) {
        try {
            Ndef ndef = Ndef.get(tag);

            if (ndef != null) {
                // La etiqueta es compatible con Ndef, escribir el mensaje
                ndef.connect();
                ndef.writeNdefMessage(message);
                ndef.close();
                return true;
            } else {
                // La etiqueta no es compatible con Ndef, intentar formatearla y escribir el mensaje
                NdefFormatable formatable = NdefFormatable.get(tag);
                if (formatable != null) {
                    formatable.connect();
                    formatable.format(message);
                    formatable.close();
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
