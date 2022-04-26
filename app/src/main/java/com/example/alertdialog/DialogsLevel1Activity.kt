package com.example.alertdialog

import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.alertdialog.databinding.ActivityDialogsLevel1Binding
import com.example.alertdialog.entities.AvailableVolumeValues
import kotlin.properties.Delegates.notNull

class DialogsLevel1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityDialogsLevel1Binding

    private var volume by notNull<Int>()
    private var color by notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDialogsLevel1Binding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        binding.apply {
            showDefaultAlertDialogButton.setOnClickListener {
                showAlertDialog()
            }

            showSingleChoiceAlertDialogButton.setOnClickListener {
                showSingleChoiceAlertDialog()
            }

            showSingleChoiceWithConfirmationAlertDialogButton.setOnClickListener {
                showSingleChoiceWithConfirmationAlertDialog()
            }

            showMultipleChoiceAlertDialogButton.setOnClickListener {
                showMultipleChoiceAlertDialog()
            }

            showMultipleChoiceWithConfirmationAlertDialogButton.setOnClickListener {
                showMultipleChoiceWithConfirmationAlertDialog()
            }
        }

        volume = savedInstanceState?.getInt(KEY_VOLUME) ?: 50
        color = savedInstanceState?.getInt(KEY_COLOR) ?: Color.RED

        updateUi()

    }

    // ---- Default Alert Dialog ----
    private fun showAlertDialog() {
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> showToast(R.string.uninstall_confirmed)
                DialogInterface.BUTTON_NEGATIVE -> showToast(R.string.uninstall_rejected)
                DialogInterface.BUTTON_NEUTRAL -> showToast(R.string.uninstall_ignored)
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(getString(R.string.default_alert_title))
            .setMessage(getString(R.string.default_alert_message))
            .setIcon(R.drawable.ic_launcher_foreground)
            .setPositiveButton(getString(R.string.action_yes), listener)
            .setNegativeButton(getString(R.string.action_no), listener)
            .setNeutralButton(getString(R.string.action_ignore), listener)
            .setOnCancelListener {
                showToast(R.string.dialog_cancelled)
            }
            .setOnDismissListener {
                Log.d(TAG, "Dialog missed")
            }
            .create()
        dialog.show()
    }

    private fun showToast(message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // ---- Single Choice Alert Dialog ----
    private fun showSingleChoiceAlertDialog() {
        val volumeItems = AvailableVolumeValues.createVolumeValues(volume)
        val volumeItemsText = volumeItems.values
            .map { getString(R.string.volume_description, it) }
            .toTypedArray()

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.volume_setup)
            .setSingleChoiceItems(volumeItemsText, volumeItems.currentIndex) { dialog, which ->
                volume = volumeItems.values[which]
                updateUi()
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    // ---- Single Choice With Confirmation Alert Dialog ----
    private fun showSingleChoiceWithConfirmationAlertDialog() {
        val volumeItems = AvailableVolumeValues.createVolumeValues(volume)
        val volumeItemsText = volumeItems.values
            .map { getString(R.string.volume_description, it) }
            .toTypedArray()

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.volume_setup)
            .setSingleChoiceItems(volumeItemsText, volumeItems.currentIndex, null)
            .setPositiveButton(R.string.action_confirm) { dialog, _ ->
                val index = (dialog as AlertDialog).listView.checkedItemPosition
                volume = volumeItems.values[index]
                updateUi()
            }
            .create()
        dialog.show()
    }

    // ---- Multiple Choice Alert Dialog ----
    private fun showMultipleChoiceAlertDialog() {
        val colorItems = resources.getStringArray(R.array.colors)
        val colorComponents = mutableListOf(
            Color.red(this.color),
            Color.green(this.color),
            Color.blue(this.color)
        )

        val checkBox = colorComponents
            .map { it > 0 }
            .toBooleanArray()

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.volume_setup)
            .setMultiChoiceItems(colorItems, checkBox) { _, which, isChecked ->
                colorComponents[which] = if (isChecked) 255 else 0
                this.color = Color.rgb(
                    colorComponents[0],
                    colorComponents[1],
                    colorComponents[2]
                )
                updateUi()
            }
            .setPositiveButton(R.string.action_close, null)
            .create()
        dialog.show()
    }

    // ---- Multiple Choice With Confirmation Alert Dialog ----
    private fun showMultipleChoiceWithConfirmationAlertDialog() {
        val colorItems = resources.getStringArray(R.array.colors)
        val colorComponents = mutableListOf(
            Color.red(this.color),
            Color.green(this.color),
            Color.blue(this.color)
        )

        val checkBox = colorComponents
            .map { it > 0 }
            .toBooleanArray()

        var color: Int = this.color
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.volume_setup)
            .setMultiChoiceItems(colorItems, checkBox) { _, which, isChecked ->
                colorComponents[which] = if (isChecked) 255 else 0
                color = Color.rgb(
                    colorComponents[0],
                    colorComponents[1],
                    colorComponents[2]
                )
                updateUi()
            }
            .setPositiveButton(R.string.action_confirm) { _, _ ->
                this.color = color
                updateUi()
            }
            .create()
        dialog.show()
    }

    // ---- UI update ----
    private fun updateUi() {
        binding.currentVolumeTextView.text = getString(R.string.current_volume, volume)
        binding.colorView.setBackgroundColor(color)
    }

    companion object {
//        private const val TAG = DialogsLevel1Activity::class.java.simpleName
        private const val KEY_VOLUME = "KEY_VOLUME"
        private const val KEY_COLOR = "KEY_COLOR"
    }
}