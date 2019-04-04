package com.example.demo


import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class Kontroller {

    @GetMapping("/test")
    fun test() =
        Class("hello ggo")


    @GetMapping("/list")
    fun list(): MutableList<Class> {
        return mutableListOf(Class("hellho go"), Class("hello"))
    }

    @GetMapping("/consents")
    fun sugar() {
        println("sugar")
        // TODO : Get consents from sugar CRM
    }
}

