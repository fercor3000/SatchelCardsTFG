package com.example.satchelcards;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;

public class pruebaNFC extends Activity {
    // Declaración de variables
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;

    private boolean writeMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prueba_nfc);

        // Obtener el adaptador NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Comprobar si el dispositivo es compatible con NFC
            Toast.makeText(this, "NFC no está soportado en este dispositivo.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar el PendingIntent para manejar las intenciones NFC
        pendingIntent = PendingIntent.getActivity(this,
                0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);

        // Configurar el filtro de intenciones para las etiquetas NFC detectadas
        IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefIntent.addDataType("*/*");  // Seleccionar todos los tipos MIME
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Error al agregar un tipo MIME.", e);
        }
        intentFiltersArray = new IntentFilter[]{ndefIntent};

        // Configurar los tipos de tecnología NFC que se pueden utilizar
        techListsArray = new String[][]{
                new String[]{Ndef.class.getName()},
                new String[]{NdefFormatable.class.getName()}
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            // Habilitar la detección de etiquetas NFC en primer plano
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (nfcAdapter != null) {
            // Deshabilitar la detección de etiquetas NFC en primer plano
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (writeMode) {
            // Modo escritura NFC
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            writeDataToTag(tag);
        } else {
            // Modo lectura NFC
            String payload = readDataFromTag(intent);
            if (payload != null) {
                Toast.makeText(this, "Datos leídos de la etiqueta NFC: " + payload, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String readDataFromTag(Intent intent) {
        String payload = null;
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef = Ndef.get(tag);
        if (ndef != null) {
            try {
                // Conectar a la etiqueta NFC
                ndef.connect();
                // Obtener el mensaje NDEF de la etiqueta
                NdefMessage ndefMessage = ndef.getNdefMessage();
                if (ndefMessage != null && ndefMessage.getRecords() != null
                        && ndefMessage.getRecords().length > 0) {
                    // Obtener los datos del primer registro del mensaje NDEF
                    payload = new String(ndefMessage.getRecords()[0].getPayload());
                }
                // Cerrar la conexión con la etiqueta NFC
                ndef.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FormatException e) {
                throw new RuntimeException(e);
            }
        }
        return payload;
    }

    private void writeDataToTag(Tag tag) {
        // Crear un mensaje NDEF para escribir en la etiqueta NFC
        NdefMessage ndefMessage = createNdefMessage();
        if (tag != null) {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                try {
                    // Conectar a la etiqueta NFC
                    ndef.connect();
                    // Escribir el mensaje NDEF en la etiqueta
                    ndef.writeNdefMessage(ndefMessage);
                    // Cerrar la conexión con la etiqueta NFC
                    ndef.close();
                    Toast.makeText(this, "Datos escritos en la etiqueta NFC.", Toast.LENGTH_SHORT).show();
                } catch (IOException  e) {
                    e.printStackTrace();
                } catch (FormatException e) {
                    throw new RuntimeException(e);
                }
            } else {
                NdefFormatable ndefFormatable = NdefFormatable.get(tag);
                if (ndefFormatable != null) {
                    try {
                        // Conectar a la etiqueta NFC
                        ndefFormatable.connect();
                        // Formatear la etiqueta para que sea compatible con NDEF
                        ndefFormatable.format(ndefMessage);
                        // Cerrar la conexión con la etiqueta NFC
                        ndefFormatable.close();
                        Toast.makeText(this, "Datos escritos en la etiqueta NFC.", Toast.LENGTH_SHORT).show();
                    } catch (IOException  e) {
                        e.printStackTrace();
                    } catch (FormatException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private NdefMessage createNdefMessage() {
        // Crear el contenido que se escribirá en la etiqueta NFC
        String message = "Datos de la llave NFC de la casa.";
        NdefMessage ndefMessage = new NdefMessage(
                new NdefRecord[]{NdefRecord.createTextRecord(null, message)});
        return ndefMessage;
    }
}

/*COMO USAR LA APP SI SUPUESTAMENTE FUNCIONA

1.Conecta tu dispositivo Android con NFC habilitado al equipo y asegúrate de que tu dispositivo esté en modo de depuración USB.
2.Ejecuta la aplicación en tu dispositivo Android.
3.La aplicación mostrará los botones "Leer NFC" y "Escribir NFC". Toca el botón "Leer NFC" y acerca la etiqueta NFC o
  la llave de tu casa al dispositivo para leer los datos.
4.Si deseas simular la escritura de datos en una etiqueta NFC, toca el botón "Escribir NFC" y
acerca tu dispositivo al lector NFC de tu casa.
Recuerda que este código es solo un ejemplo y puede requerir ajustes para adaptarse a tu caso específico.
Además, ten en cuenta que la funcionalidad real de abrir una puerta mediante una réplica de llave NFC puede
involucrar protocolos de seguridad y autenticación adicionales que no se abordan en este ejemplo.
 */



/*COMENTARIOS DE AYUDA PARA LA COMPRENSION DE NFC

Escribir NFC y leer NFC son dos funcionalidades diferentes que se utilizan en la comunicación con etiquetas o dispositivos NFC:

-Leer NFC: La funcionalidad de lectura NFC permite a tu dispositivo Android leer datos de una etiqueta NFC o de otro
dispositivo compatible con NFC. Puedes usar la lectura NFC para obtener información almacenada en una etiqueta NFC,
como identificadores, texto, URL u otros tipos de datos. Por ejemplo, si tienes una etiqueta NFC programada con información
de contacto, puedes leer esa etiqueta con tu dispositivo y mostrar los datos de contacto en tu aplicación.

-Escribir NFC: La funcionalidad de escritura NFC permite a tu dispositivo Android escribir datos en una etiqueta NFC o en otro
dispositivo compatible con NFC. Puedes usar la escritura NFC para almacenar información en una etiqueta NFC,
como mensajes, URL, registros de eventos, entre otros. Por ejemplo, si deseas compartir un enlace web con otra persona,
puedes escribir el enlace en una etiqueta NFC y luego esa persona puede acercar su dispositivo NFC a la etiqueta para leer y
abrir el enlace.
*/
