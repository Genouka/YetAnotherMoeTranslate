/*
 * Copyright (C) 2024 murangogo
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.moe.moetranslator.launch

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.moe.moetranslator.BaseActivity
import com.moe.moetranslator.MainActivity
import com.moe.moetranslator.R
import com.moe.moetranslator.utils.AppPathManager
import com.moe.moetranslator.utils.CustomPreference
import com.moe.moetranslator.utils.UtilTools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class LaunchActivity : BaseActivity() {
    private lateinit var prefs: CustomPreference


    // 使用lifecycleScope替代MainScope
    private val activityScope = lifecycleScope


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 锁定竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        // 初始化路径管理类
        AppPathManager.init(this)
        // 初始化工具类
        UtilTools.init(this)
        prefs = CustomPreference.getInstance(this)

        setContentView(R.layout.activity_launch)

        val logoImageView = findViewById<ImageView>(R.id.logo_name)

        if (shouldShowChineseLogo()) {
            logoImageView.setImageResource(R.drawable.logo_design)
        } else {
            logoImageView.setImageResource(R.drawable.logo_design_en)
        }

        applySystemBarsPadding(findViewById(R.id.launch_layout), true, true)

        activityScope.launch {
            try {
                delay(1500)

                if (prefs.getBoolean("Is_First_Run", true)) {
                    prefs.setBoolean("Is_First_Run", false)
                    startActivity(Intent(this@LaunchActivity, FirstLaunchPage::class.java))
                } else {
                    startActivity(Intent(this@LaunchActivity, MainActivity::class.java))
                }
                finish()
            } catch (e: Exception) {
                // 处理初始化失败的情况
                Log.e("LaunchActivity", "Initialization failed", e)
                Toast.makeText(
                    this@LaunchActivity,
                    getString(R.string.init_error, e),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun shouldShowChineseLogo(): Boolean {
        val locale = Locale.getDefault()
        return locale.language == "zh"
    }

    override fun onDestroy() {
        super.onDestroy()
        // 确保Activity销毁时取消所有协程
        activityScope.cancel()
    }
}