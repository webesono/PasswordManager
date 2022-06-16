package com.uas.passwordmanager.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uas.passwordmanager.R
import com.uas.passwordmanager.adapters.CategoriesAdapter
import com.uas.passwordmanager.databinding.ActivityDashboardBinding
import com.uas.passwordmanager.models.Kategori
import com.uas.passwordmanager.utilities.Constants
import com.uas.passwordmanager.utilities.PreferenceManager
import kotlin.collections.ArrayList

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var kategoris: ArrayList<Kategori>
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var kategoriAdapter: CategoriesAdapter
    private lateinit var mAuth: FirebaseAuth

    private val TIME_INTERVAL = 2000
    private var backPressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceManager = PreferenceManager(applicationContext)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        mAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        kategoris = ArrayList()
        kategoriAdapter = CategoriesAdapter(this, kategoris)
        binding.kategorisRecycleView.adapter = kategoriAdapter

        loadUser()
        setListener()
        navBar()
        addData()


    }

    private fun navBar(){
        binding.bottomNavigationView.selectedItemId = (R.id.dashboard)
        binding.bottomNavigationView.setOnItemSelectedListener (NavigationBarView.OnItemSelectedListener{
            when (it.itemId){
                R.id.profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(0,0)
                    return@OnItemSelectedListener  true
                }
                R.id.dashboard -> return@OnItemSelectedListener true
                R.id.categories ->{
                    startActivity(Intent(applicationContext, CategoryActivity::class.java))
                    overridePendingTransition(0,0)
                    return@OnItemSelectedListener true
                }
            }
            false
        })
    }

    private fun setListener(){
        binding.imageProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.imageSignOut.setOnClickListener { logout() }

        binding.addItem.setOnClickListener {
            startActivity(Intent(this, AddItemActivity::class.java))
        }
    }

    private fun loadUser(){
        binding.textNama.text = preferenceManager.getString(Constants.KEY_NAME)

        val bytes: ByteArray = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap: Bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        binding.imageProfile.setImageBitmap(bitmap)
    }

    private fun showToast(pesan: String){
        Toast.makeText(applicationContext, pesan, Toast.LENGTH_SHORT).show()
    }

    private fun logout(){
        showToast("Signing out ...")
        mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()
        preferenceManager.clear()
        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }

    private fun addData(){
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val uid = mAuth.currentUser?.uid!!

        database.reference.child(Constants.KEY_COLLECTION_PASSWORDS)
            .child(uid)
            .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                kategoris.clear()

                for (postSnapshot in snapshot.children){
                    val kategori = postSnapshot.getValue(Kategori::class.java)

                    kategoris.add(kategori!!)
                }
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

    override fun onBackPressed() {
        if (backPressed + TIME_INTERVAL > System.currentTimeMillis() ){
            super.onBackPressed()
            return@onBackPressed
        }
        else{
            Toast.makeText(baseContext, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show()
        }
        backPressed = System.currentTimeMillis()

    }
}