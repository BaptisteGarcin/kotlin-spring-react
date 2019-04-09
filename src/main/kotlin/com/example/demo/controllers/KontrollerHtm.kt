package com.example.demo.controllers

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping

@Controller
class KontrollerHtm {

    @Value("\${app.mode}")
    lateinit var mode: String

    @Value("\${app.devurl}")
    lateinit var devurl: String

    @GetMapping("")
    fun login(model: Model): String {
        return "login"
    }

    @PostMapping("/login")
    fun login(@ModelAttribute("uname") uname: String,
              @ModelAttribute("psw") psw: String, model: Model): String {
        if (uname == "admin" && psw == "admin") {
            if (mode == "dev")
                model["publicUrl"] = devurl
            else
                model["publicUrl"] = ""
            return "react"
        }
        return "error"
    }


}

