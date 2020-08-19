package com.samsad

import com.fasterxml.jackson.databind.SerializationFeature
import com.samsad.model.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import java.text.DateFormat

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        jackson {
            install(ContentNegotiation) {
                jackson {
                    enable(SerializationFeature.INDENT_OUTPUT)
                    //enable(...)
                    dateFormat = DateFormat.getDateInstance()
                    disableDefaultTyping()
                    //convertValue(..., ...)
                }
            }
        }
    }

    val client = HttpClient(Apache) {
        routing {

            get("/") {
                //Text respose
                //call.respondText { "Hello Ktor" }

                //call.respond(mapOf(listOf("Samsad","Abdul Rahman") to "samsad"))

                var persons = ArrayList<Person>()



                for (i in 1..10) {
                    val person = Person("Samsad $i", i, "Android Developer")
                    persons.add(person)
                }

                var postPersons = PostPersons(persons)

                call.respond(postPersons)

            }

            get("/test") {
                call.respondText { "Test Response From Ktor By Samsad" }
            }

            post("/rassack") {
                call.respondText { "Abdul Rassak is Very Good Android Developer" }
            }

            post("/rassack2") {
                val parameters = call.receiveParameters()
                val firstParameter = parameters["value1"]
                val secondParameter = parameters["value2"]

                call.respondText { "Abdul Rassak is Very Good Android Developer who give me $firstParameter and $secondParameter" }
            }

            get("/person/rassack") {
                val person = Person("Rassak", 25, "Android Engineer")
                call.respond(person)
            }


            //We can group the snippets get and post request toghether,
            // we don’t want to repeat ourselves.
            //We can group routes with the same prefix, using the route(path) { } block. For each HTTP method,
            // there is an overload without the route path argument that we can use at routing leaf nodes:

            route("/snippet") {
                get("/snippets") {
                    call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
                }
                post("/snippets") {
                    val post = call.receive<PostSnippet>()
                    snippets += Snippet(post.snippet.text)
                    call.respond(mapOf("OK" to true))
                }
            }
        }
    }

}

