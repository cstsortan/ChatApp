package com.d4vinci.chatapp

/**
 * Created by D4Vinci on 6/7/2017.
 */
class Chat {
    var name: String=""
    var text: String=""
    var time: String=""

    constructor() {}

    constructor(name: String, text: String, time: String) {
        this.name = name
        this.text = text
        this.time = time
    }
}