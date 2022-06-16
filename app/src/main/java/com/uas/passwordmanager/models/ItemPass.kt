package com.uas.passwordmanager.models

class ItemPass {
    var kategori: String? = null
    var keterangan: String? = null
    var password: String? = null
    var userId: String? = null

    constructor(){}

    constructor(kategori: String?, keterangan: String?, password: String?, userId: String?){
        this.kategori = kategori
        this.keterangan = keterangan
        this.password = password
        this.userId = userId
    }
}