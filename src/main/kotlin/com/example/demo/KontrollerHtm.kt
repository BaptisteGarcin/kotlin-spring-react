package com.example.demo

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class KontrollerHtm {


    @GetMapping("")
    fun blog(model: Model): String {
        println("te")
        return "exampleTest"
    }

    @GetMapping("/react")
    fun react(model: Model): String {
        return "index"
    }


}

