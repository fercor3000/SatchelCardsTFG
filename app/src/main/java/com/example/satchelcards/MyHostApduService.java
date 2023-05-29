package com.example.satchelcards;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

public class MyHostApduService extends HostApduService {

    private static final String TAG = "MyHostApduService";

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        // Procesar el comando APDU recibido y generar una respuesta
        // Aquí puedes implementar tu lógica de emulación de tarjetas NFC

        // Ejemplo: enviar una respuesta de éxito (90 00)

        // Imprimir un mensaje en el Logcat para verificar que se está ejecutando
        Log.d(TAG, "Se ha recibido un comando APDU");

        return "9000".getBytes();
    }

    @Override
    public void onDeactivated(int reason) {
        // Realizar las acciones necesarias cuando el dispositivo deja de estar en modo de emulación
    }
}
