package app.onde.audio

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import app.onde.audio.service.AudioCaptureService
import app.onde.audio.util.LogServer
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.DynamicColors

/** Local setup guide and diagnostic export, useful without adb on the S23. */
class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(20), dp(24), dp(32))
        }
        val scroll = ScrollView(this).apply { addView(content) }
        setContentView(scroll)
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(scroll) { view, insets ->
            val bars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars() or androidx.core.view.WindowInsetsCompat.Type.displayCutout())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        fun text(title: String, body: String) {
            content.addView(TextView(this).apply {
                text = title
                setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleLarge)
                setPadding(0, dp(22), 0, dp(8))
                isAccessibilityHeading = true
            })
            content.addView(TextView(this).apply {
                text = body
                setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyLarge)
            })
        }
        fun button(label: String, click: () -> Unit) {
            content.addView(MaterialButton(this).apply {
                text = label
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12) }
                setOnClickListener { click() }
            })
        }
        text("Bienvenue dans Onde", "Le son de votre téléphone, sur vos enceintes AirPlay. Sans compte et sans limite de durée intégrée. Version de test ${BuildConfig.VERSION_NAME} : la compatibilité avec le Galaxy S23 et le HomePod mini reste à tester.")
        text("1. Le même Wi-Fi", "Connectez le Galaxy et le HomePod au même réseau local. Évitez le réseau invité, qui peut isoler les appareils. Le HomePod doit déjà être configuré.")
        text("2. Autoriser AirPlay", "Sur l’iPhone ou l’iPad qui gère le HomePod : Maison → Réglages du domicile → Haut-parleurs et téléviseurs (le nom peut varier). Autorisez les personnes sur le même réseau. Une restriction aux membres du domicile peut empêcher cette connexion Android.")
        text("3. Choisir le HomePod", "Revenez à l’accueil et touchez votre enceinte. Acceptez la capture audio. Si Android propose de partager une seule app ou tout l’écran, choisissez tout l’écran : cela permet de changer d’app musicale. Onde capture le son de lecture, sans envoyer l’image de l’écran ni enregistrer le microphone.")
        button("Choisir une enceinte") { finish() }
        text("4. Lancer votre musique", "Ouvrez Deezer, Spotify ou YouTube Music et lancez un morceau. Un délai d’environ deux secondes est normal. Le bouton Arrêter et la notification mettent fin à la diffusion.")
        button("Ouvrir mon app musicale") { MusicApps.show(this) }
        text("Connecté, mais aucun son ?", "Regardez la jauge audio sur l’accueil. Si elle reste immobile pendant la musique, l’app source peut bloquer la capture. Si elle bouge mais que le HomePod reste silencieux, la connexion AirPlay doit être examinée. Aucune compatibilité universelle avec les apps ou les contenus protégés n’est garantie.")
        text("Sur le Galaxy S23", "Si la diffusion s’arrête lorsque l’écran se verrouille : Paramètres Samsung → Applications → Onde → Batterie → Non restreinte. Vérifiez également que Onde ne figure pas parmi les applications en veille profonde.")
        button("Réglages Android de Onde") {
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.net.Uri.parse("package:$packageName")))
        }
        text("Diagnostic local", "Les informations techniques restent sur le téléphone. Le bouton ci-dessous ouvre le partage Android : vous choisissez vous-même si vous souhaitez les transmettre. Le rapport contient notamment des adresses réseau et les noms des enceintes.")
        button("Partager un diagnostic") { shareDiagnostics() }
        button("Confidentialité et données") { startActivity(Intent(this, PrivacyActivity::class.java)) }
        button("Code source et licences") {
            startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://github.com/pulsarf0x/onde")))
        }
        text("Crédits et licence", "Onde est une adaptation de CenturyPlay (g8row), sous licence GNU AGPL v3. Le codec ALAC provient d’Apple, sous licence Apache 2.0. Les sources complètes et les notices sont fournies avec cette version. AirPlay et HomePod sont des marques d’Apple ; cette application est indépendante.")
        button("Fermer l’aide") { finish() }
    }

    private fun shareDiagnostics() {
        val capture = AudioCaptureService.state.value
        val session = AudioCaptureService.state.value
        val report = buildString {
            appendLine("Onde ${BuildConfig.VERSION_NAME} — prototype")
            appendLine("${Build.MANUFACTURER} ${Build.MODEL} — Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("Capture : ${capture.capturing}")
            appendLine("Source : ${session.source}")
            appendLine("Niveau capturé : ${AudioCaptureService.level}")
            appendLine("Enceintes : ${session.speakers.size}")
            session.speakers.forEach { speaker ->
                appendLine("${speaker.name} : ${speaker.status} — ${speaker.message.orEmpty()}")
            }
            appendLine("\nJournal technique :")
            append(LogServer.snapshot().joinToString("\n"))
        }
        startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Diagnostic Onde")
            putExtra(Intent.EXTRA_TEXT, report.takeLast(180_000))
        }, "Partager le diagnostic"))
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
