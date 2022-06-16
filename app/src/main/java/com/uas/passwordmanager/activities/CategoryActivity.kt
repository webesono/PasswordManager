package com.uas.passwordmanager.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.uas.passwordmanager.R
import com.uas.passwordmanager.adapters.Kategori2Adapter
import com.uas.passwordmanager.databinding.ActivityCategoryBinding
import com.uas.passwordmanager.models.ItemPass
import com.uas.passwordmanager.models.Kategori
import com.uas.passwordmanager.utilities.Constants
import com.uas.passwordmanager.utilities.PreferenceManager

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var kategoris: ArrayList<Kategori>
    private lateinit var kategoriAdapter: Kategori2Adapter
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        kategoris = ArrayList()
        kategoriAdapter = Kategori2Adapter(this, kategoris)
        preferenceManager = PreferenceManager(applicationContext)
        preferenceManager.putString(Constants.KEY_POSITION, "-")

        navBar()
        addData()
        setListener()
    }

    private fun setListener(){
        binding.back.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

    private fun navBar(){
        binding.bottomNavigationView.selectedItemId = (R.id.categories)
        binding.bottomNavigationView.setOnItemSelectedListener (NavigationBarView.OnItemSelectedListener{
            when (it.itemId){
                R.id.dashboard -> {
                    startActivity(Intent(applicationContext, DashboardActivity::class.java))
                    overridePendingTransition(0,0)
                    return@OnItemSelectedListener  true
                }
                R.id.categories -> return@OnItemSelectedListener true
                R.id.profile ->{
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(0,0)
                    return@OnItemSelectedListener true
                }
            }
            false
        })
    }

    private fun addData(){
        loading(true)
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        database.reference.child(Constants.KEY_CATEGORIES).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                kategoris.clear()

                for (postSnapshot in snapshot.children){
                    val kategori = postSnapshot.getValue(Kategori::class.java)
                    kategoris.add(kategori!!)
                }
                binding.kategorisRecycleView.adapter = kategoriAdapter
                kategoriAdapter.notifyDataSetChanged()
                loading(false)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun loading(isLoading: Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.INVISIBLE
        }
    }



}