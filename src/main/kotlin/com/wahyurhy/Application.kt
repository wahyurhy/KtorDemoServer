package com.wahyurhy

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.html.*
import kotlinx.serialization.Serializable
import java.lang.Exception

fun main() {
    embeddedServer(Netty, port = 8080, watchPaths = listOf("classes", "resources")) {
        install(ContentNegotiation) {
            json()
        }
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(Routing) {
        static {
            resources("static")
        }
        get("/") {
            call.respondText("Hello World Gaes!")
        }
        get("/users/{username}") {
            val username = call.parameters["username"]
            val header = call.request.headers["Connection"]

            if (username == "Admin") {
                call.response.header(name = "CustomHeader", value = "Admin")
                call.respond(message = "Hello Admin", status = HttpStatusCode.OK)
            }

            call.respondText("Hello $username with header: $header")
        }
        get("/user") {
            val name = call.request.queryParameters["name"]
            val age = call.request.queryParameters["age"]
            call.respondText("Hi, my name is $name, I'm $age years old.")
        }
        get("/person") {
            try {
                val person = Person(name = "Wahyu Rahayu ReBorn", age = 23)
                call.respond(message = person, status = HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(message = "${e.message}", status = HttpStatusCode.BadRequest)
            }
        }
        get("/redirect") {
            call.respondRedirect("/moved", permanent = false)
        }
        get("/moved") {
            call.respondText("You have been successfully redirected!")
        }
        get("/welcome") {
            val name = call.request.queryParameters["name"]
            call.respondHtml {
                head {
                    title { +"Custom Title" }
                }
                body {
                    if (name.isNullOrEmpty()) {
                        h3 { +"Welcome bro!" }
                    } else {
                        h3 { +"Welcome $name!" }
                    }

                    p { +"Current directory is: ${System.getProperty("user.dir")}" }
                    img(src = "kimetsu.jpg")
                }
            }
        }
        get("/antiload") {
            call.respondText("Yoyoyo ngga perlu matiin server lagi!")
        }
    }
}

@Serializable
data class Person(
    val name: String,
    val age: Int
)