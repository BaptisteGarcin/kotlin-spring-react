package com.example.demo


import com.altima.api.sugar.enums.ModuleEnum
import com.altima.api.sugar.models.SugarContact
import com.altima.api.sugar.models.SugarRecord
import com.altima.api.sugar.service.ISugarSearchService
import com.altima.api.sugar.service.query.api.SugarQueryApi.module
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.reactivecouchbase.json.Json
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import rx.Observable
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletResponse


@RestController
class Kontroller(private var sugarSearchService: ISugarSearchService) {
    @Value("\${app.nio-client-id}")
    lateinit var nio_client_id: String

    @Value("\${app.nio-client-secret}")
    lateinit var nio_client_secret: String

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
    fun consentsthree(): Observable<ResponseEntity<MutableList<ConsentNio>>>? {
        println("consents")
        // TODO : Get consents from sugar CRM
        val callQuery = module(ModuleEnum.CONTACT)
        return sugarSearchService.filter<SugarContact>(callQuery)
                .onBackpressureBuffer()
                // It happens often to get Read Timed out error
                .timeout(260, TimeUnit.SECONDS)
                .map { contact: SugarContact? ->
                    ConsentNio(
                            userId = contact?.id ?: "",
                            name = contact?.name ?: "",
                            doneBy = DoneBy(
                                    userId = "",
                                    role = ""
                            ),
                            version = 1,
                            groups = listOf(
                                    Groups(
                                            key = "grp1",
                                            label = "J'accepte de recevoir les offres personnalisées",
                                            consents = listOf(
                                                    Consents(
                                                            key = "phone",
                                                            label = "Par téléphone",
                                                            checked = contact?.isRefusSollicitTel ?: false
                                                    ),
                                                    Consents(
                                                            key = "email",
                                                            label = "Par e-mail",
                                                            checked = contact?.isRefusSollicitMailCourrier ?: false
                                                    ))
                                    )
                            )
                    )
                }
                .doOnNext { importToNio(it) }
                .toList()
                .map { ResponseEntity.ok(it) }
                .doOnTerminate {
                    println("terminated")
                }
    }

    fun importToNio(consentNio: ConsentNio) {
        val client = OkHttpClient()

        val mediaType = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, Json.toJson(consentNio).stringify())
        val request = Request.Builder()
                .url("http://localhost:9000/api/dev/organisations/Altima/users/${consentNio.userId}")
                .put(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Nio-Client-Id", nio_client_id)
                .addHeader("Nio-Client-Secret", nio_client_secret)
                .build()

        val response = client.newCall(request).execute()
        println(response)
    }

}

