package com.dobler.camera2talk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_choose.*

class ChooseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_choose)

        btCamera2.setOnClickListener {
            val intent = Intent(this, CameraWithTextureActivity::class.java)
            startActivity(intent)
        }

        btCamerax.setOnClickListener {
            val intent = Intent(this, CameraAndroidXActivity::class.java)
            startActivity(intent)
        }
    }
}
