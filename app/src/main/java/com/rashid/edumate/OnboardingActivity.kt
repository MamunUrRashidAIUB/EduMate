package com.rashid.edumate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class OnboardingActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding)
        val getStartedButton :Button =findViewById(R.id.button)

        getStartedButton.setOnClickListener{

            val intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
