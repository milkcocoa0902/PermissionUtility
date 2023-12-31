package com.milkcocoa.info.permission_util

import android.content.Context
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
class RequestPermissionLauncher
private constructor(
    private val permission: String,
) {
    private lateinit var requestPermissionResultLauncher: ActivityResultLauncher<String>
    private var continuation: CancellableContinuation<PermissionResult>? = null

    /**
     * constructor which called from fragment
     * @param fragment[Fragment] caller fragment.
     * @param permission[String] permission to request
     */
    constructor(fragment: Fragment, permission: String) : this(permission) {
        requestPermissionResultLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                PermissionStore.storeState(fragment.requireContext(), permission)
                it.let {
                    if (it) return@let PermissionResult.PERMISSION_GRANTED
                    else if (fragment.shouldShowRequestPermissionRationale(permission)) return@let PermissionResult.PERMISSION_DENIED
                    else return@let PermissionResult.PERMISSION_ABSOLUTELY_DENIED
                }.run {
                    continuation?.resume(this)
                    continuation = null
                }
            }
    }

    /**
     * constructor which called from activity
     * @param activity[AppCompatActivity] caller activity.
     * @param permission[String] permission to request
     */
    constructor(activity: AppCompatActivity, permission: String) : this(permission) {
        requestPermissionResultLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                PermissionStore.storeState(activity, permission)
                it.let {
                    if (it) return@let PermissionResult.PERMISSION_GRANTED
                    else if (activity.shouldShowRequestPermissionRationale(permission)) return@let PermissionResult.PERMISSION_DENIED
                    else return@let PermissionResult.PERMISSION_ABSOLUTELY_DENIED
                }.run {
                    continuation?.resume(this)
                    continuation = null
                }
            }
    }

    /**
     * request permission.
     * @return [PermissionResult] :
     * [PermissionResult.PERMISSION_GRANTED] if requested permission is granted,
     * [PermissionResult.PERMISSION_DENIED] if permission denied ,
     * [PermissionResult.PERMISSION_ABSOLUTELY_DENIED] if denied with 'do NOT show'
     * @throws IllegalStateException when double launch
     */
    suspend fun launch(): PermissionResult {
        continuation?.let { throw IllegalStateException("this launcher has been started.") }

        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {
                this.continuation = null
            }

            this.continuation = continuation
            requestPermissionResultLauncher.launch(permission)
        }
    }

    fun checkSelfPermission(activity: AppCompatActivity): PermissionStatus{
        if(activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) return PermissionStatus.PERMISSION_GRANTED
        if(PermissionStore.loadState(activity, permission).not()) return PermissionStatus.PERMISSION_NOT_ASKED
        if(activity.shouldShowRequestPermissionRationale(permission)) return PermissionStatus.PERMISSION_DENIED
        return PermissionStatus.PERMISSION_ABSOLUTELY_DENIED
    }

    fun checkSelfPermission(fragment: Fragment): PermissionStatus{
        if(fragment.requireContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) return PermissionStatus.PERMISSION_GRANTED
        if(PermissionStore.loadState(fragment.requireContext(), permission).not()) return PermissionStatus.PERMISSION_NOT_ASKED
        if(fragment.shouldShowRequestPermissionRationale(permission)) return PermissionStatus.PERMISSION_DENIED
        return PermissionStatus.PERMISSION_ABSOLUTELY_DENIED
    }
}
