package com.example.qrreader

import android.Manifest
import android.content.Intent
import android.graphics.drawable.Animatable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import org.json.JSONException
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    var cardView1:CardView? = null
    var cardView2:CardView? = null
    var btnScan:Button? = null
    var btnEnterCode:Button? = null
    var btnEnter:Button? = null
    var edtCode:EditText? = null
    var tvText:TextView? = null
    var hide:Animation? = null
    var reveal:Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cardView1 = findViewById(R.id.cardView1)
        cardView2 = findViewById(R.id.cardView2)
        btnScan = findViewById(R.id.btnScan)
        btnEnterCode = findViewById((R.id.btnEnterCode))
        edtCode = findViewById(R.id.edtCode)
        tvText= findViewById(R.id.tvText)
        btnEnter = findViewById(R.id.btnEnter)

        hide = AnimationUtils.loadAnimation(this,android.R.anim.fade_out)
        reveal = AnimationUtils.loadAnimation(this,android.R.anim.fade_in)

        tvText!!.startAnimation(reveal)
        cardView2!!.startAnimation(reveal)
        cardView2!!.visibility = View.VISIBLE
        tvText!!.setText("Scan QR Code Here")

        btnEnter!!.setOnClickListener{
            if(edtCode!!.text.toString().isNullOrEmpty()){
                Toast.makeText(this,"Please Enter Code",Toast.LENGTH_SHORT).show()

            }else{

                var value = edtCode!!.text.toString()
                Toast.makeText(this,value,Toast.LENGTH_SHORT).show()

            }
        }

        btnScan!!.setOnClickListener{
            tvText!!.startAnimation(reveal)
            cardView2!!.startAnimation(reveal)
            cardView1!!.startAnimation(hide)

            cardView2!!.visibility = View.VISIBLE
            cardView1!!.visibility = View.GONE
            tvText!!.setText("Scan QR Code Here")

            cameraTask()

        }
        btnEnterCode!!.setOnClickListener{
            tvText!!.startAnimation(reveal)
            cardView1!!.startAnimation(reveal)
            cardView2!!.startAnimation(hide)

            cardView1!!.visibility = View.VISIBLE
            cardView2!!.visibility = View.GONE
            tvText!!.setText("Enter QR Code Here")
        }

        cardView2!!.setOnClickListener{
            cameraTask()
        }
    }

    private fun hasCameraAccess() : Boolean
    {
        return EasyPermissions.hasPermissions(this,Manifest.permission.CAMERA)
    }

    private fun cameraTask(){
        if(hasCameraAccess()){
            var qrScanner = IntentIntegrator(this)
            qrScanner.setPrompt("Scan a QR Code")
            qrScanner.setCameraId(0)
            qrScanner.setOrientationLocked(true)
            qrScanner.setBeepEnabled(true)
            qrScanner.captureActivity = CaptureActivity::class.java
            qrScanner.initiateScan()

        }else{
            EasyPermissions.requestPermissions(
                this,
                "This app needs to access your camera to scan the QR code.",
                123,
                Manifest.permission.CAMERA
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        if(result != null){
            if(result.contents == null){
                Toast.makeText(this,"Result Not Found",Toast.LENGTH_SHORT).show()
                edtCode!!.setText("Nothing found here :(")
            }else{
                try {
                    cardView1!!.startAnimation(reveal)
                    cardView2!!.startAnimation(hide)
                    cardView1!!.visibility = View.VISIBLE
                    cardView2!!.visibility = View.GONE
                    edtCode!!.setText(result.contents.toString())
                }catch (exception:JSONException){
                    Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_SHORT).show()
                    edtCode!!.setText("Error")
                }

            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }

        if(requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){
            Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onRationaleDenied(requestCode: Int) {

    }

    override fun onRationaleAccepted(requestCode: Int) {

    }
}