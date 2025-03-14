package com.annai.flavorize.utils

import org.gradle.api.GradleException
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

var debugEnable = false
val pluginName = "Annai Flavorize"

fun printDebug(message: String) {
    if(debugEnable) println("📢 $message")
}

fun printWarning(message: String) {
    println("⚠️ $message")
}

fun printError(message: String) {
    println("❌ $message")
}

fun throwError(message: String, exceptionClass: KClass<out Throwable>? = null, e: Exception? = null): Nothing {
    val exception = exceptionClass?.primaryConstructor
        ?.call("❌ $message", e)
        ?: GradleException("❌ $message", e)

    throw exception
}


