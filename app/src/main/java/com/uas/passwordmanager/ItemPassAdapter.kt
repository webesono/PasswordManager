package com.uas.passwordmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uas.passwordmanager.R
import com.uas.passwordmanager.models.ItemPass

class ItemPassAdapter(val context: Context, private val itemList: List<ItemPass>): RecyclerView.Adapter<ItemPassAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        var view: View = LayoutInflater.from(context) .inflate(
            R.layout.item_container_pass, parent, false
        )

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.textPassword.text = itemList[position].password.toString()
        holder.textKeterangan.text = itemList[position].keterangan.toString()
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder( itemView: View): RecyclerView.ViewHolder(itemView){
        val textPassword: TextView = itemView.findViewById(R.id.textPassword)
        val textKeterangan: TextView = itemView.findViewById(R.id.textKeterangan)
    }


}