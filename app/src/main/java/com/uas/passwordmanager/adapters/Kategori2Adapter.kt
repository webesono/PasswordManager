package com.uas.passwordmanager.adapters

import android.content.Context
import android.content.Intent
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uas.passwordmanager.R
import com.uas.passwordmanager.activities.DetailActivity
import com.uas.passwordmanager.models.Kategori
import com.uas.passwordmanager.utilities.Constants
import com.uas.passwordmanager.utilities.PreferenceManager

class Kategori2Adapter: RecyclerView.Adapter<Kategori2Adapter.Kategori2ViewHolder> {

    private var categories: List<Kategori>
    private var context: Context
    private lateinit var preferenceManager: PreferenceManager

    constructor(context: Context, categories: List<Kategori>){
        this.categories = categories
        this.context = context
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Kategori2Adapter.Kategori2ViewHolder {
        var view: View = LayoutInflater.from(context).inflate(
            R.layout.item_container_kategoritok, parent, false
        )
        return Kategori2ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Kategori2Adapter.Kategori2ViewHolder, position: Int) {
        val currentCategory = categories[position]
        preferenceManager = PreferenceManager(context)
        holder.textKategori.text = currentCategory.kategori
        holder.itemsRecycleView.visibility = View.GONE

        holder.itemView.setOnClickListener {
            preferenceManager.putString(Constants.KEY_KATEGORI, currentCategory.kategori!!)
            val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra(Constants.KEY_KATEGORI, currentCategory.kategori)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    class Kategori2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textKategori = itemView.findViewById<TextView>(R.id.textKategori)
        val itemsRecycleView = itemView.findViewById<RecyclerView>(R.id.passRecycleView)

    }
}