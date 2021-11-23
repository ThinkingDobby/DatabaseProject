package com.thinkingdobby.databaseproject

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.firebase.database.ServerValue
import com.thinkingdobby.databaseproject.data.MyPetDB
import com.thinkingdobby.databaseproject.data.MyPetPost
import com.thinkingdobby.databaseproject.functions.createCopyAndReturnRealPath
import kotlinx.android.synthetic.main.activity_post_my_pet.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class PostMyPetActivity : AppCompatActivity() {
    private var sex = "남"
    private var pickImageFromAlbum = 0
    private var uriPhoto: Uri? = Uri.parse("android.resource://com.thinkingdobby.databaseproject/drawable/card_background_sample")
    private var ei = ExifInterface(createCopyAndReturnRealPath(applicationContext, uriPhoto!!)!!)
    private var orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED)

    private var myPetDB: MyPetDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_my_pet)

        // request permission
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        // request permission

        window.apply {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = Color.TRANSPARENT
            }
        }

        postMyPet_btn_back.setOnClickListener {
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            finish()
        }

        postMyPet_btn_gallery.setOnClickListener { loadImage() }

        postMyPet_btn_selectSex.setOnClickListener {
            val builder = AlertDialog.Builder(this@PostMyPetActivity)
            builder.setTitle("성별을 고르세요.")
            val sexes = arrayOf("남", "여")
            builder.setSingleChoiceItems(sexes, -1) { _, which: Int ->
                sex = sexes[which]
            }

            builder.setPositiveButton("확인") { _, which ->
                postMyPet_et_sex.text = sex
            }

            builder.setNegativeButton("취소") {_, which -> }

            builder.create().show()
        }

        postMyPet_btn_post.setOnClickListener {
            GlobalScope.launch {
                myPetDB = MyPetDB.getInstance(this@PostMyPetActivity)

                val newMyPet = MyPetPost()
                newMyPet.writeTime = ServerValue.TIMESTAMP.toString()

                newMyPet.petBreed = postMyPet_et_breed.text.toString()
                newMyPet.petName = postMyPet_et_name.text.toString()
                newMyPet.petSex = postMyPet_et_sex.text.toString()
                newMyPet.petLength = postMyPet_et_length.text.toString()

                newMyPet.petInfo = postMyPet_et_info.text.toString()

                newMyPet.petImage = getByteArrayFromDrawable(uriPhoto!!)
                newMyPet.imgOt = orientation

                myPetDB?.myPetDao()?.insert(newMyPet)

                finish()
            }
        }
    }

    private fun loadImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Load Picture"), pickImageFromAlbum)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageFromAlbum) {
            if (resultCode == Activity.RESULT_OK) {
                uriPhoto = data?.data
                ei = ExifInterface(createCopyAndReturnRealPath(applicationContext, uriPhoto!!)!!)
                orientation = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED)
                postMyPet_iv_pet.setImageURI(uriPhoto)
            }
        }
    }

    private fun getByteArrayFromDrawable(uri: Uri): ByteArray {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)

        return stream.toByteArray()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
    }
}
