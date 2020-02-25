package com.luisansal.jetpack.utils

import androidx.lifecycle.LiveData
import java.io.IOException
import java.util.*

@Throws(IOException::class)
fun Any.readString(path: String): String {
    val stream = this::class.java.classLoader?.getResourceAsStream(path)
    val s = stream?.let { Scanner(it).useDelimiter("\\A") } ?: return ""
    val result = if (s.hasNext()) s.next() else ""
    stream.close()
    return result
}

fun <T> LiveData<T>.observeOnce(onChangeHandler: (T) -> Unit) {
    val observer = OneTimeObserver(handler = onChangeHandler)
    observe(observer, observer)
}