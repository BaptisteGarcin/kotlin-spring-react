package com.example.demo

import org.apache.coyote.http11.Constants.a
import org.springframework.http.RequestEntity.head
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView
import javax.swing.text.html.HTML


@RestController
class Kontroller {

    @GetMapping("/test")
    fun test() =
        Class("hello ggo")


    @GetMapping("/list")
    fun list(): MutableList<Class> {
        return mutableListOf(Class("hellho go"), Class("hello"))
    }

    @GetMapping("/html")
    fun listStudents(model: Model): String {
        return "exampleTest"
    }

}

