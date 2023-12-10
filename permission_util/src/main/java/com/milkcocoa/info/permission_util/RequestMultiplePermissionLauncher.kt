package com.milkcocoa.info.permission_util

import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.IllegalStateException
import kotlin.coroutines.resume

/**
 * RequestPermissionLauncher
 * @author keita
 * @since 2023/12/10 14:48
 */

/**
 *
 */
class RequestMultiplePermissionLauncher
private constructor(
    private val permissions: Array<String>,
) {
    private lateinit var requestPermissionResultLauncher: ActivityResultLauncher<Array<String>>
    private var continuation: CancellableContinuation<Map<String, PermissionResult>>? = null

    /**
     * constructor which called from fragment
     * @param fragment[Fragment] caller fragment.
     * @param permissions[String] permission to request
     */
    constructor(fragment: Fragment, permissions: List<String>) : this(permissions.toTypedArray()) {
        requestPermissionResultLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
                results.map {
                    PermissionStore.storeState(fragment.requireContext(), it.key)
                    if (it.value) {
                        it.key to PermissionResult.PERMISSION_GRANTED
                    } else {
                        if (fragment.shouldShowRequestPermissionRationale(it.key)) {
                            it.key to PermissionResult.PERMISSION_DENIED
                        } else {
                            it.key to PermissionResult.PERMISSION_ABSOLUTELY_DENIED
                        }
                    }
                }.let {
                    mapOf(*it.toTypedArray())
                }.run {
                    continuation?.resume(this)
                    continuation = null
                }
            }
    }

    /**
     * constructor which called from activity
     * @param activity[AppCompatActivity] caller activity.
     * @param permissions[String] permission to request
     */
    constructor(
        activity: AppCompatActivity,
        permissions: List<String>
    ) : this(permissions.toTypedArray()) {
        requestPermissionResultLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
                results.map {
                    PermissionStore.storeState(activity, it.key)
                    if (it.value) {
                        it.key to PermissionResult.PERMISSION_GRANTED
                    } else {
                        if (activity.shouldShowRequestPermissionRationale(it.key)) {
                            it.key to PermissionResult.PERMISSION_DENIED
                        } else {
                            it.key to PermissionResult.PERMISSION_ABSOLUTELY_DENIED
                        }
                    }
                }.let {
                    mapOf(*it.toTypedArray())
                }.run {
                    continuation?.resume(this)
                    continuation = null
                }
            }
    }
    /**
     * request permission.
     * @return mapOf([String], [PermissionResult]) :
     * [PermissionResult.PERMISSION_GRANTED] if requested permission is granted,
     * [PermissionResult.PERMISSION_DENIED] if permission denied ,
     * [PermissionResult.PERMISSION_ABSOLUTELY_DENIED] if denied with 'do NOT show'.
     * the key is permission name
     * @throws IllegalStateException when double launch
     */
    suspend fun launch(): Map<String, PermissionResult> {
        continuation?.let { throw IllegalStateException("this launcher has been started.") }

        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {
                this.continuation = null
            }

            this.continuation = continuation
            requestPermissionResultLauncher.launch(permissions)
        }
    }

    fun checkSelfPermission(activity: AppCompatActivity): Map<String, PermissionStatus>{
        return permissions.map {  permission ->
            if(activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) return@map permission to PermissionStatus.PERMISSION_GRANTED
            if(PermissionStore.loadState(activity, permission).not()) return@map permission to PermissionStatus.PERMISSION_NOT_ASKED
            if(activity.shouldShowRequestPermissionRationale(permission)) return@map permission to PermissionStatus.PERMISSION_DENIED
            return@map permission to PermissionStatus.PERMISSION_ABSOLUTELY_DENIED
        }.let { mapOf(*it.toTypedArray()) }
    }

    fun checkSelfPermission(fragment: Fragment): Map<String, PermissionStatus>{
        return permissions.map {  permission ->
            if(fragment.requireContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) return@map permission to PermissionStatus.PERMISSION_GRANTED
            if(PermissionStore.loadState(fragment.requireContext(), permission).not()) return@map permission to PermissionStatus.PERMISSION_NOT_ASKED
            if(fragment.shouldShowRequestPermissionRationale(permission)) return@map permission to PermissionStatus.PERMISSION_DENIED
            return@map permission to PermissionStatus.PERMISSION_ABSOLUTELY_DENIED
        }.let { mapOf(*it.toTypedArray()) }
    }
}
