package com.example.biometricauthentication

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

fun generateSecretKey(): SecretKey {
    val keyGenerator = KeyGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_AES,
        "AndroidKeyStore"
    )
    keyGenerator.init(
        KeyGenParameterSpec.Builder(
            "dkjfaklfjladfjkla", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(true)
//            .setUserAuthenticationParameters(5000,BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()
    )
    return keyGenerator.generateKey()
}

fun cipher(secretKey: SecretKey): Cipher {
    val cipher = Cipher.getInstance(
        KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7
    )
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    return cipher
}