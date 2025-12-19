package com.example.homescreen

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.homescreen.databinding.ActivityHomeScreenBinding
import com.example.homescreen.entity.HistoryEntity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HomeScreen : AppCompatActivity() {

    private lateinit var binding: ActivityHomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setupViewPager()

        binding.btnOpen.setOnClickListener {
            openUrl()
        }

        binding.historyicon.setOnClickListener {
            startActivity(Intent(this, HistoryScreen::class.java))
        }
    }

    private fun setupViewPager() {
        val images = listOf(
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3
        )

        val pager: ViewPager2 = binding.viewPager
        pager.adapter = ImageAdapter(images)

        TabLayoutMediator(binding.tabDots, pager) { _, _ -> }.attach()
    }

    private fun openUrl() {
        var url = binding.etUrl.text.toString().trim()

        if (url.isEmpty()) {
            Toast.makeText(this, "Please enter URL", Toast.LENGTH_SHORT).show()
            return
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://$url"
        }

        if (!Patterns.WEB_URL.matcher(url).matches()) {
            Toast.makeText(this, "Please enter a valid URL", Toast.LENGTH_SHORT).show()
            return
        }

        saveToHistory(url)

        val intent = Intent(this, WebViewScreen::class.java)
        intent.putExtra("url", url)
        startActivity(intent)
    }

    private fun saveToHistory(url: String) {
        lifecycleScope.launch {
            try {
                Log.d("HomeScreen", "Saving URL: $url")

                val time = SimpleDateFormat(
                    "dd-MM-yyyy HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())

                val historyEntity = HistoryEntity(
                    url = url,
                    time = time
                )

                withContext(Dispatchers.IO) {
                    val db = AppDatabase.getDb(this@HomeScreen)
                    db.historyDao().insert(historyEntity)
                }

                Toast.makeText(
                    this@HomeScreen,
                    "✓ Added to history",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Log.e("HomeScreen", "Insert failed", e)
                Toast.makeText(
                    this@HomeScreen,
                    "Failed to save history",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
