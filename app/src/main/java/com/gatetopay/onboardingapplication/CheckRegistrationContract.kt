package com.gatetopay.onboardingapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.contract.ActivityResultContract
import com.gatetopay.onboardingapplication.models.CheckConfigration
import com.gatetopay.onboardingsdk.exceptions.GateToCheckException

/**
#     #     #   #
#           #  #
# #     #
 * Created by Suhaib Kamal  on 2/25/25
 * skamal@blessedtreeit.com
 * <p>
 * Project Name: abyyan
 * <p>
 * SEDRA
 */
class CheckRegistrationContract: ActivityResultContract<CheckConfigration,AbyyanResult>() {
    override fun createIntent(context: Context, input: CheckConfigration): Intent {
        val intent = Intent(context, CheckRegistrationActivity::class.java)
        intent.putExtra("config", input)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): AbyyanResult {

        intent?.let {
            return if (resultCode == Activity.RESULT_OK) AbyyanResult(
                intent.getStringExtra("GUID"),
                isSuccess = true
            ) else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    AbyyanResult(
                        error = intent.getSerializableExtra(
                            "error",
                            GateToCheckException::class.java
                        )
                    )
                } else {
                    AbyyanResult(error = intent.getSerializableExtra("error") as GateToCheckException)
                }
            }
        }?: return AbyyanResult(error = GateToCheckException("intent is null"))

    }
}

class AbyyanResult(var abyyanId: String?=null, var error: GateToCheckException?=null, var isSuccess: Boolean = false)