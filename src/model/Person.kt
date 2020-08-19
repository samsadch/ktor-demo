package com.samsad.model

import java.util.*
import kotlin.collections.ArrayList

data class Person(var name: String, var age: Int, var job:String)

data class PostPersons(val persons:MutableList<Person>)