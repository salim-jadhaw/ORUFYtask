package com.example.homescreen

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.homescreen.databinding.ActivityWebViewScreenBinding
import com.example.homescreen.entity.HistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WebViewScreen : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewScreenBinding
    private var currentUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        currentUrl = intent.getStringExtra("url") ?: run {
            Toast.makeText(this, "No URL provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvUrl.text = currentUrl

        setupWebView()
        binding.webView.loadUrl(currentUrl)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnClose.setOnClickListener { finish() }
    }

    private fun setupWebView() {
        binding.webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
            setSupportZoom(true)
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                url?.let {
                    binding.tvUrl.text = it
                    currentUrl = it
                    saveToHistory(it)
                }
            }
        }
    }

    private fun saveToHistory(url: String) {
        lifecycleScope.launch {
            try {
                Log.d("WebViewScreen", "Saving history: $url")

                val time = SimpleDateFormat(
                    "dd-MM-yyyy HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())

                val entity = HistoryEntity(
                    url = url,
                    time = time
                )

                withContext(Dispatchers.IO) {
                    AppDatabase
                        .getDb(this@WebViewScreen)
                        .historyDao()
                        .insert(entity)
                }

                Toast.makeText(
                    this@WebViewScreen,
                    "✓ History saved",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Log.e("WebViewScreen", "Insert failed", e)
            }
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
