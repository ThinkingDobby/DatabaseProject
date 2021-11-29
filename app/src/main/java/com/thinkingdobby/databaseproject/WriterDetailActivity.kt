package com.thinkingdobby.databaseproject

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.thinkingdobby.databaseproject.data.User
import kotlinx.android.synthetic.main.activity_writer_detail.*

class WriterDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writer_detail)

        val writerId = intent.getStringExtra("writerId")

        Firebase.database.getReference("account").child(writerId!!).get().addOnSuccessListener {
            val user = it.getValue(User::class.java)
            writerDetail_et_nickname.text = user?.nickname
            writerDetail_et_contact.text = user?.contact
            writerDetail_et_info.text = user?.info
        }

        window.apply {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = Color.TRANSPARENT
            }
        }

        val fromHome = intent.getBooleanExtra("fromHome", false)

        if (fromHome) {
            writerDetail_tv_title.text = "사용자 정보"
            writerDetail_btn_edit.visibility = View.VISIBLE
        }

        writerDetail_btn_edit.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            intent.putExtra("edit", true)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }

        writerDetail_btn_back.setOnClickListener {
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
    }
}
