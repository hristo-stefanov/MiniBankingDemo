package hristostefanov.minibankingdemo.presentation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DisplaySavingsGoal(
    val id: String,
    val name: String
) : Parcelable