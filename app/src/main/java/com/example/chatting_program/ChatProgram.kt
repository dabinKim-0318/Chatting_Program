package com.example.chatting_program

import android.app.Activity
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.example.chatting_program.databinding.ActivityUpupBinding
import java.io.IOException
import java.net.*


class ChatProgram : AppCompatActivity() {
    lateinit var binding: ActivityUpupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //메인쓰레드에서 UI를 그리기 위해 handler 사용
        val handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val msg = msg.data.getString("msg")
                if (msg != null) {
                    binding.tvMessage.append(msg + "\n")
                }
            }
        }
        initNetwork(this, binding, handler).start()
    }
}

class initNetwork(val context: Activity, val binding: ActivityUpupBinding, val handler: Handler) {
    var connect = false
    var socket: Socket? = null

    // 클라이언트 프로그램 시작
    fun clientOn(IPaddress: String, port: Int) {
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    socket = Socket(
                        IPaddress, port
                    )
                    receiveMsg()
                } catch (e: Exception) {
                    Log.d("소켓전송 실패", "실패")
                    e.printStackTrace()
                }
            }
        }
        thread.start()
    }

    // 클라이언트 프로그램 종료
    fun clientOff() {
        try {
            if (socket != null && !socket!!.isClosed) {
                socket!!.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 메시지 받기
    fun receiveMsg() {
        while (true) {
            try {
                val input = socket!!.getInputStream()
                val buf = ByteArray(512)
                val leng = input.read(buf)
                if (leng == -1) throw IOException()
                val message = String(buf, 0, leng, charset("UTF-8"))

                //handler 호출
                val msg: Message = handler.obtainMessage()
                val bundle = Bundle()
                bundle.putString("msg", message)
                msg.setData(bundle)
                handler.sendMessage(msg)

            } catch (e: Exception) {
                e.printStackTrace()
                clientOff()
                break
            }
        }
    }

    // 메시지 보내기
    fun sendMsg(msg: String) {
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    val out = socket!!.getOutputStream()
                    val buf: ByteArray = msg.toByteArray(charset("UTF-8"))
                    out.write(buf)
                    out.flush()
                } catch (e: Exception) {
                    clientOff()
                }
            }
        }
        thread.start()
    }

    fun start() {
        //접속 클릭하면 startClient 실행
        binding.btConnect.setOnClickListener {
            binding.btConnect.toggle()
            if (connect == false) {
                try {
                    clientOn("211.205.151.190", 8765)
                    binding.tvMessage.setText("<상담시작>\n")
                    //     binding.btConnect.setText("종료하기")
                    connect = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                binding.tvMessage.append("<상담종료>")
                connect = false
                clientOff()
            }
        }

        //전송 버튼 누르면 send실행
        binding.btSend.setOnClickListener {
            sendMsg(binding.etInput.getText().toString() + "\n")
            binding.etInput.setText("")
        }
    }
}


