package com.gatetopay.onboardingapplication

import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import com.gatetopay.onboardingapplication.databinding.ActivityAbyanRegistrationBinding
import com.gatetopay.onboardingapplication.view_models.CheckViewModel


class CheckRegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAbyanRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAbyanRegistrationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val model: CheckViewModel by viewModels()
        model.getSedraCheckException().observe(this) {
            if (it != null) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.localizedMessage)
                builder.setPositiveButton("OK") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    model.clearException()
                    finish()
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
        }
    }

/*    override fun onBackPressed() {
       // super.onBackPressed()
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to exit?")
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            finish()
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }*/

    override fun onSupportNavigateUp() =
        findNavController(this, binding.navHostFragment.id).navigateUp()

}
