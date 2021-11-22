package com.thinkingdobby.databaseproject

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        window.apply {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = Color.TRANSPARENT
            }
        }

        val intent = Intent(this, FindPetActivity::class.java)

        home_btn_toFindPet.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)

            intent.putExtra("mode", "FindPet")
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }

        home_btn_toFindPerson.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)

            intent.putExtra("mode", "FindPerson")
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }
    }
}
