package com.thinkingdobby.databaseproject

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.thinkingdobby.databaseproject.adapter.PetAdapter
import com.thinkingdobby.databaseproject.data.PetPost
import kotlinx.android.synthetic.main.activity_find_pet.*

class FindPetActivity : AppCompatActivity() {

    private val findPetList = mutableListOf<PetPost>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_pet)

        window.apply {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = Color.TRANSPARENT
            }
        }

        findPet_btn_add.setOnClickListener {
            val intent = Intent(this, PostPetActivity::class.java)
            startActivity(intent)
        }

        // 하단 메뉴
        findPet_btn_toFindPerson.setOnClickListener {
            val intent = Intent(this, FindPersonActivity::class.java)
            startActivity(intent)
            finish()
        }

        findPet_btn_toHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }



        val layoutManager = LinearLayoutManager(this@FindPetActivity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        findPet_rv_list.layoutManager = layoutManager
        findPet_rv_list.adapter = PetAdapter(this@FindPetActivity, findPetList)
    }
}
