package com.thinkingdobby.databaseproject.viewHolder

import android.graphics.Bitmap
import android.media.ExifInterface
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.thinkingdobby.databaseproject.R
import com.thinkingdobby.databaseproject.data.MyPetPost
import com.thinkingdobby.databaseproject.functions.rotateImage
import kotlinx.android.synthetic.main.my_pet_card.view.*

class MyPetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val myPet_iv_background = itemView.myPet_iv_background

    val myPet_tv_name = itemView.myPet_tv_name
    val myPet_tv_breed = itemView.myPet_tv_breed
    val myPet_tv_lengthValue = itemView.myPet_tv_lengthValue
    val myPet_iv_sex = itemView.myPet_iv_sex

    fun bind(myPet: MyPetPost, bitmap: Bitmap) {
        myPet_tv_name.text = myPet.petName
        myPet_tv_breed.text = myPet.petBreed
        myPet_tv_lengthValue.text = myPet.petLength.toString()

        if (myPet.petSex == "남") {
            myPet_iv_sex.setImageResource(R.drawable.icon_gender_boy)
        } else if (myPet.petSex == "여") {
            myPet_iv_sex.setImageResource(R.drawable.icon_gender_girl)
        }

        val rotatedBitmap = when (myPet.imgOt) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }

        myPet_iv_background.setImageBitmap(rotatedBitmap)
    }
}