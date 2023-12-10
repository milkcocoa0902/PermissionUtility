package com.milkcocoa.info.permissionutility

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.milkcocoa.info.permission_util.PermissionResult
import com.milkcocoa.info.permission_util.RequestPermissionLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val requestCameraPermission: RequestPermissionLauncher =
            RequestPermissionLauncher(
                activity = this,
                permission = Manifest.permission.CAMERA
            )

        Log.i("MainActivity", requestCameraPermission.checkSelfPermission(this).toString())

        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                requestCameraPermission.launch().let { result ->
                    Log.i("MainActivity", result.toString())
                    when(result){
                        PermissionResult.PERMISSION_GRANTED ->{

                        }
                        PermissionResult.PERMISSION_DENIED ->{

                        }
                        PermissionResult.PERMISSION_ABSOLUTELY_DENIED ->{

                        }
                    }
                }
            }.getOrNull()
        }

    }
}
