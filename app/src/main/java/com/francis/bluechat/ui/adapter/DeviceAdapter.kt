package com.francis.bluechat.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.francis.bluechat.R
import com.francis.bluechat.data.Device
import com.francis.bluechat.databinding.ItemDeviceBinding

class DeviceAdapter(private val listener: DeviceClickListener) :
    ListAdapter<Device, DeviceAdapter.ViewHolder>(DeviceDiffCallback()) {

    interface DeviceClickListener {
        fun onclick(device: Device)
    }

    class ViewHolder(private val binding: ItemDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: DeviceClickListener, device: Device) {
            binding.listener = listener
            binding.device = device
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ItemDeviceBinding>(
                    inflater,
                    R.layout.item_device,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listener, getItem(position))
    }

    class DeviceDiffCallback : DiffUtil.ItemCallback<Device>() {
        override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem == newItem
        }
    }
}