package com.gatetopay.onboardingapplication.view_holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
#     #     #   #
#           #  #
# #     #
 * Created by Suhaib Kamal  on 3/12/25
 * skamal@blessedtreeit.com
 * <p>
 * Project Name: AbyanUiApp
 * <p>
 * SEDRA
 */
abstract class BaseHolder(view:View): RecyclerView.ViewHolder(view) {
    abstract fun <T>bind(t: T?);
}