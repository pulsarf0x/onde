package app.onde.audio

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import app.onde.audio.service.MediaInfoTracker
import app.onde.audio.shizuku.ShizukuManager
import app.onde.audio.util.LogServer
import app.onde.audio.util.Prefs
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = Prefs.NAME
        const val KEY_DEBUG_MODE = "debug_mode"
        const val KEY_MANUAL_HOST = "manual_host"
    }

    private lateinit var prefs: Prefs
    private lateinit var container: LinearLayout
    private val refreshers = mutableListOf<() -> Unit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        prefs = Prefs(this)
        container = findViewById(R.id.settingsContainer)
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.appBar)) { v, insets ->
            v.updatePadding(top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top); insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(container) { v, insets ->
            v.updatePadding(bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom + dp(32)); insets
        }
        build()
    }

    override fun onResume() {
        super.onResume()
        ShizukuManager.refresh()
        refreshers.forEach { it() }
    }

    private fun build() {
        section("Écoute")
        action("Aide et diagnostic", { "Configurer le HomePod mini, lancer votre musique et examiner la connexion" }) {
            startActivity(Intent(this, HelpActivity::class.java))
        }
        slider("Délai audio", "Un délai plus long résiste mieux aux variations du Wi-Fi. Applicable à la prochaine connexion.",
            500f, 4000f, 100f, prefs.latencyMs.toFloat(), { "${it.toInt()} ms" }) { prefs.latencyMs = it.toInt() }
        switch("Audio ALAC", "Transport sans perte. La qualité de la source et la capture Android restent déterminantes.", !prefs.preferPcm) { prefs.preferPcm = !it }
        switch("Privilégier AirPlay 2", "Utiliser AirPlay 2 lorsque les deux protocoles sont disponibles. Applicable à la prochaine connexion.", prefs.preferAirPlay2) { prefs.preferAirPlay2 = it }
        switch("Diffuser aussi les jeux", "Sinon, seul le son multimédia est transmis. Applicable à la prochaine capture.", prefs.includeGames) { prefs.includeGames = it }

        section("Téléphone et batterie")
        switch("Garder le téléphone silencieux", "Couper le haut-parleur du téléphone pendant la diffusion.", prefs.silencePhone) { prefs.silencePhone = it }
        switch("Boutons de volume", "Régler le volume des enceintes avec les boutons du téléphone.", prefs.volumeKeysControlSpeakers) { prefs.volumeKeysControlSpeakers = it }
        slider("Arrêt après silence", "Économiser la batterie lorsqu’aucun son n’est capturé. 0 désactive cet arrêt.",
            0f, 60f, 5f, prefs.autoStopMinutes.toFloat(), { if (it < 1f) "Désactivé" else "${it.toInt()} min" }) { prefs.autoStopMinutes = it.toInt() }
        switch("Reconnecter à l’ouverture", "Retrouver les dernières enceintes utilisées.", prefs.autoConnect) { prefs.autoConnect = it }
        switch("Garder l’écran allumé", "Uniquement lorsque Onde est visible.", prefs.keepScreenOn) { prefs.keepScreenOn = it }
        switch("Afficher le morceau en cours", "Titre et commandes de lecture, si l’accès aux notifications est autorisé.", prefs.showNowPlaying) { prefs.showNowPlaying = it }
        action("Batterie sur Samsung", { "Si l’écoute s’arrête écran verrouillé : Batterie → Non restreinte" }) {
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")))
        }

        section("Intégration Android")
        switch("Enceintes dans les sorties audio", "Afficher les enceintes dans le sélecteur de sortie multimédia Android.", prefs.systemSwitcher) { prefs.systemSwitcher = it }
        action("Titre, artiste et commandes", {
            if (MediaInfoTracker.hasNotificationAccess(this)) "Accès autorisé" else "Facultatif : autoriser l’accès aux notifications"
        }) { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
        switch("Transmettre les métadonnées", "Envoyer le titre et la pochette aux récepteurs compatibles.", prefs.sendMetadata) { prefs.sendMetadata = it }
        action("Tuile des réglages rapides", { "Ajouter un raccourci pour lancer ou arrêter AirPlay" }) {
            if (Build.VERSION.SDK_INT >= 33) requestTile()
            else Toast.makeText(this, "Ouvrez les réglages rapides et ajoutez la tuile Onde.", Toast.LENGTH_LONG).show()
        }

        section("Options avancées")
        action("Shizuku (facultatif)", { shizukuSummary() }) { onShizukuClicked() }
        switch("Capture via Shizuku", "Utiliser ce mode si Shizuku est installé et autorisé. Sinon, utiliser l’autorisation Android standard.", prefs.useShizukuCapture) { prefs.useShizukuCapture = it }
        action("Nom sur les enceintes", { prefs.senderName }) { editSenderName() }
        action("Ajouter une enceinte par IP", { "Pour un récepteur qui n’apparaît pas automatiquement" }) { addManualDevice() }
        action("Version et licence", { "Onde ${BuildConfig.VERSION_NAME} · basé sur CenturyPlay · GNU AGPL v3" }) {
            startActivity(Intent(this, HelpActivity::class.java))
        }
    }

    // ------------------------------------------------------------------ actions

    private fun shizukuSummary(): String = when (ShizukuManager.status.value) {
        ShizukuManager.Status.NOT_INSTALLED -> "Non installé — découvrir cette option"
        ShizukuManager.Status.NOT_RUNNING -> "Installé mais arrêté — ouvrir Shizuku"
        ShizukuManager.Status.NO_PERMISSION -> "En cours — connecter Onde"
        ShizukuManager.Status.READY -> "Connecté — relancer la configuration"
    }

    private fun onShizukuClicked() {
        when (ShizukuManager.status.value) {
            ShizukuManager.Status.NOT_INSTALLED -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://shizuku.rikka.app/")))
            ShizukuManager.Status.NOT_RUNNING -> packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api")?.let { startActivity(it) }
            ShizukuManager.Status.NO_PERMISSION -> ShizukuManager.requestPermission()
            ShizukuManager.Status.READY -> lifecycleScope.launch {
                val report = ShizukuManager.setupPermissions()
                MaterialAlertDialogBuilder(this@SettingsActivity).setTitle(R.string.setup_done).setMessage(report)
                    .setPositiveButton(R.string.ok, null).show()
                refreshers.forEach { it() }
            }
        }
    }

    private fun requestTile() {
        if (Build.VERSION.SDK_INT < 33) return
        val sbm = getSystemService(android.app.StatusBarManager::class.java)
        sbm.requestAddTileService(
            android.content.ComponentName(this, app.onde.audio.service.AirPlayTileService::class.java),
            getString(R.string.tile_label),
            android.graphics.drawable.Icon.createWithResource(this, R.drawable.ic_speaker),
            mainExecutor,
        ) {}
    }

    private fun editSenderName() {
        val input = EditText(this).apply {
            setText(prefs.customSenderName)
            hint = prefs.senderName
        }
        val frame = FrameLayout(this).apply { setPadding(dp(24), dp(8), dp(24), 0); addView(input) }
        MaterialAlertDialogBuilder(this)
            .setTitle("Nom sur les enceintes")
            .setMessage("Nom affiché par les récepteurs compatibles. Laissez vide pour utiliser le nom du téléphone.")
            .setView(frame)
            .setPositiveButton(R.string.ok) { _, _ ->
                prefs.customSenderName = input.text.toString()
                refreshers.forEach { it() }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun addManualDevice() {
        val input = EditText(this).apply {
            hint = getString(R.string.manual_connect_hint)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
            setText(prefs.raw.getString(KEY_MANUAL_HOST, ""))
        }
        val frame = FrameLayout(this).apply { setPadding(dp(24), dp(8), dp(24), 0); addView(input) }
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.manual_connect_title)
            .setView(frame)
            .setPositiveButton(R.string.save_manual_device) { _, _ ->
                val text = input.text.toString().trim()
                val host = text.substringBefore(":")
                val port = text.substringAfter(":", "5000").toIntOrNull() ?: 5000
                if (host.isEmpty()) return@setPositiveButton
                prefs.raw.edit()
                    .putString(KEY_MANUAL_HOST, text)
                    .putString("manual_device_host", host)
                    .putInt("manual_device_port", port)
                    .putBoolean("manual_device_pending", true)
                    .apply()
                Toast.makeText(this, "Ajout : $host:$port", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    // ------------------------------------------------------------------ row builders

    private fun section(title: String) {
        container.addView(TextView(this).apply {
            text = title
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_LabelLarge)
            setTextColor(MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary))
            setPadding(dp(24), dp(24), dp(24), dp(8))
        })
    }

    private fun row(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(dp(24), dp(14), dp(20), dp(14))
        val ripple = android.util.TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, ripple, true)
        setBackgroundResource(ripple.resourceId)
    }

    private fun texts(parent: LinearLayout, title: String, summary: String): TextView {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(TextView(this).apply {
            text = title
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyLarge)
        })
        val sum = TextView(this).apply {
            text = summary
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium)
            setTextColor(MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurfaceVariant))
        }
        col.addView(sum)
        parent.addView(col, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        return sum
    }

    private fun switch(title: String, summary: String, value: Boolean, onChange: (Boolean) -> Unit) {
        val r = row()
        texts(r, title, summary)
        val sw = MaterialSwitch(this).apply { isChecked = value; setOnCheckedChangeListener { _, c -> onChange(c) } }
        r.addView(sw)
        r.setOnClickListener { sw.toggle() }
        container.addView(r)
    }

    private fun action(title: String, summary: () -> String, onClick: () -> Unit) {
        val r = row()
        val sum = texts(r, title, summary())
        refreshers += { sum.text = summary() }
        r.setOnClickListener { onClick() }
        container.addView(r)
    }

    private fun slider(
        title: String, summary: String, from: Float, to: Float, step: Float, value: Float,
        label: (Float) -> String, onChange: (Float) -> Unit,
    ) {
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(14), dp(20), dp(4))
        }
        val head = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        texts(head, title, summary)
        val valueText = TextView(this).apply {
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_LabelLarge)
            setTextColor(MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary))
            setPadding(dp(12), 0, 0, 0)
        }
        head.addView(valueText)
        col.addView(head)
        val sl = Slider(this).apply {
            valueFrom = from; valueTo = to; stepSize = step
            this.value = (Math.round(value / step) * step).coerceIn(from, to)
            labelBehavior = com.google.android.material.slider.LabelFormatter.LABEL_GONE
            isTickVisible = false
            addOnChangeListener { _, v, fromUser -> valueText.text = label(v); if (fromUser) onChange(v) }
        }
        valueText.text = label(sl.value)
        col.addView(sl)
        container.addView(col)
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
