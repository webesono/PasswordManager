package com.uas.passwordmanager.models

class Item {
    var itemPass: List<ItemPass>?= null

    constructor(){}
    constructor(itemPass: List<ItemPass>){
        this.itemPass = itemPass
    }
}