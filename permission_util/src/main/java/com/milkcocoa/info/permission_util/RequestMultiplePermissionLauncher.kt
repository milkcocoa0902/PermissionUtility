package com.milkcocoa.info.permission_util

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
 constructor(activity: AppCompatActivity, permissions: List<String>) : this(permissions.toTypedArray()) {
     requestPermissionResultLauncher =
         activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
             results.map {
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
}
