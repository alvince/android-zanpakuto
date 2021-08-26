package cn.alvince.zanpakuto.lifecycle

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel

/**
 * Returns application context
 */
val AndroidViewModel.appContext: Context get() = getApplication()

/**
 * Obtains application resources
 */
val AndroidViewModel.resources: Resources get() = getApplication<Application>().resources

/**
 * Returns a localized string from the application's package's default string table.
 *
 * @see Context.getString
 */
fun AndroidViewModel.getString(@StringRes resId: Int): String = getApplication<Application>().getString(resId)

/**
 * Returns a localized formatted string from the application's package's default string table,
 * substituting the format arguments as defined in [java.util.Formatter] and [String.format].
 *
 * @see Context.getString
 */
fun AndroidViewModel.getString(@StringRes resId: Int, vararg formatArgs: Any): String = getApplication<Application>().getString(resId, *formatArgs)
