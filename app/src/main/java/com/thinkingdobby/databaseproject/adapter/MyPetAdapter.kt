package com.thinkingdobby.databaseproject.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thinkingdobby.databaseproject.MyPetDetailActivity
import com.thinkingdobby.databaseproject.R
import com.thinkingdobby.databaseproject.data.MyPetPost
import com.thinkingdobby.databaseproject.viewHolder.MyPetViewHolder
import kotlinx.android.synthetic.main.my_pet_card.view.*

class MyPetAdapter(val context: Context, private val dataList: List<MyPetPost>, private val bitmapList: List<Bitmap>)
    : RecyclerView.Adapter<MyPetViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPetViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.my_pet_card, parent, false)
        return MyPetViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyPetViewHolder, position: Int) {
        holder.bind(dataList[position], bitmapList[position])
        holder.itemView.myPet_btn_info.setOnClickListener {
            val intent = Intent(context, MyPetDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("selectedMyPet", dataList[position])
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
}