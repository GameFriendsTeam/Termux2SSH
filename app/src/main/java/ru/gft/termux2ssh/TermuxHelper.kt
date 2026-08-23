package ru.gft.termux2ssh;

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast


class TermuxHelper {
    companion object {
        fun sendToTermux(activity: Activity, cont: Context, run_exec: String, args: ArrayList<String>) {
            val intent = Intent().apply {
                setClassName("com.termux", "com.termux.app.RunCommandService")
                action = "com.termux.RUN_COMMAND"
                putExtra("com.termux.RUN_COMMAND_PATH", "/data/data/com.termux/files/usr/bin/${run_exec}")
                putExtra("com.termux.RUN_COMMAND_ARGUMENTS", args.toTypedArray())
                putExtra("com.termux.RUN_COMMAND_WORKDIR", "/data/data/com.termux/files/home")
                putExtra("com.termux.RUN_COMMAND_BACKGROUND", false)
                putExtra("com.termux.RUN_COMMAND_SESSION_ACTION", "0")
            }
            try {
                activity.startService(intent)
            } catch (e: Exception) {
                Toast.makeText(cont, "Не удалось запустить Termux: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        fun sendsToTermux(activity: Activity, context: Context, commands: List<String>) {
            val script = commands.joinToString(" && ")
            sendToTermux(activity, context, "bash", arrayListOf("-lc", script))
        }


        fun onRequestPermissionsResult(
            activity: Activity, context: Context, run_exec: String, pendingArgs: ArrayList<String>?,
            requestCode: Int, permissions: Array<out String>, grantResults: IntArray
        ) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pendingArgs?.let { TermuxHelper.sendToTermux(activity, context, run_exec, it) }
            } else {
                Toast.makeText(context, "Termux не дал разрешение RUN_COMMAND", Toast.LENGTH_LONG).show()
            }
            activity.finish()
        }
    }
}