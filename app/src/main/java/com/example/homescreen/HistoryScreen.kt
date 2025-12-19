package com.example.homescreen

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homescreen.databinding.ActivityHistoryScreenBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryScreen : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryScreenBinding
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setupRecyclerView()
        loadHistory()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.dlt.setOnClickListener {
            clearHistory()
        }

        binding.ivUpload.setOnClickListener {
            uploadHistory()
        }
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(mutableListOf())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HistoryScreen)
            adapter = historyAdapter
        }
    }

    private fun loadHistory() {
        lifecycleScope.launch {
            try {
                Log.d("HistoryScreen", "Loading history...")

                val db = AppDatabase.getDb(this@HistoryScreen)

                val historyList = withContext(Dispatchers.IO) {
                    db.historyDao().getAll()
                }

                if (historyList.isNotEmpty()) {
                    historyAdapter.updateList(historyList)
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                } else {
                    binding.recyclerView.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                }

            } catch (e: Exception) {
                Log.e("HistoryScreen", "Load error", e)
                Toast.makeText(
                    this@HistoryScreen,
                    "Error loading history",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun clearHistory() {
        lifecycleScope.launch {
            try {
                val db = AppDatabase.getDb(this@HistoryScreen)

                withContext(Dispatchers.IO) {
                    db.historyDao().clearAll()
                }

                historyAdapter.updateList(emptyList())
                binding.recyclerView.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE

                Toast.makeText(
                    this@HistoryScreen,
                    "History cleared",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Toast.makeText(
                    this@HistoryScreen,
                    "Failed to clear history",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun uploadHistory() {
        val list = historyAdapter.getHistoryList()
        if (list.isEmpty()) {
            Toast.makeText(this, "No history to upload", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(
            this,
            "Uploading ${list.size} items...",
            Toast.LENGTH_SHORT
        ).show()

        // TODO: Beeceptor API call
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }
}
