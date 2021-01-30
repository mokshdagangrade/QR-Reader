package com.example.qrreader

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.lang.IllegalArgumentException

class CodeActivity : AppCompatActivity() {

    var input:EditText? = null
    var generate: Button? = null
    var output:ImageView? = null
    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code)

        context = this

        input = findViewById(R.id.input)
        generate = findViewById(R.id.btnGenerate)
        output = findViewById(R.id.output)

        generate!!.setOnClickListener {
            val inputText = input!!.text.toString().trim()
            val bitmap:Bitmap? = encodeAsBitmap(inputText,300,300,context)
            output!!.setImageBitmap(bitmap)

        }
    }

    fun encodeAsBitmap(str: String, WIDTH: Int, HEIGHT: Int, ctx: Context): Bitmap? {
        val result: BitMatrix
        try {
            result = MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null)
        }
        catch (iae: IllegalArgumentException){
            return null
        }
        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        for (y in 0 until height){
            val offset = y*width
            for(x in 0 until width){
                pixels[offset + x] = if(result.get(x, y)) -0x1000000 else -0x1
            }
        }
        val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels,0,width,0,0,width,height)
        return bitmap

    }


}