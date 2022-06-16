package com.uas.passwordmanager.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uas.passwordmanager.R
import com.uas.passwordmanager.activities.DetailActivity
import com.uas.passwordmanager.models.ItemPass
import com.uas.passwordmanager.utilities.Constants
import com.uas.passwordmanager.utilities.PreferenceManager

class ItemPassAdapter(val context: Context, private val itemList: ArrayList<ItemPass>): RecyclerView.Adapter<ItemPassAdapter.ItemViewHolder>() {

    private lateinit var preferenceManager: PreferenceManager

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
        val currentItem = itemList[position]
        preferenceManager = PreferenceManager(context)

        holder.textPassword.text = currentItem.password
        holder.textKeterangan.text = currentItem.keterangan

        holder.itemView.setOnLongClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(Constants.KEY_KETERANGAN, currentItem.keterangan.toString())
            intent.putExtra(Constants.KEY_PASSWORD, currentItem.password.toString())
            intent.putExtra(Constants.KEY_KATEGORI, currentItem.kategori.toString())
            intent.putExtra(Constants.KEY_POSITION, position.toString())
            preferenceManager.putString(Constants.KEY_POSITION, position.toString())
            context.startActivity(intent)

            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder( itemView: View): RecyclerView.ViewHolder(itemView){
        val textPassword: TextView = itemView.findViewById(R.id.textPassword)
        val textKeterangan: TextView = itemView.findViewById(R.id.textKeterangan)
    }


}