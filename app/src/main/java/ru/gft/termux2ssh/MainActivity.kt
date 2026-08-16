package ru.gft.termux2ssh

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)

        findViewById<Button>(R.id.btnGrant).setOnClickListener {
            ActivityCompat.requestPermissions(
                this, arrayOf("com.termux.permission.RUN_COMMAND"), 1
            )
        }

//        findViewById<Button>(R.id.btnTest).setOnClickListener {
//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("ssh://test@192.168.1.10:22")))
//        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        updateStatus()
    }

    private fun updateStatus() {
        val granted = ContextCompat.checkSelfPermission(
            this, "com.termux.permission.RUN_COMMAND"
        ) == PackageManager.PERMISSION_GRANTED

        val termuxInstalled = try {
            packageManager.getPackageInfo("com.termux", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

        tvStatus.text = buildString {
            append(if (termuxInstalled) "✅ Termux has been installed\n" else "❌ Termux not found\n")
            append(if (granted) "✅ has RUN_COMMAND permission" else "⚠️ RUN_COMMAND permission denied")
        }
    }
}