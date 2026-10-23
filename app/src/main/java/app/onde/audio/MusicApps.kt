package app.onde.audio

import android.content.ActivityNotFoundException
import android.content.Context
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Opens installed players; Onde never asks for a streaming-service password. */
object MusicApps {
    private val players = listOf(
        "Deezer" to "deezer.android.app",
        "Spotify" to "com.spotify.music",
        "YouTube Music" to "com.google.android.apps.youtube.music",
    )

    fun show(context: Context) {
        val installed = players.mapNotNull { (name, pkg) ->
            context.packageManager.getLaunchIntentForPackage(pkg)?.let { name to it }
        }
        if (installed.isEmpty()) {
            Toast.makeText(context, R.string.onde_no_music, Toast.LENGTH_LONG).show()
            return
        }
        MaterialAlertDialogBuilder(context)
            .setTitle("Votre musique")
            .setItems(installed.map { it.first }.toTypedArray()) { _, index ->
                try {
                    context.startActivity(installed[index].second)
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(context, R.string.onde_no_music, Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
