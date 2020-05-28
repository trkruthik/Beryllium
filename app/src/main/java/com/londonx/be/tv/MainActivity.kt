package com.londonx.be.tv

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yanzhenjie.andserver.AndServer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import splitties.systemservices.wifiManager
import splitties.views.textResource
import java.util.concurrent.TimeUnit

private const val SERVER_PORT = 8899

class MainActivity : AppCompatActivity() {
    private val andServer by lazy {
        AndServer.webServer(this)
            .port(SERVER_PORT)
            .timeout(10, TimeUnit.SECONDS)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        andServer.startup()
        MainScope().launch {
            while (!isDestroyed) {
                if (!andServer.isRunning) {
                    tvInfo.textResource = R.string.starting_config_server
                    delay(1000)
                }
                val ip = wifiManager?.connectionInfo?.ipAddress
                if (ip == null) {
                    delay(1000)
                    continue
                }
                val ip4String = intToIP(ip)
                tvInfo.text = getString(R.string.fmt_server_running_at_, ip4String, SERVER_PORT)
                delay(5000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        andServer.shutdown()
    }

    private fun intToIP(ip: Int): String {
        return (ip and 0xFF).toString() + "." +
                (ip shr 8 and 0xFF) + "." +
                (ip shr 16 and 0xFF) + "." +
                (ip shr 24 and 0xFF)
    }
}
