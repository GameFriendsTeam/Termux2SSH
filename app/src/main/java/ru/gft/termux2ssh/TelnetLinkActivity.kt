package ru.gft.termux2ssh

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class TelnetLinkActivity : AppCompatActivity() {
    private var pendingArgs:ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri: Uri? = intent?.data
        if (uri == null) { finish(); return }

        val args = buildTelnetArgs(uri)

        if (ContextCompat.checkSelfPermission(this, "com.termux.permission.RUN_COMMAND")
            != PackageManager.PERMISSION_GRANTED) {
            pendingArgs = args
            ActivityCompat.requestPermissions(this, arrayOf("com.termux.permission.RUN_COMMAND"), 1)
        } else {
            TermuxHelper.sendToTermux(this, this, "telnet", args)
            finish()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        TermuxHelper.onRequestPermissionsResult(
            this, this, "telnet", pendingArgs, requestCode, permissions, grantResults
        )
    }


    private fun buildTelnetArgs(uri: Uri): ArrayList<String> {
        val args = ArrayList<String>()
        val userInfo = uri.userInfo
        if (!userInfo.isNullOrEmpty()) {
            args.add("-l")
            args.add(userInfo)
        }
        val host = uri.host ?: ""
        args.add(host)
        if (uri.port != -1) {
            args.add(uri.port.toString())
        }
        return args
    }
}