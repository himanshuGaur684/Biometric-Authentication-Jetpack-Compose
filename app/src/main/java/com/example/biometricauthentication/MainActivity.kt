package com.example.biometricauthentication

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.biometricauthentication.ui.theme.BiometricAuthenticationTheme

val LocalActivity = compositionLocalOf<FragmentActivity> { error("error") }

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiometricAuthenticationTheme {
                // A surface container using the 'background' color from the theme
                CompositionLocalProvider(LocalActivity provides this) {
                    BiometricAuth()
                }
            }
        }
    }
}

@Composable
fun BiometricAuth() {

    val context = LocalContext.current
    val activity = LocalActivity.current
    val biometricManager = BiometricManager.from(context)

    val resultCode = remember { mutableIntStateOf(Int.MIN_VALUE) }
    val executor = ContextCompat.getMainExecutor(context)

    val launcherIntent = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            when (result.resultCode) {
                1 -> {
                    context.showToast("Enrollment done")
                    resultCode.intValue = 1
                }

                2 -> {
                    context.showToast("Rejected")
                }

                else -> {
                    context.showToast("User cancel the enrollment")
                }
            }
        }
    )

    LaunchedEffect(key1 = resultCode.intValue, block = {
        biometricManager.checkExistence(
            onSuccess = {
                val biometricPromptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Authenticate")
                    .setSubtitle("Authenticate subtitle")
                    .setNegativeButtonText("Cancel")
                    .setAllowedAuthenticators(it).build()

                val biometricPrompt =
                    BiometricPrompt(
                        activity,
                        executor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationError(
                                errorCode: Int,
                                errString: CharSequence
                            ) {
                                super.onAuthenticationError(errorCode, errString)
                                context.showToast("error")
                            }

                            override fun onAuthenticationFailed() {
                                super.onAuthenticationFailed()
                                context.showToast("failed")
                            }

                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                super.onAuthenticationSucceeded(result)
                                context.showToast("success")
                            }
                        })
                val secretKey = generateSecretKey()
                val cipher = cipher(secretKey)
                val cryptObject = BiometricPrompt.CryptoObject(cipher)
                biometricPrompt.authenticate(biometricPromptInfo, cryptObject)

            },
            openSettings = {
                sdkInt(aboveVersion9 = {
                    val intent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BiometricManager.Authenticators.BIOMETRIC_STRONG
                        )
                    }
                    launcherIntent.launch(intent)
                }, belowVersion10 = {
                    val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                    activity.startActivity(intent)
                })
            },
            onError = {
                context.showToast(it)
            })
    })


}



