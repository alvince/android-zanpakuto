package androidx.lifecycle

import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun <T> ViewModel.setTagIfAbsentX(key: String, value: T): T = this.setTagIfAbsent(key, value)

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun <T> ViewModel.getTagX(key: String): T? = this.getTag(key)
