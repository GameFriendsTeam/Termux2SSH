package ru.gft.termux2ssh

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
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
            sendToTermux(args)
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pendingArgs?.let { sendToTermux(it) }
        } else {
            Toast.makeText(this, "Termux не дал разрешение RUN_COMMAND", Toast.LENGTH_LONG).show()
        }
        finish()
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

    private fun sendToTermux(args: ArrayList<String>) {
        val intent = Intent().apply {
            setClassName("com.termux", "com.termux.app.RunCommandService")
            action = "com.termux.RUN_COMMAND"
            putExtra("com.termux.RUN_COMMAND_PATH", "/data/data/com.termux/files/usr/bin/ssh")
            putExtra("com.termux.RUN_COMMAND_ARGUMENTS", args.toTypedArray())
            putExtra("com.termux.RUN_COMMAND_WORKDIR", "/data/data/com.termux/files/home")
            putExtra("com.termux.RUN_COMMAND_BACKGROUND", false)
            putExtra("com.termux.RUN_COMMAND_SESSION_ACTION", "0")
        }
        try {
            startService(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Не удалось запустить Termux: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}