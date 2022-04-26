package com.example.alertdialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.alertdialog.databinding.ItemVolumeSingleChoiceBinding

class VolumeAdapter(private val values: List<Int>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = parent.context
        val binding = convertView?.tag as ItemVolumeSingleChoiceBinding?
            ?: ItemVolumeSingleChoiceBinding.inflate(LayoutInflater.from(context)).also {
                it.root.tag = it
            }

        val volume = getItem(position)

        binding.apply {
            volumeValueTextView.text = context.getString(R.string.volume_description, volume)
            volumeValueProgressBar.progress = volume
        }

        return binding.root
    }

    override fun getCount(): Int = values.size

    override fun getItem(position: Int): Int = values[position]

    override fun getItemId(position: Int): Long = position.toLong()
}