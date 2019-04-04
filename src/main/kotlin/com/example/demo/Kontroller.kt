package com.example.demo


import com.altima.api.sugar.enums.ModuleEnum
import com.altima.api.sugar.models.SugarRecord
import com.altima.api.sugar.service.ISugarSearchService
import com.altima.api.sugar.service.query.api.SugarQueryApi.module
import org.reactivecouchbase.json.Json
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
class Kontroller(private var sugarSearchService: ISugarSearchService) {

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
        val callQuery = module(ModuleEnum.CONTACT)

        sugarSearchService.filter<SugarRecord>(callQuery)
                .onBackpressureBuffer()
                .timeout(60000, TimeUnit.MILLISECONDS)
                .forEach { sugarRecord -> println("sugarRecord: ${Json.toJson(sugarRecord).stringify()}") }
    }
}

