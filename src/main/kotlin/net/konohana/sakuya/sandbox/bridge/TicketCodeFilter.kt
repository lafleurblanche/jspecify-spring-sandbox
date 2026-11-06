package net.konohana.sakuya.sandbox.bridge

@JvmName("filterValidStrings")
fun filterValid(items: List<String>): List<String> = items.filter { it.isNotBlank() }

@JvmName("filterValidNumbers")
fun filterValid(items: List<Int>): List<Int> = items.filter { it > 0 }
