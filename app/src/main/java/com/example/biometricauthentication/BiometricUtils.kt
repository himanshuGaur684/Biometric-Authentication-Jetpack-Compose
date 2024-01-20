package com.example.biometricauthentication

import android.os.Build
import androidx.biometric.BiometricManager

inline fun authenticators(aboveVersion9: () -> Int, belowVersion10: () -> Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        aboveVersion9.invoke()
    } else {
        belowVersion10.invoke()
    }
}

inline fun BiometricManager.checkExistence(
    onSuccess: (Int) -> Unit,
    onError: (String) -> Unit,
    openSettings: () -> Unit
) {
    val authenticators = authenticators(aboveVersion9 = {
        BiometricManager.Authenticators.BIOMETRIC_STRONG
    }, belowVersion10 = {
        BiometricManager.Authenticators.BIOMETRIC_WEAK
    })

    when (canAuthenticate(authenticators)) {
        BiometricManager.BIOMETRIC_SUCCESS -> {
            onSuccess.invoke(authenticators)
        }

        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
            onError.invoke("BIOMETRIC_ERROR_HW_UNAVAILABLE")
        }

        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            openSettings.invoke()
        }

        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            onError.invoke("BIOMETRIC_ERROR_NO_HARDWARE")
        }

        BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
            onError.invoke("BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED")
        }

        BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
            onError.invoke("BIOMETRIC_ERROR_UNSUPPORTED")
        }

        BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
            onError.invoke("Something went wrong")
        }
    }
}