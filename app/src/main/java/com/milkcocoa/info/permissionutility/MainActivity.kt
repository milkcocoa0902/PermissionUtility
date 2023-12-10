package com.milkcocoa.info.permissionutility

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.milkcocoa.info.permission_util.RequestPermissionLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        RequestPermissionLauncher(
            this,
            permission = android.Manifest.permission.CAMERA
        ).let {
            CoroutineScope(Dispatchers.Main).launch {
                it.launch().let {
                    Log.i("MainActivity", it.toString())
                }
            }
        }

    }
}
