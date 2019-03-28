package com.example.demo

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class KontrollerHtm {

    @Value("\${app.mode}")
    lateinit var mode: String

    @GetMapping("")
    fun blog(model: Model): String {
        println("te")
        return "exampleTest"
    }

    @GetMapping("/react")
    fun react(model: Model): String {
        println("mode $mode")
        if (mode == "prod")
            model["publicUrl"] = ""
        else
            model["publicUrl"] = "http://localhost:1234"
        return "react"
    }


}

