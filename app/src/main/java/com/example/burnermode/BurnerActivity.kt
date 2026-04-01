package com.example.burnermode

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import info.guardianproject.netcipher.proxy.OrbotHelper
import info.guardianproject.netcipher.webkit.WebkitProxy
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executor

class BurnerActivity : AppCompatActivity() {
    private lateinit var prefs: EncryptedPrefsManager
    private lateinit var root: View
    private lateinit var toggle: SwitchMaterial
    private lateinit var statusTv: TextView
    private lateinit var timerTv: TextView
    private lateinit var panicBtn: Button
    private lateinit var dashboardGrid: GridLayout
    private lateinit var btnBrowser: LinearLayout
    private lateinit var btnNotes: LinearLayout
    private lateinit var btnContacts: LinearLayout
    private lateinit var btnPhotos: LinearLayout

    private val handler = Handler(Looper.getMainLooper())
    private var startTime: Long = 0L
    private var running = false
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var pendingEnableByAuth = false

    private val tick = object : Runnable {
        override fun run() {
            val elapsed = if (running) System.currentTimeMillis() - startTime else 0L
            timerTv.text = formatElapsed(elapsed)
            handler.postDelayed(this, 1000)
        }
    }

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let { savePhotoToVault(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_burner)

        prefs = EncryptedPrefsManager(this)
        
        root = findViewById(R.id.rootLayout)
        toggle = findViewById(R.id.switchBurner)
        statusTv = findViewById(R.id.tvStatus)
        timerTv = findViewById(R.id.tvTimer)
        dashboardGrid = findViewById(R.id.dashboardGrid)
        btnBrowser = findViewById(R.id.btnBrowser)
        btnNotes = findViewById(R.id.btnNotes)
        btnContacts = findViewById(R.id.btnContacts)
        btnPhotos = findViewById(R.id.btnPhotos)
        panicBtn = findViewById(R.id.btnPanic)

        val active = prefs.isBurnerActive()
        if (active) {
            startTime = prefs.getStartTime().takeIf { it > 0L } ?: System.currentTimeMillis()
            running = true
            applyBurnerUI(true)
            handler.post(tick)
            toggle.isChecked = true
        } else {
            applyBurnerUI(false)
            toggle.isChecked = false
        }

        setupBiometric()

        btnNotes.setOnClickListener { showNotesDialog() }
        btnBrowser.setOnClickListener { 
            checkTorAndShowBrowser("https://duckduckgo.com", "Stealth Browser (Tor Enabled)") 
        }
        btnContacts.setOnClickListener { showContactsDialog() }
        btnPhotos.setOnClickListener { showPhotoVaultDialog() }
        
        panicBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("Purge all secure data and reset system?")
                .setPositiveButton("PURGE") { _, _ -> performPanicWipe() }
                .setNegativeButton("CANCEL", null)
                .show()
        }

        toggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !running) {
                pendingEnableByAuth = true
                biometricPrompt.authenticate(promptInfo)
            } else if (!isChecked && running) {
                performDisable()
            }
        }
    }

    private fun setupBiometric() {
        val executor: Executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this@BurnerActivity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    runOnUiThread { showPinEntryDialog() }
                    pendingEnableByAuth = false
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    pendingEnableByAuth = false
                    runOnUiThread { toggle.isChecked = false }
                }
            })

        val builder = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Burner OS Login")
            .setSubtitle("Authorize access to secure environment")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            builder.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        } else {
            @Suppress("DEPRECATION")
            builder.setDeviceCredentialAllowed(true)
        }
        promptInfo = builder.build()
    }

    private fun applyBurnerUI(active: Boolean) {
        if (active) {
            root.setBackgroundColor(Color.parseColor("#1A0000"))
            statusTv.text = "BURNER OS ACTIVE"
            statusTv.setTextColor(Color.RED)
            timerTv.setTextColor(Color.WHITE)
            dashboardGrid.visibility = View.VISIBLE
        } else {
            root.setBackgroundColor(Color.WHITE)
            statusTv.text = "SYSTEM INACTIVE"
            statusTv.setTextColor(Color.GRAY)
            timerTv.setTextColor(Color.BLACK)
            dashboardGrid.visibility = View.GONE
        }
    }

    private fun showNotesDialog() {
        val input = EditText(this)
        input.setText(prefs.getSecureNote())
        input.hint = "Type secure notes here..."
        input.setPadding(32, 32, 32, 32)

        AlertDialog.Builder(this)
            .setTitle("Secure Note Vault")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                prefs.saveSecureNote(input.text.toString())
                Toast.makeText(this, "Encrypted & Saved", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showContactsDialog() {
        val input = EditText(this)
        input.setText(prefs.getContacts())
        input.hint = "Name: 555-0199\nName2: 555-0123"
        input.setPadding(32, 32, 32, 32)

        AlertDialog.Builder(this)
            .setTitle("Secure Contacts")
            .setMessage("These contacts exist ONLY in this encrypted vault.")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                prefs.saveContacts(input.text.toString())
                Toast.makeText(this, "Contacts Encrypted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showPhotoVaultDialog() {
        val photoDir = prefs.getPhotoDir()
        val photos = photoDir.listFiles() ?: arrayOf()

        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(16, 16, 16, 16)

        val btnCapture = Button(this)
        btnCapture.text = "Capture Secret Photo"
        btnCapture.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePhotoLauncher.launch(intent)
        }
        container.addView(btnCapture)

        val grid = GridLayout(this)
        grid.columnCount = 3
        photos.forEach { file ->
            val iv = ImageView(this)
            val lp = GridLayout.LayoutParams()
            lp.width = 250
            lp.height = 250
            lp.setMargins(8, 8, 8, 8)
            iv.layoutParams = lp
            iv.scaleType = ImageView.ScaleType.CENTER_CROP
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            iv.setImageBitmap(bitmap)
            grid.addView(iv)
        }
        container.addView(grid)

        AlertDialog.Builder(this)
            .setTitle("Secret Photo Vault")
            .setView(container)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun savePhotoToVault(bitmap: Bitmap) {
        val file = File(prefs.getPhotoDir(), "IMG_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        Toast.makeText(this, "Photo Locked in Vault", Toast.LENGTH_SHORT).show()
    }

    private fun checkTorAndShowBrowser(initialUrl: String, title: String) {
        if (!OrbotHelper.get(this).isOrbotInstalled) {
            AlertDialog.Builder(this)
                .setTitle("Tor Connectivity")
                .setMessage("For maximum stealth, Burner OS uses Tor. Please install Orbot to enable untraceable browsing.\n\nContinue without Tor?")
                .setPositiveButton("Continue (Unsecured)") { _, _ -> showBrowserDialog(initialUrl, title, false) }
                .setNegativeButton("Install Orbot") { _, _ -> OrbotHelper.get(this).promptInstallOrbot(this) }
                .show()
        } else {
            showBrowserDialog(initialUrl, title, true)
        }
    }

    private fun showBrowserDialog(initialUrl: String, title: String, useTor: Boolean) {
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(16, 16, 16, 16)

        val urlBar = EditText(this)
        urlBar.hint = "Enter URL or Search query"
        urlBar.setText(initialUrl)
        urlBar.setSingleLine()
        
        val webView = WebView(this)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false
            }
        }
        
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            databaseEnabled = false
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
        }

        if (useTor) {
            try {
                WebkitProxy.setProxy("com.example.burnermode", applicationContext, webView, "127.0.0.1", 8118)
                Toast.makeText(this, "Tor Proxy Active", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Tor Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        
        urlBar.setOnEditorActionListener { _, _, _ ->
            val query = urlBar.text.toString()
            if (query.startsWith("http")) {
                webView.loadUrl(query)
            } else {
                webView.loadUrl("https://duckduckgo.com/?q=$query")
            }
            true
        }

        container.addView(urlBar)
        container.addView(webView, ViewGroup.LayoutParams.MATCH_PARENT, 1200)

        webView.loadUrl(initialUrl)

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(container)
            .setNegativeButton("Exit & Purge Session") { _, _ ->
                webView.clearCache(true)
                webView.clearHistory()
                webView.clearFormData()
                Toast.makeText(this, "Session data purged", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun performEnable() {
        prefs.setBurnerActive(true)
        startTime = System.currentTimeMillis()
        prefs.setStartTime(startTime)
        running = true
        applyBurnerUI(true)
        handler.post(tick)
    }

    private fun performDisable() {
        running = false
        handler.removeCallbacks(tick)
        prefs.setBurnerActive(false)
        applyBurnerUI(false)
        toggle.isChecked = false
    }

    private fun performPanicWipe(isDuress: Boolean = false) {
        handler.removeCallbacks(tick)
        prefs.clearAll()
        running = false
        startTime = 0L
        timerTv.text = "00:00:00"
        toggle.isChecked = false

        if (isDuress) {
            val intent = Intent(this, CalculatorActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            root.animate().alpha(0f).setDuration(300).withEndAction {
                applyBurnerUI(false)
                root.alpha = 1f
                Snackbar.make(root, "SYSTEM PURGED: ALL DATA DELETED", Snackbar.LENGTH_LONG).show()
            }.start()
        }
    }

    private fun showPinEntryDialog() {
        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        input.hint = "Enter System PIN"

        AlertDialog.Builder(this)
            .setTitle("Authentication Required")
            .setMessage("Default: 1234 | Duress: 9999")
            .setView(input)
            .setPositiveButton("GO") { _, _ ->
                val entered = input.text.toString()
                when {
                    entered == prefs.getDuressPin() -> performPanicWipe(true)
                    entered == prefs.getPin() -> {
                        performEnable()
                        toggle.isChecked = true
                    }
                    else -> {
                        Toast.makeText(this, "ACCESS DENIED", Toast.LENGTH_SHORT).show()
                        toggle.isChecked = false
                    }
                }
            }
            .setNegativeButton("CANCEL") { _, _ -> toggle.isChecked = false }
            .setCancelable(false)
            .show()
    }

    private fun formatElapsed(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
