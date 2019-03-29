package com.example.demo

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

    @GetMapping("")
    fun blog(model: Model): String {
        println("te")
        return "exampleTest"
    }

    @PostMapping("/login")
    fun login(@ModelAttribute("uname") uname: String,
              @ModelAttribute("psw") psw: String, model: Model): String {
        if (uname == "admin" && psw == "admin") {
            if (mode == "prod")
                model["publicUrl"] = ""
            else
                model["publicUrl"] = "http://localhost:1234"
            return "react"
        }
        return "error"
    }


}

