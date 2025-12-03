package com.gatetopay.onboardingapplication.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
#     #     #   #
#           #  #
# #     #
 * Created by Suhaib Kamal  on 3/2/25
 * skamal@blessedtreeit.com
 * <p>
 * Project Name: AbyanUiApp
 * <p>
 * SEDRA
 */
data class CheckConfigration(
    @SerializedName("baseUrl") val baseUrl: String,
    @SerializedName("subscriptionKey") val subscriptionKey: String,
    @SerializedName("needOCR") var needOCR: Boolean? = null,
    @SerializedName("customerID") val customerID: String,
    @SerializedName("nationalID") val nationalID: String,
    @SerializedName("riskID") val riskID: String,
):Serializable
