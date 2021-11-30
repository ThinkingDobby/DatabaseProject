package com.thinkingdobby.databaseproject

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.thinkingdobby.databaseproject.data.MyPetDB
import com.thinkingdobby.databaseproject.data.MyPetPost
import com.thinkingdobby.databaseproject.functions.createCopyAndReturnRealPath
import com.thinkingdobby.databaseproject.functions.rotateImage
import kotlinx.android.synthetic.main.activity_post_my_pet.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class PostMyPetActivity : AppCompatActivity() {
    private var sex = "남"
    private var pickImageFromAlbum = 0
    private var uriPhoto: Uri? = Uri.parse("android.resource://com.thinkingdobby.databaseproject/drawable/my_background_sample")

    private var myPetDB: MyPetDB? = null
    private lateinit var tempImage: ByteArray
    private var tempOt = 0
    private var imageChanged = false
    private var postId = 0L

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

        val edit = intent.getStringExtra("edit") ?: "no"
        if (edit == "yes") {
            val bundle = intent.extras
            val pet = bundle!!.getParcelable<MyPetPost>("selectedMyPet")!!

            postMyPet_et_name.setText(pet.petName)
            postMyPet_et_info.setText(pet.petInfo)
            postMyPet_et_breed.setText(pet.petBreed)
            postMyPet_et_sex.text = pet.petSex
            postMyPet_et_length.setText(pet.petLength)

            postId = pet.postId!!
            tempImage = pet.petImage!!
            tempOt = pet.imgOt!!

            postMyPet_tv_title.text = "동물정보 수정"
            postMyPet_tv_post.text = "동물정보 수정"

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
                .transform(CenterCrop())
                .into(postMyPet_iv_pet)

        } else {
            postMyPet_iv_pet.setImageResource(R.drawable.my_background_sample)
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
            if (postMyPet_et_name.length() == 0) {
                Toast.makeText(this, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            } else {
                GlobalScope.launch {
                    myPetDB = MyPetDB.getInstance(this@PostMyPetActivity)

                    val newMyPet = MyPetPost()
                    newMyPet.postId = if (edit == "no") newMyPet.hashCode().toLong() else postId
                    newMyPet.writeTime = System.currentTimeMillis().toString()

                    newMyPet.petName = postMyPet_et_name.text.toString()
                    newMyPet.petBreed = postMyPet_et_breed.text.toString()
                    newMyPet.petSex = postMyPet_et_sex.text.toString()
                    newMyPet.petLength = postMyPet_et_length.text.toString()

                    newMyPet.petInfo = postMyPet_et_info.text.toString()

                    if (edit == "yes" && !imageChanged) newMyPet.petImage = tempImage
                    else newMyPet.petImage = getByteArrayFromDrawable(uriPhoto!!)

                    val ei =
                        ExifInterface(createCopyAndReturnRealPath(applicationContext, uriPhoto!!)!!)
                    val orientation = ei.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED
                    )

                    if (edit == "yes" && !imageChanged) newMyPet.imgOt = tempOt
                    else newMyPet.imgOt = orientation

                    if (edit == "no") myPetDB?.myPetDao()?.insert(newMyPet)
                    else myPetDB?.myPetDao()?.updateByMyPetPostNo(
                        postId,
                        newMyPet.writeTime!!,
                        newMyPet.petName!!,
                        newMyPet.petBreed!!,
                        newMyPet.petSex!!,
                        newMyPet.petLength!!,
                        newMyPet.petInfo!!,
                        newMyPet.petImage!!,
                        newMyPet.imgOt!!
                    )
                }
                Toast.makeText(this@PostMyPetActivity, "업로드되었습니다.", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
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
                postMyPet_iv_pet.setImageURI(uriPhoto)
                imageChanged = true
            }
        }
    }

    private fun getByteArrayFromDrawable(uri: Uri): ByteArray {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

        val width = bitmap.width
        val height = bitmap.height

        val scaledBitmap = if (width < height) {
            if (width > 729) {
                Bitmap.createScaledBitmap(
                    bitmap,
                    729,    // Xdp -> 3*X
                    (height.toDouble() / (width.toDouble() / 729)).toInt(),
                    true
                )
            } else bitmap
        } else {
            if (height > 972) {
                Bitmap.createScaledBitmap(
                    bitmap,
                    (width.toDouble() / (height.toDouble() / 972)).toInt(),
                    972,
                    true
                )
            } else bitmap
        }

        val stream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)

        return stream.toByteArray()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
    }
}
