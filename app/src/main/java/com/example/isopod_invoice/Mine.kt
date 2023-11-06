package com.example.isopod_invoice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.io.IOException

class Mine : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mine)

//頁面轉換Start
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.selectedItemId = R.id.Homepage

        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Mine -> return@OnNavigationItemSelectedListener true

                R.id.Homepage -> {

                    intent?.extras?.let {
                        val uid = it.getString("key")
                        val bundle = Bundle()
                        println(uid)
                        bundle.putString("key", uid)
                        var intenthomepage = Intent(this,Homepage::class.java).apply {
                            putExtras(bundle)
                        }
                        println(bundle)
                        println(intenthomepage)
                        startActivity(intenthomepage)

                        overridePendingTransition(0, 0)
                        return@OnNavigationItemSelectedListener true
                    }}
                R.id.Info -> {

                    intent?.extras?.let {
                        val uid = it.getString("key")
                        val bundle = Bundle()
                        println(uid)
                        bundle.putString("key", uid)
                        var intentinfo = Intent(this,Info::class.java).apply {
                            putExtras(bundle)
                        }
                        println(bundle)
                        println(intentinfo)
                        startActivity(intentinfo)

                        overridePendingTransition(0, 0)
                        return@OnNavigationItemSelectedListener true
                    }}
            }
            false
        })
//頁面轉換End

        //Show 自己的所有發票Start
        val Show = findViewById<Button>(R.id.Show)

        Show.setOnClickListener {
            intent?.extras?.let {
                val uid = it.getString("key")


//            if (yearmonth.text.isNotEmpty()){
//                val postyearmonth = yearmonth.text.toString()
//
//                val parts = postyearmonth.split("-");//分割yearmonth
//                val year = parts[0]
//                val month = parts[1]


                //----------------
                val client = OkHttpClient()


                val urlBuilder = "https://fangbibi.lionfree.net/app/user_invoice_check.php".toHttpUrlOrNull()
                    ?.newBuilder()
                    ?.addQueryParameter("uid", "${uid}")



                val url = urlBuilder?.build().toString()

                val request = Request.Builder()
                    .url(url)
                    .build()
                println(request.url.toString())
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {

                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            //println("-----${responseBody}")

                            val jsonObject = JSONObject(responseBody)
                            val dataArray = jsonObject.getJSONArray("data")


                            runOnUiThread {
                                //直接將內容回傳給id名稱為TextView
                                val textView = findViewById<TextView>(R.id.Information)
                                textView.setMovementMethod(ScrollingMovementMethod.getInstance());
                                //.text = "${year} - ${month}期 \n 開獎號碼   獎項"
                                if (dataArray.length() == 0) {
                                    textView.text = "查無資料"
                                } else {

                                   textView.text = " 發票號碼       年       月        狀態          金額\n"
                                    for (i in 0 until dataArray.length()) {
                                        val chatObject = dataArray.getJSONObject(i)
                                        val vid = chatObject.getString("vid")
                                        val vmon = chatObject.getString("vmon")

                                        val vyear = chatObject.getInt("vyear")
                                        val statue = chatObject.getString("statue")
                                        val price = chatObject.getString("price")
//                                        textView.append("${vid}  -  ${vyear}  -  ${vmon}  -  ${statue}  -  ${price}\n")

                                        val formattedText = String.format(
                                            "%-11s %-8d %-6s %-10s %8s\n",
                                            vid, vyear, vmon, statue, price
                                        )
                                        textView.append(formattedText)


                                        // 顯示愛心 ImageView
//                                        Heart.visibility = View.VISIBLE
//
//                                        // 定義淡入淡出動畫
//                                        val animation = AlphaAnimation(1.0f, 0.0f)
//                                        animation.duration = 2000 // 2 秒
//                                        Heart.startAnimation(animation)
//
//                                        // 使用 Handler 在 2 秒後將愛心 ImageView 設置為不可見
//                                        Handler().postDelayed({
//                                            Heart.visibility = View.INVISIBLE
//                                        }, 2000)
                                    }
                                }
                            }
                        } else {
                            println("Request failed")
                            runOnUiThread {
                                //直接將內容回傳給id名稱為TextView
                                findViewById<TextView>(R.id.Information).text = "資料錯誤"
                            }

                        }
                    }
                })}
//            }
//            else{
//                Toast.makeText(this,"請輸入資料", Toast.LENGTH_SHORT).show()
////                Error.visibility = View.VISIBLE
////
////                // 定義淡入淡出動畫
////                val animation = AlphaAnimation(1.0f, 0.0f)
////                animation.duration = 2000 // 2 秒
////                Error.startAnimation(animation)
////
////                // 使用 Handler 在 2 秒後將愛心 ImageView 設置為不可見
////                Handler().postDelayed({
////                    Error.visibility = View.INVISIBLE
////                }, 2000)
//
//            }






        }



        //Show 自己的所有發票End


    }
}