package com.thyo.animestream.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.thyo.animestream.AcraApplication
import com.thyo.animestream.BuildConfig
import com.thyo.animestream.CommonActivity.showToast
import com.thyo.animestream.app
import com.thyo.animestream.mvvm.logError
import com.thyo.animestream.utils.AppUtils.tryParseJson
import com.thyo.animestream.utils.Coroutines.ioSafe
import com.thyo.animestream.utils.Coroutines.main
import kotlinx.coroutines.delay

class InAppUpdater {
    companion object {

        suspend fun Activity.runAutoUpdate(checkAutoUpdate: Boolean = true): Boolean {
            val currentTime = System.currentTimeMillis()
            val lastUpdateCheck = AcraApplication.getKey<Long>("LastUpdateCheckTime") ?: 0L

            if (!checkAutoUpdate || currentTime - lastUpdateCheck > 10000) { 
                AcraApplication.setKey("LastUpdateCheckTime", currentTime)
                
                ioSafe {
                    try {
                        val response = app.get("https://extension-thyoapp.up.railway.app/update.json").text
                        val json = tryParseJson<Map<String, String>>(response)
                        
                        if (json != null) {
                            val latestVersion = json["version"] ?: BuildConfig.VERSION_NAME
                            val updateUrl = json["url"] ?: ""
                            val updateMessage = json["message"] ?: "+ Pembaruan sistem\n+ Perbaikan bug"

                            if (latestVersion != BuildConfig.VERSION_NAME && updateUrl.isNotEmpty()) {
                                // MENCEGAH NUMPUK DENGAN SPLASH SCREEN
                                // Tunggu 5.5 detik agar video splash screen selesai dulu
                                delay(5500) 
                                
                                main {
                                    showDynamicIslandUpdate(latestVersion, updateUrl, updateMessage)
                                }
                            } else if (!checkAutoUpdate) {
                                main {
                                    showToast("Aplikasi sudah versi terbaru", Toast.LENGTH_SHORT)
                                }
                            }
                        }
                    } catch (e: Exception) { 
                        logError(e) 
                    }
                }
                return true
            }
            return false
        }

        // ========================================================
        // UI ALA DYNAMIC ISLAND iPHONE OLEH THYO
        // ========================================================
        private fun Activity.showDynamicIslandUpdate(latestVersion: String, updateUrl: String, updateMessage: String) {
            val rootView = findViewById<ViewGroup>(android.R.id.content)
            
            // Helper untuk mengukur dp ke pixel agar presisi di semua layar
            val density = resources.displayMetrics.density
            fun dp(v: Int) = (v * density).toInt()

            // 1. CONTAINER UTAMA (Pembungkus Dynamic Island)
            val islandContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                    topMargin = dp(30) // Turun sedikit dari atas layar
                }
                elevation = dp(20).toFloat()
                z = dp(20).toFloat()
                setPadding(dp(20), dp(12), dp(20), dp(12))
            }

            // Background melengkung hitam khas Dynamic Island
            val bgDrawable = GradientDrawable().apply {
                setColor(Color.parseColor("#000000")) 
                cornerRadius = dp(100).toFloat() // Sangat bulat seperti pil awalnya
            }
            islandContainer.background = bgDrawable

            // 2. TAMPILAN SINGKAT (PILL / COLLAPSED)
            val collapsedView = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(-2, -2)
            }
            
            val iconPill = TextView(this).apply {
                text = "✨ "
                textSize = 14f
            }
            
            val textPill = TextView(this).apply {
                text = "Pembaruan Tersedia"
                setTextColor(Color.WHITE)
                textSize = 14f
                setTypeface(null, Typeface.BOLD)
            }
            
            collapsedView.addView(iconPill)
            collapsedView.addView(textPill)

            // 3. TAMPILAN DIPERLUAS (EXPANDED DETAIL)
            val expandedView = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(dp(320), -2) // Ukuran layar lanskap detail
                visibility = View.GONE
            }

            val titleView = TextView(this).apply {
                text = "Pembaruan Sistem ($latestVersion)"
                textSize = 16f
                setTextColor(Color.WHITE)
                setTypeface(null, Typeface.BOLD)
                setPadding(0, 0, 0, dp(8))
            }

            val messageView = TextView(this).apply {
                text = updateMessage
                textSize = 13f
                setTextColor(Color.LTGRAY)
                setTypeface(null, Typeface.ITALIC) 
                setPadding(0, 0, 0, dp(16))
            }

            // Tata letak tombol (Batal & Update)
            val buttonLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.END
            }

            val btnCancel = TextView(this).apply {
                text = "Nanti"
                setTextColor(Color.GRAY)
                textSize = 14f
                setPadding(dp(16), dp(8), dp(16), dp(8))
                setOnClickListener {
                    // Animasi menghilang ke atas saat batal
                    islandContainer.animate().translationY(-dp(100).toFloat()).alpha(0f).setDuration(300)
                        .withEndAction { rootView.removeView(islandContainer) }.start()
                }
            }

            val btnUpdate = TextView(this).apply {
                text = "Update"
                setTextColor(Color.BLACK)
                textSize = 14f
                setTypeface(null, Typeface.BOLD)
                setBackgroundColor(Color.WHITE)
                setPadding(dp(20), dp(8), dp(20), dp(8))
                
                // Bikin sudut tombol update melengkung
                background = GradientDrawable().apply {
                    setColor(Color.WHITE)
                    cornerRadius = dp(20).toFloat()
                }

                setOnClickListener {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl.trim()))
                        intent.addCategory(Intent.CATEGORY_BROWSABLE)
                        startActivity(intent)
                    } catch (e: Exception) {}
                    rootView.removeView(islandContainer)
                }
            }

            buttonLayout.addView(btnCancel)
            buttonLayout.addView(btnUpdate)
            
            expandedView.addView(titleView)
            expandedView.addView(messageView)
            expandedView.addView(buttonLayout)

            // Menggabungkan view
            islandContainer.addView(collapsedView)
            islandContainer.addView(expandedView)

            // 4. ANIMASI INTERAKSI (SMOOTH EXPAND)
            islandContainer.setOnClickListener {
                if (expandedView.visibility == View.GONE) {
                    // Perintah gaib Android untuk bikin animasi transisi mulus
                    TransitionManager.beginDelayedTransition(rootView, AutoTransition().apply { duration = 350 })
                    
                    // Mengubah bentuk background dari "Pil" ke "Kotak Melengkung"
                    bgDrawable.cornerRadius = dp(30).toFloat() 
                    
                    collapsedView.visibility = View.GONE
                    expandedView.visibility = View.VISIBLE
                    
                    // Hapus klik agar tidak bisa diciutkan lagi (opsional)
                    islandContainer.isClickable = false 
                }
            }

            // 5. ANIMASI MUNCUL PERTAMA KALI (Turun dari atas)
            islandContainer.translationY = -dp(100).toFloat()
            islandContainer.alpha = 0f
            rootView.addView(islandContainer)
            
            islandContainer.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(500)
                .setInterpolator(android.view.animation.OvershootInterpolator(1.2f)) // Efek mantul dikit
                .start()
        }
    }
}
