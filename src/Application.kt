package com.samsad

import LoginRegister
import User
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.SerializationFeature
import com.samsad.model.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt
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
import users
import java.text.DateFormat

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

open class SimpleJWT(val secret: String) {
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).build()
    fun sign(name: String): String = JWT.create().withClaim("name", name).sign(algorithm)
}


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

    /*//Basic Auth
    install(Authentication) {
        basic {
            realm = "myrealm"
            validate { if (it.name == "samsad" && it.password == "123") UserIdPrincipal("user") else null }
        }
    }*/

    val simpleJwt = SimpleJWT("my-super-secret-for-jwt")
    install(Authentication) {
        jwt {
            verifier(simpleJwt.verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("name").asString())
            }
        }
    }



    val client = HttpClient(Apache) {
        routing {

            get("/") {
                //Text respose
                call.respondText { "Hello Ktor" }
            }

            get("/map") {
                call.respond(mapOf("response" to "samsad"))
            }

            get("/map2") {
                call.respond(mapOf(listOf("Samsad", "Abdul Rahman") to "samsad"))
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

            get("/persons2") {
                var persons = ArrayList<Person>()

                for (i in 1..10) {
                    val person = Person("Samsad $i", i, "Android Developer")
                    persons.add(person)
                }

                var postPersons = PostPersons(persons)

                call.respond(postPersons)
            }


            get("/response") {
                var modelResponseModel = ResponseModel(200, "Ok")
                call.respond(mapOf("response" to modelResponseModel))
                /*   {
                        "response" : {
                        "message" : "Ok",
                        "code" : 200
                      }
                    }
              */
            }

            get("/responseWithoutMap") {
                var modelResponseModel = ResponseModel(200, "Ok")
                call.respond(modelResponseModel)
                /*  {
                       "message" : "Ok",
                       "code" : 200
                    }
                */
            }

            //Authentication using Basic Auth

            route("/auth") {
                get {
                    call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
                }
                authenticate {
                    post {
                        val post = call.receive<PostSnippet>()
                        snippets += Snippet(post.snippet.text)
                        call.respond(mapOf("OK" to true))
                    }
                }
            }

            post("/login") {
                val post = call.receive<LoginRegister>()
                val user = users.getOrPut(post.user) { User(post.user, post.password) }
                if (user.password != post.password) error("Invalid credentials")
                call.respond(mapOf("token" to simpleJwt.sign(user.name)))
            }
        }
    }



}

