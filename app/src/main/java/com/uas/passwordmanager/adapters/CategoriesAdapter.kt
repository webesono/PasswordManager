package com.uas.passwordmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uas.passwordmanager.R
import com.uas.passwordmanager.ItemPassAdapter
import com.uas.passwordmanager.models.ItemPass
import com.uas.passwordmanager.models.Kategori
import com.uas.passwordmanager.utilities.Constants

class CategoriesAdapter(private var context: Context, private var categories: ArrayList<Kategori>): RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {


    lateinit var itemPassAdapter: ItemPassAdapter

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoriesAdapter.CategoryViewHolder {
        var view: View = LayoutInflater.from(context).inflate(
            R.layout.item_container_kategori, parent, false
        )
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriesAdapter.CategoryViewHolder, position: Int) {
        val currentCategory = categories[position]
        val kategori = currentCategory.kategori.toString()

        holder.textKategori.text = kategori
        itemPassAdapter = categories[position].itemPass?.let { ItemPassAdapter(context, it) }!!
        holder.passRecycleView.adapter = itemPassAdapter

    }

    override fun getItemCount(): Int {
        return categories.size
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textKategori = itemView.findViewById<TextView>(R.id.textKategori)
        val passRecycleView = itemView.findViewById<RecyclerView>(R.id.passRecycleView)
        val errorText = itemView.findViewById<TextView>(R.id.errorText)
    }

}