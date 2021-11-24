package com.thinkingdobby.databaseproject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.thinkingdobby.databaseproject.adapter.MyPetAdapter
import com.thinkingdobby.databaseproject.data.MyPetDB
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private var myPetDB: MyPetDB? = null

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

        home_btn_toFindPet.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)

            val intent = Intent(this, FindPetActivity::class.java)
            intent.putExtra("mode", "FindPet")
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }

        home_btn_toFindPerson.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)

            val intent = Intent(this, FindPetActivity::class.java)
            intent.putExtra("mode", "FindPerson")
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }

        home_btn_add.setOnClickListener {
            val intent = Intent(this, PostMyPetActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }

        var myPetAdapter: MyPetAdapter?
        home_rv_list.layoutManager = LinearLayoutManager(this@HomeActivity)
        home_rv_list.setHasFixedSize(true)

        myPetDB = MyPetDB.getInstance(this)

        myPetDB?.myPetDao()?.getAll()!!.observe(this, Observer {
            val bitmapList = mutableListOf<Bitmap>()
            for (i in it!!) {
                val options = BitmapFactory.Options()
//                 options.inSampleSize = 16
                val bitmap = BitmapFactory.decodeByteArray(i.petImage, 0, i.petImage!!.size, options)
                bitmapList.add(bitmap)
            }
            myPetAdapter = MyPetAdapter(this@HomeActivity, it, bitmapList)
            home_rv_list.adapter = myPetAdapter
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
    }
}
