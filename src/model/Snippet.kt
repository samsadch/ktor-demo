package com.samsad.model

import java.util.*

data class Snippet(val text: String)


val snippets = Collections.synchronizedList(mutableListOf(
    Snippet("hello"),
    Snippet("world")
))