package com.example.nguyenxuansang98.asynctask

import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

import java.io.InputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    companion object{
        private var downloadedImg : ImageView ? =null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        downloadedImg = findViewById(R.id.img_demo)
        btn_loadImage.setOnClickListener {
            LoadImageUrl(this).execute("https://scontent.fdad3-1.fna.fbcdn.net/v/t1.0-9/s960x960/89788357_1122496908142897_7848441589658550272_o.jpg?_nc_cat=103&_nc_sid=0be424&_nc_ohc=IuFiNlLmhC8AX8bfFx1&_nc_ht=scontent.fdad3-1.fna&_nc_tp=7&oh=afccfb3bf6e96833ad5fda7b009f6ebc&oe=5EA489B0")
        }
    }
    class LoadImageUrl(val activity: Activity) : AsyncTask<String,Int,Bitmap>(){

        var progessDiglog :ProgressDialog? = null
        override fun doInBackground(vararg params: String?): Bitmap {
            for(i in 1..100){
                Thread.sleep(50);
                publishProgress(i)
                println(""+i)
                if (i==100){
                    progessDiglog!!.dismiss()
                }
            }
            return dowloadBitMap(params[0]!!)
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progessDiglog = ProgressDialog(activity)
            progessDiglog!!.setTitle("Download in Image.....")
            progessDiglog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            progessDiglog!!.max = 100
            progessDiglog!!.progress = 0
            progessDiglog!!.show()
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            progessDiglog!!.progress = values.get(0)!!
        }
        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            downloadedImg!!.setImageBitmap(result)
            progessDiglog?.dismiss()
        }

        private fun dowloadBitMap(url:String):Bitmap{
            val defaultHttpClient = DefaultHttpClient()
            val getRequest = HttpGet(url)
            try {
                val reponse = defaultHttpClient.execute(getRequest)
                val statusCode = reponse.statusLine.statusCode
                if(statusCode != HttpStatus.SC_OK){
                    return null!!
                }
                val entity = reponse.entity
                if(entity != null){
                    var inputStream : InputStream? = null
                    try {
                        inputStream = entity.content
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        return  bitmap
                    }finally {
                        if(inputStream !=null){
                            inputStream.close()
                        }
                        entity.consumeContent()
                    }
                }
            }catch (e : Exception){
                getRequest.abort()
                Log.e("LOI : ",e.toString())
            }
            return null!!
        }
    }
}
