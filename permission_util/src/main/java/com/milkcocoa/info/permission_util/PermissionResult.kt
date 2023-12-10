package com.milkcocoa.info.permission_util

/**
 * PermissionResult
 * @author keita
 * @since 2023/12/10 14:47
 */

/**
 *
 */
enum class PermissionResult {
    // requested permission is granted
    PERMISSION_GRANTED,
    // requested permission is denied
    PERMISSION_DENIED,
    // requested permission is denied with 'do NOT show'
    PERMISSION_ABSOLUTELY_DENIED,
}
