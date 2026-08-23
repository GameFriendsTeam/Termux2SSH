package ru.gft.termux2ssh

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SshLinkActivity : AppCompatActivity() {

    private var pendingArgs: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri: Uri? = intent?.data
        if (uri == null) { finish(); return }

        val args = buildSshArgs(uri)

        if (ContextCompat.checkSelfPermission(this, "com.termux.permission.RUN_COMMAND")
            != PackageManager.PERMISSION_GRANTED) {
            pendingArgs = args
            ActivityCompat.requestPermissions(this, arrayOf("com.termux.permission.RUN_COMMAND"), 1)
        } else {
            TermuxHelper.sendToTermux(this, this, "ssh", args)
            finish()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this, "com.termux.permission.RUN_COMMAND")
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf("com.termux.permission.RUN_COMMAND"), 1)
        } else {
            TermuxHelper.onRequestPermissionsResult(
                this, this, "ssh", pendingArgs, requestCode, permissions, grantResults
            )
        }
    }


    private fun buildSshArgs(uri: Uri): ArrayList<String> {
        val args = ArrayList<String>()
        if (uri.port != -1) {
            args.add("-p")
            args.add(uri.port.toString())
        }
        val userInfo = uri.userInfo
        val host = uri.host ?: ""
        args.add(if (!userInfo.isNullOrEmpty()) "$userInfo@$host" else host)
        return args
    }
}