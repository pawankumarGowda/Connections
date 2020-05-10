package com.example.connections.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.connections.BR
import com.example.connections.model.WifiStation
import com.example.connections.R
import com.example.connections.databinding.WifiListItemBinding

class WifiListAdapter(var context: Context) : RecyclerView.Adapter<WifiListAdapter.ViewHolder>() {
    private var list: List<WifiStation> = emptyList<WifiStation>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: WifiListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.wifi_list_item,
            parent,
            false
        )
        return WifiListAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    public fun setAdapterList(list: List<WifiStation>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(val binding: WifiListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Any) {
            binding.setVariable(BR.wifistation, data)
            binding.executePendingBindings()
        }
    }
}