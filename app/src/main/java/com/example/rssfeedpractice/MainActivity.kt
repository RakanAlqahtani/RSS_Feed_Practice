package com.example.rssfeedpractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL

class MainActivity : AppCompatActivity() {
    lateinit var adapter : RVAdapter
    private  var question = ArrayList<StackQuestion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvMain.layoutManager = LinearLayoutManager(this)

        parserRSS()


    }

    private fun parserRSS() {
        CoroutineScope(IO).launch {
            val data = async {
                parser()
            }.await()
            try {
                withContext(Main){

                    rvMain.adapter = RVAdapter(question)
                    rvMain.adapter!!.notifyDataSetChanged()


                }
            }catch (e : java.lang.Exception){
                Log.d("Main","unable to get data")
            }
        }
    }

    fun parser(): ArrayList<StackQuestion>{
        var qtitle = ""
        var text = ""
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            val url = URL("https://stackoverflow.com/feeds")
            parser.setInput(url.openStream(),null)
            var eventype = parser.eventType
            while (eventype != XmlPullParser.END_DOCUMENT){
                val tagName = parser.name
                when(eventype){
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> when (tagName){
                        "title" ->{
                            qtitle = text.toString()
                            val data = StackQuestion(qtitle)
                                question.add(data)
                        }

                    }
                    else -> {}
                }
                eventype = parser.next()
            }

        }catch (e : XmlPullParserException){
            e.printStackTrace()
        }

        return question
    }

}