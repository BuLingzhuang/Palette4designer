package com.bulingzhuang.palette4designer.bean

import android.net.Uri
import android.support.v7.graphics.Palette

/**
 * Created by bulingzhuang
 * on 2017/9/5
 * E-mail:bulingzhuang@foxmail.com
 */
class ContentModel constructor(val uri: Uri, val heightDP: Float, val widthDP: Float, val colorDominant: Palette.Swatch?,
                               val colorVibrant: Palette.Swatch?,
                               val colorDarkVibrant: Palette.Swatch?,
                               val colorLightVibrant: Palette.Swatch?,
                               val colorMuted: Palette.Swatch?,
                               val colorDarkMuted: Palette.Swatch?,
                               val colorLightMuted: Palette.Swatch?)