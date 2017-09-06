package com.bulingzhuang.palette4designer.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bulingzhuang.palette4designer.R
import com.bulingzhuang.palette4designer.bean.ContentModel
import com.bulingzhuang.palette4designer.utils.Tools
import com.bumptech.glide.Glide
import org.jetbrains.annotations.NotNull
import java.io.InputStream
import java.util.*

/**
 * Created by bulingzhuang
 * on 2017/9/5
 * E-mail:bulingzhuang@foxmail.com
 */
class ContentAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        val NullStr = "（Null）"
    }

    private val mDataList: MutableList<ContentModel>

    init {
        mDataList = ArrayList()
    }

    /**
     * 刷新列表
     */
    fun refreshData(@NotNull dataList: List<ContentModel>) {
        if (dataList.isNotEmpty()) {
            mDataList.clear()
            mDataList.addAll(dataList)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val itemData = mDataList[position]
        val viewHolder = holder as ContentAdapterViewHolder
        val layoutParams = viewHolder.mIvContent.layoutParams
        val heightPx = Tools.dp2px(context, itemData.heightDP)
        layoutParams.height = heightPx.toInt()
        viewHolder.mIvContent.layoutParams = layoutParams
        Glide.with(context).load(itemData.uri).placeholder(R.mipmap.ic_launcher_foreground).into(viewHolder.mIvContent)

        setColor(itemData.colorDominant, viewHolder.mBtnDominant)
        setColor(itemData.colorVibrant, viewHolder.mBtnVibrant)
        setColor(itemData.colorDarkVibrant, viewHolder.mBtnDarkVibrant)
        setColor(itemData.colorLightVibrant, viewHolder.mBtnLightVibrant)
        setColor(itemData.colorMuted, viewHolder.mBtnMuted)
        setColor(itemData.colorDarkMuted, viewHolder.mBtnDarkMuted)
        setColor(itemData.colorLightMuted, viewHolder.mBtnLightMuted)
    }

    /**
     * 测量图片
     */
    private fun measureImg(data: ContentModel, view: ImageView) {
//        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, data.uri)
//        val inputStream = context.contentResolver.openInputStream(data.uri)
//
//        //处理图片宽高
//        val heightDp = 250f * bitmap.height / bitmap.width
//        val layoutParams = view.layoutParams
//        val heightPx = Tools.dp2px(context, heightDp)
//        data.heightDP = heightDp
//        data.widthDP = 250f
//        layoutParams.height = heightPx.toInt()
//        view.layoutParams = layoutParams
//        Glide.with(context).load(data.uri).placeholder(R.mipmap.ic_launcher_foreground).into(view)
//
//        //计算色值
//        val builder = Palette.from(bitmap)
//        val palette = builder.generate()
//        data.colorDominant = palette.dominantSwatch
//        data.colorVibrant = palette.vibrantSwatch
//        data.colorDarkVibrant = palette.darkVibrantSwatch
//        data.colorLightVibrant = palette.lightVibrantSwatch
//        data.colorMuted = palette.mutedSwatch
//        data.colorDarkMuted = palette.darkMutedSwatch
//        data.colorLightMuted = palette.lightMutedSwatch
    }

    private fun setColor(swatch: Palette.Swatch?, view: TextView) {
        val oText = view.text
        if (swatch != null) {
            view.background.setColorFilter(swatch.rgb, PorterDuff.Mode.SRC_IN)
            view.setTextColor(swatch.titleTextColor)
            if (oText.contains(NullStr)) {
                view.text = oText.substring(0, oText.indexOf(NullStr))
            }
        } else {
            view.background.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN)
            view.setTextColor(Color.WHITE)
            if (!oText.contains(NullStr)) {
                view.text = String.format(Locale.CHINA, "$oText%s", NullStr)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(context).inflate(R.layout.adapter_content, parent, false)
        return ContentAdapterViewHolder(inflate)
    }

    private inner class ContentAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mIvContent: ImageView = itemView.findViewById(R.id.iv_content)
        val mBtnDominant: TextView = itemView.findViewById(R.id.btn_Dominant)
        val mBtnVibrant: TextView = itemView.findViewById(R.id.btn_Vibrant)
        val mBtnDarkVibrant: TextView = itemView.findViewById(R.id.btn_DarkVibrant)
        val mBtnLightVibrant: TextView = itemView.findViewById(R.id.btn_LightVibrant)
        val mBtnMuted: TextView = itemView.findViewById(R.id.btn_Muted)
        val mBtnDarkMuted: TextView = itemView.findViewById(R.id.btn_DarkMuted)
        val mBtnLightMuted: TextView = itemView.findViewById(R.id.btn_LightMuted)
    }
}