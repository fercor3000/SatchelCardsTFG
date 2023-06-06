package com.example.satchelcards;

import android.os.Bundle;
import android.support.annotation.Nullable;

import androidx.biometric.BiometricManager;

public class Fingerprint extends ClassBlockOrientation {
    @Override
    protected void onCreate(@Nullable @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BiometricManager biometricManager = BiometricManager.from(this);
    }
}
