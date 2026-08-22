package ru.gft.termux2ssh

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var btnTheme: Button
    private lateinit var infoBtn: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        btnTheme = findViewById(R.id.btnTheme)
        infoBtn = findViewById(R.id.infoBtn)

        findViewById<Button>(R.id.btnGrant).setOnClickListener {
            ActivityCompat.requestPermissions(
                this, arrayOf("com.termux.permission.RUN_COMMAND"), 1
            )
        }

        btnTheme.setOnClickListener {
            val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            val current = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            val next = when (current) {
                AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_NO
                AppCompatDelegate.MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                else -> AppCompatDelegate.MODE_NIGHT_YES
            }
            prefs.edit().putInt(KEY_THEME, next).apply()
            AppCompatDelegate.setDefaultNightMode(next)
            updateThemeButton()
        }

        infoBtn.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
        updateThemeButton()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        updateStatus()
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val mode = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun updateThemeButton() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val current = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        btnTheme.text = when (current) {
            AppCompatDelegate.MODE_NIGHT_YES -> getString(R.string.thm_btn_dark)
            AppCompatDelegate.MODE_NIGHT_NO -> getString(R.string.thm_btn_light)
            else -> getString(R.string.thm_btn_sys)
        }
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
            append(if (termuxInstalled) getString(R.string.termux_installed)+"\n" else getString(R.string.termux_not_installed)+"\n")
            append(if (granted) getString(R.string.has_perm) else getString(R.string.hasnt_perm))
        }
    }

    companion object {
        private const val PREFS_NAME = "ui_prefs"
        private const val KEY_THEME = "night_mode"
    }
}
