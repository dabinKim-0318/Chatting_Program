package com.example.chatting_program

import android.app.Activity
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.example.chatting_program.databinding.ActivityMain2Binding
import com.example.chatting_program.databinding.ActivityUpupBinding
import java.io.IOException
import java.net.Socket

class Upup : AppCompatActivity() {
    lateinit var binding: ActivityUpupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val msg = msg.data.getString("msg")
                if (msg != null) {
                    binding.tvMessage.append(msg + "\n")
                }
            }
        }
        initNetwork2(this, binding, handler).start()
    }

    companion object {
        const val IP = "192.168.80.1"
        const val PORT = 8789
    }
}

class initNetwork2(val context: Activity, val binding: ActivityUpupBinding, val handler: Handler) {
    var connect = false
    var socket: Socket? = null

    // 클라이언트 프로그램 동작 메소드
    fun startClient(IP: String, port: Int) {
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    socket = Socket("192.168.80.1", 8789)
                    receive()
                } catch (e: Exception) {
                    Log.d("소켓전송 실패", "실패")
                    e.printStackTrace()
                }
            }
        }
        thread.start()
    }

    // 클라이언트 프로그램 종료 메소드
    fun stopClient() {
        try {
            if (socket != null && !socket!!.isClosed) {
                socket!!.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 서버로부터 메세지를 전달받는 메소드
    fun receive() {
        while (true) {
            try {
                val input = socket!!.getInputStream()
                val buffer = ByteArray(512)
                val length = input.read(buffer)
                if (length == -1) throw IOException()
                val message = String(buffer, 0, length, charset("UTF-8"))

                val msg: Message = handler.obtainMessage()
                val bundle = Bundle()
                bundle.putString("msg", message)
                msg.setData(bundle)
                handler.sendMessage(msg)

            } catch (e: Exception) {
                e.printStackTrace()
                stopClient()
                break
            }
        }
    }

    // 서버로 메세지를 전송하는 메소드
    fun send(message: String) {
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    val out = socket!!.getOutputStream()
                    val buffer: ByteArray = message.toByteArray(charset("UTF-8"))
                    out.write(buffer)
                    out.flush()
                } catch (e: Exception) {
                    stopClient()
                }
            }
        }
        thread.start()
    }

    fun start() {
        //접속 클릭하면 startClient 실행
        binding.btConnect.setOnClickListener {
            if (connect == false) {
                try {
                    startClient(MainActivity.IP, MainActivity.PORT)
                    binding.tvMessage.setText("[채팅방 접속]\n")
                    binding.btConnect.setText("종료하기")
                    connect = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                binding.tvMessage.append("[채팅방 종료]")
                binding.btConnect.setText("접속하기")
                connect = false
                stopClient()
            }
        }

        //보내기 버튼 누르면 send실행
        binding.btSend.setOnClickListener {
            send(binding.etInput.getText().toString() + "\n")
            //     binding.tvMessage.append(":"+binding.etInput.text+"\n")
            binding.etInput.setText("")
        }
    }
}
