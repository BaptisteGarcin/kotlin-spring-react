package com.example.demo

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class KontrollerHtm {


    @GetMapping("/htmlpage")
    fun blog(model: Model): String {
        println("te")
        return "exampleTest"
    }



}

