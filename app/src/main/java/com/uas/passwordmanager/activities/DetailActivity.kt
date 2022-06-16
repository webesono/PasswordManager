package com.uas.passwordmanager.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.uas.passwordmanager.R
import com.uas.passwordmanager.adapters.ItemPassAdapter
import com.uas.passwordmanager.databinding.ActivityDetailBinding
import com.uas.passwordmanager.models.ItemPass
import com.uas.passwordmanager.utilities.Constants
import com.uas.passwordmanager.utilities.PreferenceManager

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var itemList: ArrayList<ItemPass>
    private lateinit var itemPassAdapter: ItemPassAdapter
    private lateinit var preferenceManager: PreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceManager = PreferenceManager(applicationContext)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        itemList = ArrayList()
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        itemPassAdapter = ItemPassAdapter(this@DetailActivity, itemList)
        binding.passRecycleView.adapter = itemPassAdapter

        setListener()
        getDetail()

        if ( preferenceManager.getString(Constants.KEY_POSITION) != "-") { showEdit() }

    }

    private fun setListener(){
        binding.back.setOnClickListener {
            startActivity(Intent(applicationContext, CategoryActivity::class.java))
        }
        binding.deleteLast.setOnClickListener {
            deleteLast()
        }
    }

    private fun getDetail(){
        val kategori = intent.getStringExtra(Constants.KEY_KATEGORI)
        binding.kategori.text = kategori
        loading(true)
        database.reference.child(Constants.KEY_COLLECTION_PASSWORDS)
            .child(mAuth.currentUser?.uid!!)
            .child(kategori!!)
            .child(Constants.KEY_ITEM)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    itemList.clear()

                    for (postSnapshot in snapshot.children){
                        val item = postSnapshot.getValue(ItemPass::class.java)
                        itemList.add(item!!)
                    }
                    itemPassAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        loading(false)
    }


    private fun loading(isLoading: Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun showErrorMessage(){
        binding.errorText.text = String.format("%s", "Data laporan tidak tersedia")
        binding.errorText.visibility = View.VISIBLE
    }

    private fun showEdit(){
        val bottomSheetDialog = BottomSheetDialog( this, R.style.BottomSheetDialogTheme)

        val sheetChangeItemView: View = LayoutInflater.from(applicationContext)
            .inflate(R.layout.activity_change_item, findViewById(R.id.changeItemContainer))

        val etNewKeterangan = sheetChangeItemView.findViewById<TextView>(R.id.etNewKeterangan)
        val etNewPassItem = sheetChangeItemView.findViewById<TextView>(R.id.etNewPassItem)

        etNewKeterangan.text = intent.getStringExtra(Constants.KEY_KETERANGAN)
        etNewPassItem.text = intent.getStringExtra(Constants.KEY_PASSWORD)

        sheetChangeItemView.findViewById<Button>(R.id.btnUpdateItem).setOnClickListener {
            val newKeterangan = etNewKeterangan.text.toString().trim()
            val newPassItem = etNewPassItem.text.toString().trim()

            if(ifItemNotEmpty(newPassItem)){
                updateItem(newPassItem, newKeterangan)

            }
        }

        bottomSheetDialog.setContentView(sheetChangeItemView)
        bottomSheetDialog.show()


    }

    private fun ifItemNotEmpty(newPassItem: String): Boolean{
        if (newPassItem.isEmpty()  || newPassItem.length < 6){
            showToast("Password tuh seenggaknya 6 karakter biar aman")
            return false
        }
        else{
            return true
        }
    }

    private fun deleteLast(){
        val uid = mAuth.currentUser?.uid!!
        val kategori = intent.getStringExtra(Constants.KEY_KATEGORI)!!
        database.reference.child(Constants.KEY_COLLECTION_PASSWORDS)
            .child(uid)
            .child(kategori)
            .child(Constants.KEY_ITEM)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot1: DataSnapshot) {
                    val count = snapshot1.childrenCount.toInt()

                    if (count <=1){
                        showToast("INI ITEM TERAKHIR")
                        showToast("MENDINGAN DIEDIT AJA")
                    }else{
                        val dbRef = database.reference.child(Constants.KEY_COLLECTION_PASSWORDS)
                            .child(uid)
                            .child(kategori)
                            .child(Constants.KEY_ITEM)

                        val query: Query = dbRef.child((count-1).toString())
                        query.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.ref.removeValue()
                                showToast("Item terakhir berhasil dihapus")
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                    }



                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun updateItem(newPassItem: String, newKeterangan: String){
        val uid = mAuth.currentUser?.uid!!
        val kategori = intent.getStringExtra(Constants.KEY_KATEGORI)!!
        val update = ItemPass(
            kategori, newKeterangan, newPassItem, uid
        )

        database.reference.child(Constants.KEY_COLLECTION_PASSWORDS)
            .child(uid)
            .child(kategori)
            .child(Constants.KEY_ITEM)
            .child(intent.getStringExtra(Constants.KEY_POSITION)!!)
            .setValue(update)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    showToast("Berhasil Update !")
                    preferenceManager.putString(Constants.KEY_POSITION, "-")
                    startActivity(intent)
                }
            }
    }
    private fun showToast(pesan: String){
        Toast.makeText(applicationContext, pesan, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, CategoryActivity::class.java))

    }

}