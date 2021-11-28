package com.thinkingdobby.databaseproject

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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
        val edit = intent.getBooleanExtra("edit", false)

        if (edit) {
            val nowId = pref.getString("writerId", "temp")!!
            createAccount_tv_subTitle.text = "계정정보 변경"
            createAccount_tv_createAccount.text = "계정정보 변경"

            Firebase.database.getReference("account").child(nowId).get().addOnSuccessListener {
                val user = it.getValue(User::class.java)
                createAccount_et_nickname.setText(user?.nickname)
                createAccount_et_contact.setText(user?.contact)
                createAccount_et_info.setText(user?.info)
            }
        }


        createAccount_btn_createAccount.setOnClickListener {
            if (createAccount_et_nickname.length() == 0 || createAccount_et_contact.length() == 0) {
                Toast.makeText(this, "닉네임과 연락처를 모두 입력해 주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val ref = FirebaseDatabase.getInstance().getReference("account").push()
                val nickname = createAccount_et_nickname.text.toString()
                val contact = createAccount_et_contact.text.toString()
                val info = createAccount_et_info.text.toString()

                if (!edit) {
                    val refKey = ref.key!!.toString()
                    editor.putString("writerId", refKey)
                    editor.apply()

                    val user = User(refKey, nickname, contact, info)
                    ref.setValue(user)
                } else {
                    val nowId = pref.getString("writerId", "temp")!!
                    val user = User(nowId, nickname, contact, info)
                    FirebaseDatabase.getInstance().getReference("account/$nowId").setValue(user)
                }

                editor.putBoolean("isFirst", false)
                editor.apply()

                if (!edit) {
                    val intent = Intent(this, MainActivity::class.java)
                    Toast.makeText(this@CreateAccountActivity, "계정이 생성되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                    startActivity(intent)
                } else {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this@CreateAccountActivity, "계정정보가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
    }
}
