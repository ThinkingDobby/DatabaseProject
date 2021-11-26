package com.thinkingdobby.databaseproject

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import com.thinkingdobby.databaseproject.data.User
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccountActivity : AppCompatActivity() {

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        window.apply {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = Color.TRANSPARENT
            }
        }

        val pref = getSharedPreferences("basic", MODE_PRIVATE)
        val editor = pref.edit()

        createAccount_btn_createAccount.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().getReference("account").push()
            val nickname = createAccount_et_nickname.text.toString()
            val contact = createAccount_et_contact.text.toString()
            val info = createAccount_et_info.text.toString()

            val refKey = ref.key!!.toString()
            editor.putString("writerId", refKey)
            editor.apply()

            val user = User(refKey, nickname, contact, info)
            ref.setValue(user)

            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
    }
}
