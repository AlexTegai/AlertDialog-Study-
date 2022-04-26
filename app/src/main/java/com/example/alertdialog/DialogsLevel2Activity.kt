package com.example.alertdialog

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.example.alertdialog.databinding.ActivityDialogsLevel2Binding
import com.example.alertdialog.databinding.PartVolumeBinding
import com.example.alertdialog.databinding.PartVolumeInputBinding
import com.example.alertdialog.entities.AvailableVolumeValues
import kotlin.properties.Delegates.notNull

class DialogsLevel2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityDialogsLevel2Binding

    private var volume by notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDialogsLevel2Binding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        binding.apply {
            showCustomAlertDialogButton.setOnClickListener {
                showCustomAlertDialog()
            }

            showCustomSingleChoiceAlertDialogButton.setOnClickListener {
                showCustomSingleChoiceAlertDialog()
            }

            showInputAlertDialogButton.setOnClickListener {
                showInputAlertDialog()
            }
        }

        volume = savedInstanceState?.getInt(KEY_VOLUME) ?: 50

        updateUi()
    }

    // ---- Custom Alert Dialog ----
    private fun showCustomAlertDialog() {
        val dialogBinding = PartVolumeBinding.inflate(layoutInflater)
        dialogBinding.volumeSeekBar.progress = volume

        val dialog = AlertDialog.Builder(this)
            .setCancelable(true)
            .setTitle(R.string.volume_setup)
            .setMessage(R.string.volume_setup_message)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.action_confirm) { _, _ ->
                volume = dialogBinding.volumeSeekBar.progress
                updateUi()
            }
            .create()
        dialog.show()
    }

    // ---- Custom Single Choice Alert Dialog ----
    private fun showCustomSingleChoiceAlertDialog() {
        val volumeItems = AvailableVolumeValues.createVolumeValues(volume)
        val adapter = VolumeAdapter(volumeItems.values)

        var volume = this.volume
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.volume_setup)
            .setSingleChoiceItems(adapter, volumeItems.currentIndex) { _, which ->
                volume = adapter.getItem(which)
            }
            .setPositiveButton(R.string.action_confirm) { _, _ ->
                this.volume = volume
                updateUi()
            }
            .create()
        dialog.show()
    }

    // ---- Input Alert Dialog ----
    private fun showInputAlertDialog() {
        val dialogBinding = PartVolumeInputBinding.inflate(layoutInflater)
        dialogBinding.volumeInputEditText.setText(volume.toString())

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.volume_setup)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.action_confirm, null)
            .create()
        dialog.setOnShowListener {
            dialogBinding.volumeInputEditText.requestFocus()
            showKeyBoard(dialogBinding.volumeInputEditText)

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val enteredText = dialogBinding.volumeInputEditText.text.toString()
                if (enteredText.isBlank()) {
                    dialogBinding.volumeInputEditText.error = getString(R.string.empty_value)
                    return@setOnClickListener
                }

                val volume = enteredText.toIntOrNull()
                if (volume == null || volume > 100) {
                    dialogBinding.volumeInputEditText.error = getString(R.string.invalid_value)
                    return@setOnClickListener
                }
                this.volume = volume
                updateUi()
                dialog.dismiss()
            }
        }
        dialog.setOnDismissListener { hideKeyBoard(dialogBinding.volumeInputEditText) }
        dialog.show()
    }

    private fun showKeyBoard(view: View) {
        view.post {
            getInputMethodManager(view).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyBoard(view: View) {
        getInputMethodManager(view).hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getInputMethodManager(view: View): InputMethodManager {
        val context = view.context
        return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private fun updateUi() {
        binding.currentVolumeTextView.text = getString(R.string.current_volume, volume)
    }

    companion object {
        private const val KEY_VOLUME = "KEY_VOLUME"
    }
}

