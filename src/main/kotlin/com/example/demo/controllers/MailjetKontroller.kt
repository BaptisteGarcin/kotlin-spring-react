package com.example.demo.controllers

import okhttp3.*
import org.reactivecouchbase.json.Json
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class MailjetKontroller() {
    @Value("\${app.mailjet-public}")
    lateinit var mailjet_public: String

    @Value("\${app.mailjet-secret}")
    lateinit var mailjet_secret: String

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