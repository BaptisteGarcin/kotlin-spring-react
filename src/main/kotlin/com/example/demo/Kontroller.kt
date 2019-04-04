package com.example.demo


import com.altima.api.sugar.enums.ModuleEnum
import com.altima.api.sugar.enums.RecordFieldsEnum
import com.altima.api.sugar.models.SugarRecord
import com.altima.api.sugar.service.ISugarSearchService
import com.altima.api.sugar.service.query.api.SugarFilterApi.and
import com.altima.api.sugar.service.query.api.SugarFilterApi.equal
import com.altima.api.sugar.service.query.api.SugarQueryApi.module
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

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
                .withFilter(and(
                        equal(RecordFieldsEnum.PARENT_TYPE.code, ModuleEnum.CONTACT.code)))

        sugarSearchService.filter<SugarRecord>(callQuery).forEach { sugarRecord -> println("sugarRecord: $sugarRecord") }
    }
}

