package com.example.demo


import com.altima.api.sugar.enums.ModuleEnum
import com.altima.api.sugar.models.SugarContact
import com.altima.api.sugar.models.SugarRecord
import com.altima.api.sugar.service.ISugarSearchService
import com.altima.api.sugar.service.query.api.SugarQueryApi.module
import org.reactivecouchbase.json.Json
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import rx.Observable
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletResponse

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
    fun sugar(httpServletResponse: HttpServletResponse) {
        println("consents")
        // TODO : Get consents from sugar CRM
        val callQuery = module(ModuleEnum.CONTACT)
        //httpServletResponse
        val toSingle = sugarSearchService.filter<SugarRecord>(callQuery)
                .onBackpressureBuffer()
                //  .delay(1, TimeUnit.SECONDS)
                .timeout(60000, TimeUnit.MILLISECONDS)
                .map {
                    Json.toJson(it)
                }
                .forEach {
                    httpServletResponse.outputStream.write(it.stringify().toByteArray())
                }
    }

    @GetMapping("/consentsbis")
    fun consentsbis(): MutableList<SugarContact> {
        println("consents")
        // TODO : Get consents from sugar CRM
        val callQuery = module(ModuleEnum.CONTACT)
        val mutableList: MutableList<SugarContact>
        mutableList = sugarSearchService.filter<SugarContact>(callQuery)
                .onBackpressureBuffer()
                //  .delay(1, TimeUnit.SECONDS)
                .timeout(80, TimeUnit.SECONDS)
                .toList().toBlocking().single()
        return mutableList
    }

    @GetMapping("/consentsthree")
    fun consentsthree(): Observable<MutableList<ConsentDto>>? {
        println("consents")
        // TODO : Get consents from sugar CRM
        val callQuery = module(ModuleEnum.CONTACT)
        //val mutableList: MutableList<SugarContact>
        return sugarSearchService.filter<SugarContact>(callQuery)
                .onBackpressureBuffer()
                .timeout(120, TimeUnit.SECONDS)
                .map { contact: SugarContact? ->
                    ConsentDto(
                            id = contact?.id ?: "",
                            name = contact?.name ?: "",
                            mailConsent = contact?.isRefusSollicitMailCourrier ?: false,
                            phoneConsent = contact?.isRefusSollicitTel ?: false
                    )
                }.toList()
    }


}

