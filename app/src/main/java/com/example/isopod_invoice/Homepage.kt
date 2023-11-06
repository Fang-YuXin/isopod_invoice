package com.example.isopod_invoice

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.io.IOException


class Homepage : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

//頁面轉換Start
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.selectedItemId = R.id.Homepage

        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Mine -> {
                    intent?.extras?.let {
                    val uid = it.getString("key")
                    val bundle = Bundle()
                    println(uid)
                    bundle.putString("key", uid)
                    var intentmine = Intent(this,Mine::class.java).apply {
                        putExtras(bundle)
                    }
                    println(bundle)
                    println(intentmine)
                    startActivity(intentmine)
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }}
                R.id.Homepage -> return@OnNavigationItemSelectedListener true
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

        val yearmonth = findViewById<EditText>(R.id.YearMonth)
        val number = findViewById<EditText>(R.id.InvoiceNumber)
        val Check = findViewById<Button>(R.id.CheckButton)
        val baby = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.baby)
        val pandas1 = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.pandas1)
        val water2 = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.water2)
        val lemon3 = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.lemon3)

//確定user的鼠婦為哪一階級
        //可以直接變成用Sql查詢，不要使用bundle傳送
        intent?.extras?.let {
        val uid = it.getString("key")

        val clientid = OkHttpClient()
        val urlBuilderid = "https://fangbibi.lionfree.net/app/user_number_select.php".toHttpUrlOrNull()
            ?.newBuilder()
            ?.addQueryParameter("uid", "${uid}")



        val urlid = urlBuilderid?.build().toString()
        val requestid = Request.Builder()
            .url(urlid)
            .build()

        clientid.newCall(requestid).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException)
            {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response)
            {

                if (response.isSuccessful)
                {
                    val responseBodyid = response.body?.string()
                    //println("id-----${responseBody}")

                    val jsonObjectid = JSONObject(responseBodyid)
                    val dataArrayid = jsonObjectid.getJSONArray("data")


                    runOnUiThread {
                        for (i in 0 until dataArrayid.length())
                        {
                            val chatObjectid = dataArrayid.getJSONObject(i)
                            val sopodnumberid = chatObjectid.getInt("number")
                            //println(sopodnumber)

                            if(sopodnumberid > 9)
                            {
                                baby.visibility = View.INVISIBLE
                                pandas1.visibility = View.INVISIBLE
                                water2.visibility = View.INVISIBLE
                                lemon3.visibility = View.VISIBLE

                            }
                            else if(sopodnumberid > 6)
                            {
                                baby.visibility = View.INVISIBLE
                                pandas1.visibility = View.INVISIBLE
                                water2.visibility = View.VISIBLE
                                lemon3.visibility = View.INVISIBLE
                            }
                            else if(sopodnumberid > 3)
                            {
                                baby.visibility = View.INVISIBLE
                                pandas1.visibility = View.VISIBLE
                                water2.visibility = View.INVISIBLE
                                lemon3.visibility = View.INVISIBLE
                            }
                            else
                            {
                                baby.visibility = View.VISIBLE
                                pandas1.visibility = View.INVISIBLE
                                water2.visibility = View.INVISIBLE
                                lemon3.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            }
        })}


//確定user鼠婦End

//Check Button Start
        Check.setOnClickListener {


            if((yearmonth.text.isNotEmpty()) and (number.text.isNotEmpty()))
            {
                val vid = number.text.toString()
                println("cid-------${vid}")
                val postyearmonth = yearmonth.text.toString()

                val parts = postyearmonth.split("-");//分割yearmonth
                val year = parts[0]
                val month = parts[1]

                if(number.length() == 8)
                {
                //----------------
                    intent?.extras?.let {
                        val uid = it.getString("key")


                        val clientPost = OkHttpClient()
                        val requestBody = FormBody.Builder()
                            .add("uid", "${uid}")
                            .add("vid", "${vid}")
                            .add("vmon", "${month}")
                            .add("vyear", "${year}")
                            .build()
                        val requestPost = Request.Builder()
                            .url("https://fangbibi.lionfree.net/app/invoiceinsert.php")
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
                                runOnUiThread { }

                                //Snackbar
                                val snackbar = Snackbar.make(Check,"${responseBody}" , Snackbar.LENGTH_SHORT)
                                snackbar.show()


//鼠婦長大End

                            }
                        })
//更新鼠婦
                        val client = OkHttpClient()
                        val urlBuilder = "https://fangbibi.lionfree.net/app/user_number_select.php".toHttpUrlOrNull()
                            ?.newBuilder()
                            ?.addQueryParameter("uid", "${uid}")



                        val url = urlBuilder?.build().toString()
                        val request = Request.Builder()
                            .url(url)
                            .build()

                        client.newCall(request).enqueue(object : Callback
                        {
                            override fun onFailure(call: Call, e: IOException)
                            {
                                e.printStackTrace()
                            }

                            override fun onResponse(call: Call, response: Response)
                            {

                                if (response.isSuccessful)
                                {
                                    val responseBody = response.body?.string()
                                    //println("id-----${responseBody}")

                                    val jsonObject = JSONObject(responseBody)
                                    val dataArray = jsonObject.getJSONArray("data")


                                    runOnUiThread {
                                        for (i in 0 until dataArray.length())
                                        {
                                            val chatObject = dataArray.getJSONObject(i)
                                            val sopodnumber = chatObject.getInt("number")
                                            //println(sopodnumber)

                                            if(sopodnumber > 9)
                                            {
                                                baby.visibility = View.INVISIBLE
                                                pandas1.visibility = View.INVISIBLE
                                                water2.visibility = View.INVISIBLE
                                                lemon3.visibility = View.VISIBLE

                                            }
                                            else if(sopodnumber > 6)
                                            {
                                                baby.visibility = View.INVISIBLE
                                                pandas1.visibility = View.INVISIBLE
                                                water2.visibility = View.VISIBLE
                                                lemon3.visibility = View.INVISIBLE
                                            }
                                            else if(sopodnumber > 3)
                                            {
                                                baby.visibility = View.INVISIBLE
                                                pandas1.visibility = View.VISIBLE
                                                water2.visibility = View.INVISIBLE
                                                lemon3.visibility = View.INVISIBLE
                                            }
                                            else
                                            {
                                                baby.visibility = View.VISIBLE
                                                pandas1.visibility = View.INVISIBLE
                                                water2.visibility = View.INVISIBLE
                                                lemon3.visibility = View.INVISIBLE
                                            }
                                        }

                                    }

                                }
                            }


                        })
//更新End

                    }
            }
                else
                {
                    Toast.makeText(this,"請確認發票號碼是否為8碼", Toast.LENGTH_SHORT).show()


                }

            }
            else{
                Toast.makeText(this,"請輸入資料", Toast.LENGTH_SHORT).show()


            }

        }
        //Check Button End
    }
}