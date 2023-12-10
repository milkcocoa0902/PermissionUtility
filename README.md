## Android 'M Permission' Utility

## Features
✅ Work with Kotlin Coroutines  
✅ Get permission request result immediately(with coroutine)  
✅ Get current permission status including 'NOT asked yet' and 'DO NOT ask again'  


## setup
### Add repository dependencies
``` kts
repositories {
    google()
    mavenCentral()
    
    // add this
    maven( url =  "https://jitpack.io" )
}
```

### Add dependencies in your app
```
dependencies{
    implementation("com.github.milkcocoa0902:PermissionUtility:0.1.1")
}
```

now you can use PermissionUtility!

## Work
### create launcher

```
val requestCameraPermission: RequestPermissionLauncher =
    RequestPermissionLauncher(
        activity = this,
        permission = Manifest.permission.CAMERA
    )
```

### check permission status

```
Log.i(
    "MainActivity", 
    requestCameraPermission.checkSelfPermission(this).toString()
)

// PERMISSIN_NOT_ASKED ... did not requested permission at once
// PERMISSION_GRANTED  ... permission will request is granted
// PERMISSION_DENIED   ... permission will request is denied
// PERMISSION ABSOLUTELY_DENIES ... permission will request is denied with 'DO NOT ask again'
```

now you got [PermissionStatus](https://github.com/milkcocoa0902/PermissionUtility/blob/main/permission_util/src/main/java/com/milkcocoa/info/permission_util/PermissionStatus.kt)

### request permission
```
CoroutineScope(Dispatchers.Main).launch {
    runCatching {
        requestCameraPermission.launch().let { result ->
            Log.i("MainActivity", result.toString())
            when(result){
                PermissionResult.PERMISSION_GRANTED ->{
                    // granted permission
                    // do your action with requested permission
                }
                PermissionResult.PERMISSION_DENIED ->{
                    // permission denied
                }
                PermissionResult.PERMISSION_ABSOLUTELY_DENIED ->{
                    // permission denied with 'DO NOT ask again'
                }
            }
        }
    }.getOrNull()
}
```

Note that  
if you call `launch()` when already launched (means trigger button double tapped) this function throws `IllegalStateException` 