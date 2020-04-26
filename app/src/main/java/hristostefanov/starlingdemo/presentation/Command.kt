package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.LiveData

interface Command {
    val enabledLive: LiveData<Boolean>
    fun execute()
}
