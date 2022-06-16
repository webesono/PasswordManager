package com.uas.passwordmanager.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.uas.passwordmanager.R
import com.uas.passwordmanager.databinding.ActivityAddItemBinding
import com.uas.passwordmanager.models.ItemPass
import com.uas.passwordmanager.models.Kategori
import com.uas.passwordmanager.utilities.Constants

class AddItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var datastore: FirebaseFirestore
    private lateinit var database: FirebaseDatabase
    private lateinit var kategoriList: ArrayList<String>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        datastore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        kategoriList = ArrayList()


        setListener()
    }

    private fun setListener(){
        binding.btnSimpan.setOnClickListener {
            val kategori = binding.spKategori.selectedItem.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            val keterangan = binding.etKeterangan.text.toString().trim()
            val uid = mAuth.currentUser?.uid.toString().trim()

            if (isNotEmpty(kategori, pass)){
                savePass(kategori, pass, keterangan, uid)
            }

        }
        dropdownKategori()
//        while (binding.spKategori.selectedItem != null){
//            dropdownApp()
//        }


    }

    private fun isNotEmpty(kategori: String, pass: String): Boolean{
        if (kategori == "null"){
            showToast("Kamu belum memilih kategori")
            return false
        }
        else if (pass.isEmpty() || pass.length<6){
            showToast("Password minimal 6 karakter !")
            binding.etPassword.requestFocus()
            return false
        }
        else if (binding.etConfmPassword.text.isEmpty()){
            showToast("Konfirmasi password belum diisi !")
            binding.etPassword.requestFocus()
            return false
        }
        else if (binding.etConfmPassword.text.toString() != pass){
            showToast("Konfirmasi password tidak sesuai !")
            binding.etConfmPassword.requestFocus()
            return false
        }
        else{
            return true
        }
    }

    private fun savePass(kategori: String, pass: String,keterangan: String, uid: String){
        loading(true)
        val item = ItemPass(kategori, keterangan, pass, uid)
        val itemPass = listOf(item)
        val category = Kategori(kategori, itemPass)

        database.reference.child(Constants.KEY_COLLECTION_PASSWORDS)
            .child(uid)
            .child(kategori)
            .child(Constants.KEY_ITEM)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val count= snapshot.childrenCount.toInt()

                    if (count <= 0){
                        database.reference.child(Constants.KEY_COLLECTION_PASSWORDS)
                            .child(uid)
                            .child(kategori)
                            .setValue(category)
                            .addOnCompleteListener {
                                if (it.isSuccessful){
                                    loading(false)
                                    finish()
                                    showToast("Password kamu udah disimpan !")
                                }
                            }
                    }else{
                        database.reference.child(Constants.KEY_COLLECTION_PASSWORDS)
                            .child(uid)
                            .child(kategori)
                            .child(Constants.KEY_ITEM).child((count).toString())
                            .setValue(item)
                            .addOnCompleteListener {
                                if (it.isSuccessful){
                                    loading(false)
                                    finish()
                                    showToast("Password kamu udah disimpan !")
                                }
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        startActivity(Intent(this, DashboardActivity::class.java))


    }

    private fun dropdownKategori(){
        database.reference.child(Constants.KEY_CATEGORIES).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                kategoriList.clear()

                for (cs in snapshot.children){
                    val spinnerKategori = cs.child(Constants.KEY_CATEGORIES).value.toString()
                    kategoriList.add(spinnerKategori)
                }

                val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(this@AddItemActivity, android.R.layout.simple_spinner_dropdown_item, kategoriList)
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spKategori.adapter = arrayAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun showToast(pesan: String){
        Toast.makeText(applicationContext, pesan, Toast.LENGTH_LONG).show()
    }

    private fun loading(isLoading: Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSimpan.visibility = View.INVISIBLE
        }else{
            binding.progressBar.visibility = View.INVISIBLE
            binding.btnSimpan.visibility = View.VISIBLE
        }
    }

}