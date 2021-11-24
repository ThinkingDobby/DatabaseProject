package com.thinkingdobby.databaseproject

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.ExifInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.thinkingdobby.databaseproject.data.MyPetDB
import com.thinkingdobby.databaseproject.data.MyPetPost
import com.thinkingdobby.databaseproject.functions.rotateImage
import kotlinx.android.synthetic.main.activity_my_pet_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyPetDetailActivity : AppCompatActivity() {
    private var myPetDB : MyPetDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pet_detail)

        overridePendingTransition(R.anim.fadein, R.anim.fadeout)

        window.apply {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = Color.TRANSPARENT
            }
        }

        val bundle = intent.extras
        val pet = bundle!!.getParcelable<MyPetPost>("selectedMyPet")!!

        myPetDB = MyPetDB.getInstance(this)

        myPetDetail_btn_back.setOnClickListener {
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            finish()
        }

        myPetDetail_btn_edit.setOnClickListener {
            val intent = Intent(this, PostMyPetActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("selectedMyPet", pet)
            intent.putExtras(bundle)
            intent.putExtra("edit", "yes")
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }

        myPetDetail_btn_delete.setOnClickListener {
            val builder = AlertDialog.Builder(this@MyPetDetailActivity)
            builder.setTitle("글을 삭제할까요?")

            builder.setPositiveButton("아니오") { _, which ->
            }

            builder.setNegativeButton("예") {_, which ->
                GlobalScope.launch {
                    myPetDB?.myPetDao()?.deleteAll()
                    finish()
                }
            }
            builder.create().show()
        }

        val options = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeByteArray(pet.petImage, 0, pet.petImage!!.size, options)

        val rotatedBitmap = when (pet.imgOt) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }

        Glide.with(this)
            .load(rotatedBitmap)
            .into(myPetDetail_iv_pet)

        myPetDetail_et_name.text = pet.petName
        myPetDetail_et_info.text = pet.petInfo
        myPetDetail_et_breed.text = pet.petBreed
        myPetDetail_et_sex.text = pet.petSex
        myPetDetail_et_length.text = pet.petLength
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
    }
}
