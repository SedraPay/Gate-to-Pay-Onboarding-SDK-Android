package com.gatetopay.onboardingapplication

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gatetopay.onboardingapplication.CheckRegistrationContract
import com.gatetopay.onboardingapplication.models.CheckConfigration

class MainActivity : ComponentActivity() {
    lateinit var abyanLauncher:ActivityResultLauncher<CheckConfigration>

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageUtil.applySavedLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        abyanLauncher = registerForActivityResult(CheckRegistrationContract()) { result ->
            if (result.isSuccess) {
                // handle success
                Toast.makeText(MainActivity@this, result.abyyanId, Toast.LENGTH_SHORT).show()
            }else{
                // handle error
            }


        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
/*        findViewById<Button>(R.id.btnToggleLanguage).text = if (LanguageUtil.getSavedLanguage(this) == "ar") "Change to English" else "Change to Arabic"


        findViewById<Button>(R.id.btnToggleLanguage).setOnClickListener {
            val newLang = LanguageUtil.toggleLanguage(this)
            recreate() // refresh UI with new language
        }*/
        findViewById<Button>(R.id.btnNext).setOnClickListener {
            if(findViewById<EditText>(R.id.etSubKey).text.isEmpty()){
                Toast.makeText(this, "Subscription Key is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(findViewById<EditText>(R.id.etBaseUrl).text.isEmpty()){
                Toast.makeText(this, "Base URL is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            abyanLauncher.launch(CheckConfigration(
                subscriptionKey = findViewById<EditText>(R.id.etSubKey).text.toString(),
                baseUrl = "https://gatetopayintgw.sedrapay.com",
                needOCR = findViewById<SwitchCompat>(R.id.switchOcr).isChecked,
                customerID = findViewById<EditText>(R.id.etCustomerId).text.toString(),
                nationalID = findViewById<EditText>(R.id.etBaseUrl).text.toString(),
                riskID = findViewById<EditText>(R.id.etProductId).text.toString()

            ))}
    }
}