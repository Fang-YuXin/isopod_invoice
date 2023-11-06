package com.example.isopod_invoice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseUser

import com.example.isopod_invoice.databinding.ActivityRegisterBinding //import我們自己的databinding
import okhttp3.*
import java.io.IOException

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //<-----------點桌面鍵盤消失------------S>
        // 獲取最外層的 View
        val rootView: View = findViewById(android.R.id.content)

        // 設置畫面點擊事件監聽器
        rootView.setOnTouchListener { v, event ->
            // 判斷點擊事件的類型
            if (event.action == MotionEvent.ACTION_DOWN) {
                // 獲取輸入法管理器
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                // 隱藏鍵盤
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
            }
            false
        }
        //<-----------點桌面鍵盤消失------------E>

        // Step 1: 先初始化 binding的變數與FirebaseAuth變數
        auth = Firebase.auth
        //println("1")
        //Step 2: 點下signUp後需要取得editview的email與password兩個欄位的值
        binding.signUp.setOnClickListener {

            //step 2: 取得email and password
            val email = binding.email.text.toString()
            val password = binding.pwd.text.toString()
            val uName = binding.name.text.toString()
            val phone = binding.phone.text.toString()

            println(email)
            println(password)

            //step 3: auth內有一個create user using email and password的function
            auth.createUserWithEmailAndPassword(email,password) //把變數email password放進去
                .addOnCompleteListener {
                    //Step 3:在這裡去判斷傳入的值是否有成功，所以要寫一個判斷式
                    if (it.isSuccessful)
                    {
                        val user = it.result?.user
                        val uid = user?.uid
                        println(uid)

                        //新增進去phpmyadmin
                        val clientPost = OkHttpClient()
                        val requestBody = FormBody.Builder()
                            .add("uid", "${uid}")
                            .add("uName", "${uName}")
                            .add("phone", "${phone}")
                            .add("email", "${email}")
                            .add("pwd", "${password}")
                            .build()
                        val requestPost = Request.Builder()
                            .url("https://fangbibi.lionfree.net/app/userinsert.php")
                            .post(requestBody)
                            .build()

                        clientPost.newCall(requestPost).enqueue(object : Callback
                        {
                            override fun onFailure(call: Call, e: IOException)
                            {
                                e.printStackTrace()
                            }

                            override fun onResponse(call: Call, response: Response)
                            {
                                val responseBody = response.body?.string()
                                // Handle the response body
                                println("POST-------${responseBody}")
                            }
                        })

                        Log.d("Test","成功註冊")
                        finish()//註冊成功就關閉這個頁面
                    }
                    else{
                        Log.w("Test","註冊失敗", it.exception)
                        println(it.exception)//it.exception是把錯誤原因記下來
                        showMessage("註冊會員失敗")//去呼叫showMessage來顯示註冊失敗
                    }
                }
        }
    }

    //Step 3:為了讓使用者知道註冊失敗，所以要寫一個"副程式"showMessage給使用者知道自己註冊失敗的訊息
    private fun showMessage(message: String) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("確定") { dialog, which -> }
        alertDialog.show()
    }


}