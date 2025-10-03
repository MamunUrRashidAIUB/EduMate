package com.rashid.edumate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val signupTextView =findViewById<TextView>(R.id.signUpText)
        signupTextView.setOnClickListener {
            val intent =Intent(this,SignupActivity::class.java)
            startActivity(intent)
        }

        val homeScreenShow=findViewById<Button>(R.id.loginButton)
        homeScreenShow.setOnClickListener {
            val intent=Intent(this,home::class.java)
            startActivity(intent)
        }


    }

}
