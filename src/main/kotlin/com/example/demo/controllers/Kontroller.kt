package com.example.demo.controllers


import com.altima.api.sugar.enums.ModuleEnum
import com.altima.api.sugar.models.SugarContact
import com.altima.api.sugar.service.ISugarSearchService
import com.altima.api.sugar.service.query.api.SugarQueryApi.module
import com.example.demo.models.*
import okhttp3.*
import okhttp3.RequestBody
import org.reactivecouchbase.json.Json
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import rx.Observable
import java.util.concurrent.TimeUnit


@RestController
class Kontroller(private var sugarSearchService: ISugarSearchService
) {
    @Value("\${app.nio-client-id}")
    lateinit var nio_client_id: String

    @Value("\${app.nio-client-secret}")
    lateinit var nio_client_secret: String

    @Value("\${app.mailjet-public}")
    lateinit var mailjet_public: String

    @Value("\${app.mailjet-secret}")
    lateinit var mailjet_secret: String

    @GetMapping("/test")
    fun test() =
            Class("hello ggo")


    @GetMapping("/list")
    fun list(): MutableList<Class> {
        return mutableListOf(Class("hellho go"), Class("hello"))
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

    fun sendEmail() {
        println("send email")
/*        val mail: Mail.Builder = Mail.newBuilder()
                .withToEmail("baptiste.garcin@serli.com")
                .withToName("name")
                .withSubject("Ceci est un email de test")
        mailService.send(mail)
                .doOnError { println(it.toString()) }
                .doOnCompleted { println("mail sent !") }*/
    }

/*    fun sendEmail2() {
        println("send email")
        val email: MailjetRequest
        val response: MailjetResponse

        val client = MailjetClient(mailjet_public, mailjet_secret, ClientOptions("v3.1"))

        val message = JSONObject()
        message.put(Emailv31.Message.FROM, JSONObject()
                .put(Emailv31.Message.EMAIL, "baptiste.garcin@outlook.fr")
                .put(Emailv31.Message.NAME, "Mailjet Pilot")
        )
                .put(Emailv31.Message.SUBJECT, "Your email flight plan!")
                .put(Emailv31.Message.TEXTPART, "Dear passenger, welcome to Mailjet! May the delivery force be with you!")
                .put(Emailv31.Message.HTMLPART, "<h3>Dear passenger, welcome to Mailjet</h3><br/>May the delivery force be with you!")
                .put(Emailv31.Message.TO, JSONArray()
                        .put(JSONObject()
                                .put(Emailv31.Message.EMAIL, "baptistabomb86@gmail.com")))

        email = MailjetRequest(Emailv31.resource).property(Emailv31.MESSAGES, JSONArray().put(message))

        response = client.post(email)
        println(response.status)
    }*/

    /**
     *
     * @api {DELETE} /api/mailjet/exclude/{emailOrId}
     *
     * @apiDescription Supprime le contact de mailjet
     *
     * @apiParam {String} email du contact sur mailjet
     *
     * @apiVersion 0.0.1
     *
     * @apiParamExample {String} "email@domain.com"
     *
     **/

    @RequestMapping(
            "/api/mailjet/delete/{email}",
            method = [RequestMethod.DELETE]
    )
    fun gdprDeleteContact(@PathVariable email: String): ResponseEntity<Any> {
        try {
            val clientHttp = OkHttpClient()

            val credential = Credentials.basic(mailjet_public, mailjet_secret)

            val requestId = Request.Builder()
                    .url("https://api.mailjet.com/v3/REST/contact/$email")
                    .get()
                    .addHeader("Authorization", credential)
                    .build()

            val responseId = clientHttp.newCall(requestId).execute()
            val ob = Json.parse(responseId.body()?.string())
            val id = ob.field("Data").get(0).field("ID").asInteger()

            val requestHttp = Request.Builder()
                    .url("https://api.mailjet.com/v4/contacts/$id")
                    .delete(null)
                    .addHeader("Authorization", credential)
                    .build()

            val res = clientHttp.newCall(requestHttp).execute()
            return if (res.isSuccessful)
                ResponseEntity.ok().build()
            else
                ResponseEntity.notFound().build()
        } catch (e: Exception) {
            return ResponseEntity.notFound().build()
        }

    }

    /**
     *
     * @api {PUT} /api/mailjet/exclude/{emailOrId}
     *
     * @apiDescription Ajout du contact dans la liste d'exclusion mailjet.
     *                  Le contact ne recevra plus d'emails via mailjet
     *
     * @apiParam {String} email ou identifiant du contact sur mailjet
     *
     * @apiVersion 0.0.1
     *
     * @apiParamExample {String} "email@domain.com"
     *
     **/
    @RequestMapping(
            "/api/mailjet/exclude/{emailOrId}",
            method = [RequestMethod.PUT]
    )
    fun gdprExcludeContactFromReceivingAnyEmails(@PathVariable emailOrId: String): ResponseEntity<Any> {
        try {
            val clientHttp = OkHttpClient()
            val credential = Credentials.basic(mailjet_public, mailjet_secret)
            val mediaType = MediaType.parse("application/json")
            val body = RequestBody.create(mediaType, "{\"IsExcludedFromCampaigns\":\"true\"}")
            val requestHttp = Request.Builder()
                    .url("https://api.mailjet.com/v3/REST/contact/$emailOrId")
                    .put(body)
                    .addHeader("Authorization", credential)
                    .addHeader("Content-Type", "application/json")
                    .build()

            val res = clientHttp.newCall(requestHttp).execute()
            return if (res.isSuccessful)
                ResponseEntity.ok().build()
            else
                ResponseEntity.notFound().build()
        } catch (e: Exception) {
            return ResponseEntity.notFound().build()
        }
    }


}

