package com.uas.passwordmanager.models

class Kategori {
    var kategori: String? = null
    var itemPass: List<ItemPass>? = null

    constructor(){}
    constructor(kategori: String, itemPass: List<ItemPass>){
        this.kategori = kategori
        this.itemPass = itemPass
    }
}