package com.example.isopod_invoice

//import com.google.firebase.auth.FirebaseUser //本次新增
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.isopod_invoice.databinding.ActivityMainBinding
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {

    //Step 1: 初始化FirebaseAuth
    private lateinit var auth: FirebaseAuth

    //Step 1:先從註冊這個功能開始寫->先初始化一個資料鏈結的部分
    private lateinit var binding: ActivityMainBinding

    private lateinit var callbackManager: CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        FacebookSdk.sdkInitialize(applicationContext)
//        AppEventsLogger.activateApp(application)
        setContentView(R.layout.activity_main)





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

        //** Step 2: 去設定我們要去啟動的RegisterActivity **
        //Step 2-1: 初始化FirebaseAuth->初始化auth
        auth = Firebase.auth
        Firebase.initialize(this)
        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        //Step 2-2:先從註冊這個功能開始寫->初始化binding要鏈結的layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Step 2-3:先從註冊這個功能開始寫->先宣告要在"signUp"這個button內寫監聽程式，
        binding.signUp.setOnClickListener {

            // Step 2-4: 宣告一個intent變數，使用Intent去開啟一個我們自己寫好的RegisterActivity
            var intentRegister = Intent(this,Register::class.java)
            // Step 2-5: start這個Activity
            startActivity(intentRegister)
        }

        /*// 配置 Facebook button
        val loginButton = findViewById<LoginButton>(R.id.fbSignIn)
        loginButton.setPermissions("email", "public_profile")
// 註冊 Facebook button的token
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookAccessToken(loginResult.accessToken)
            }
            override fun onCancel() {
// 用户取消登入
            }
            override fun onError(error: FacebookException) {
// 登入錯誤的例外處理
            }
        })*/



        //Step 3-1: 設計登入的button
        binding.signIn.setOnClickListener {
            if (binding.email.text.toString().isEmpty()) {
                showMessage("請輸入帳號")
            } else if (binding.pwd.text.toString().isEmpty()) {
                showMessage("請輸入密碼")
            } else {
                signIn()
            }
        }

        //Step 4: 設計logout button功能
//        binding.logout.setOnClickListener {
//            auth.signOut()
//
//            val user = Firebase.auth.currentUser
//            updateUI(user)
//        }


    }

    //Step 3-2: 設計登入的button->寫一個登入認證失敗的判斷式
    private fun signIn() {
        val email = binding.email.text.toString()
        val password = binding.pwd.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //Log.d("signInWithEmail:success")
                    println("---------signInWithEmail:success-----------")
                    val user = auth.currentUser
                    updateUI(user)

                    val auth1 = FirebaseAuth.getInstance()

// 檢查當前使用者是否已登入

                    val user1 = auth.currentUser
                    if (user1 != null) {
                        // 取得使用者的 UID
                        val uid = user1.uid



//查詢點擊次數number

                        val client = OkHttpClient()
                        val urlBuilder = "https://fangbibi.lionfree.net/app/user_number_select.php".toHttpUrlOrNull()
                            ?.newBuilder()
                            ?.addQueryParameter("uid", "${uid}")



                        val url = urlBuilder?.build().toString()
                        val request = Request.Builder()
                            .url(url)
                            .build()

                        client.newCall(request).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException)
                            {
                                e.printStackTrace()
                            }

                            override fun onResponse(call: Call, response: Response)
                            {

                                if (response.isSuccessful)
                                {
                                    val responseBody = response.body?.string()
                                    //println("-----${responseBody}")

                                    val jsonObject = JSONObject(responseBody)
                                    val dataArray = jsonObject.getJSONArray("data")


                                    runOnUiThread {
                                            for (i in 0 until dataArray.length())
                                            {
                                                val chatObject = dataArray.getJSONObject(i)
                                                val number = chatObject.getInt("number")
                                                var image: String = "baby"
                                                if(number > 9)
                                                {
                                                    image = "lemon3"
                                                }
                                                else if(number > 6)
                                                {
                                                    image = "water2"
                                                }
                                                else if(number > 3)
                                                {
                                                    image = "pandas1"
                                                }
                                                else
                                                {
                                                    image = "baby"
                                                }






                                                val bundle = Bundle()
                                                println(uid)
                                                bundle.putString("key", uid)
                                                //bundle.putInt("id",number)
                                                bundle.putString("image",image)

                                                val intenthomepage = Intent(this@MainActivity, Homepage::class.java)
                                                intenthomepage.putExtras(bundle)
                                                println(bundle)
                                                println(intenthomepage)

                                                startActivity(intenthomepage)


                                            }

                                    }

                                }
                            }


                        })
                    }
                    else {
                        // 使用者尚未登入，處理未登入的情況
                        println("User not logged in")

                    }






                } else {
                    it.exception?.message?.let {  }
                    println("---------error---------------")
                    showMessage("登入失敗，帳號或密碼錯誤")
                    updateUI(null)
                }
            }
    }

    //Step 6: 確認更新登入的user狀況
    private fun updateUI(user: FirebaseUser?) {
        if ( user!= null){
            //已登入
            binding.email.visibility = View.GONE
            binding.pwd.visibility = View.GONE
            binding.signIn.visibility = View.GONE
            binding.signUp.visibility = View.GONE
//            binding.logout.visibility = View.VISIBLE

        }else{
            //未登入
            binding.email.visibility = View.VISIBLE
            binding.pwd.visibility = View.VISIBLE
            binding.signIn.visibility = View.VISIBLE
            binding.signUp.visibility = View.VISIBLE
//            binding.logout.visibility = View.GONE
        }
    }

    //Step 3-3: 設計登入的button->設計一個給使用者確認的message
    private fun showMessage(message: String) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("確定") { dialog, which -> }
        alertDialog.show()

    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
// 登入成功
                    val user = auth.currentUser
// 可以在這邊處理facebook登入後的流程邏輯
                } else {
// 處理登入失敗的狀況，使用toast顯示登入失敗的 提示
                    Toast.makeText(this@MainActivity, "登入失敗", Toast.LENGTH_SHORT).show()
                }
            }
    }




}