package com.example.demo.controllers


import com.altima.api.sugar.enums.ModuleEnum
import com.altima.api.sugar.models.SugarContact
import com.altima.api.sugar.service.ISugarSearchService
import com.altima.api.sugar.service.query.api.SugarQueryApi.module
import com.example.demo.models.ConsentNio
import com.example.demo.models.Consents
import com.example.demo.models.DoneBy
import com.example.demo.models.Groups
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


@RestController
class ConsentsKontroller(private var sugarSearchService: ISugarSearchService) {
    @Value("\${app.nio-client-id}")
    lateinit var nio_client_id: String

    @Value("\${app.nio-client-secret}")
    lateinit var nio_client_secret: String

    @GetMapping("/sugar/consents")
    fun getConsentsFromSugarBlockingVersion(): MutableList<SugarContact> {
        val callQuery = module(ModuleEnum.CONTACT)
        val mutableList: MutableList<SugarContact>
        mutableList = sugarSearchService.filter<SugarContact>(callQuery)
                .onBackpressureBuffer()
                //  .delay(1, TimeUnit.SECONDS)
                .timeout(80, TimeUnit.SECONDS)
                .toList().toBlocking().single()
        return mutableList
    }

    @GetMapping("/consents")
    fun exportConsentsFromSugarAndImportToNio(): Observable<ResponseEntity<MutableList<ConsentNio>>>? {
        println("consents")
        // TODO : Get consents from sugar CRM
        // FIXME:  Request to sugar sometimes fail with java.lang.IllegalStateException: getOutputStream() has already been called for this response
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

