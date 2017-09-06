package com.bulingzhuang.palette4designer.utils

import android.os.Bundle
import android.os.Handler
import android.os.Message
import org.jetbrains.annotations.NotNull
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

/**
 * Created by bulingzhuang
 * on 2017/9/4
 * E-mail:bulingzhuang@foxmail.com
 */
object HttpUtil {

    private val genUrl = "http://cn.bing.com"
    private val genUrlUS = "http://cn.bing.com/?FORM=HPCNEN&setmkt=en-us&setlang=en-us&intlF="
    private val genUrlUS_XML = "http://cn.bing.com/HPImageArchive.aspx?idx=0&n=1"

    fun getImgSource_xml(handler: Handler) {
        Thread(Runnable {
            val url = URL(genUrlUS_XML)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5678
            val inputStream = connection.inputStream
            val htmlSource = readInputStream(inputStream)
            val imgUrl = readHtmlSource_xml(htmlSource)
            val data = Bundle()
            data.putInt("key", 233)
            data.putString("imgUrl", imgUrl)
            val message = Message()
            message.data = data
            handler.sendMessage(message)
        }).start()
    }

    /**
     * 第一套解析方案
     */
    fun getImgSource(handler: Handler) {
        Thread(Runnable {
            val url = URL(genUrlUS)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5678
            val inputStream = connection.inputStream
            val htmlSource = readInputStream(inputStream)
            val imgUrl = readHtmlSource(htmlSource)
            val data = Bundle()
            data.putInt("key", 233)
            data.putString("imgUrl", imgUrl)
            val message = Message()
            message.data = data
            handler.sendMessage(message)
        }).start()
    }

    private fun readInputStream(inputStream: InputStream): String {
        val isr = InputStreamReader(inputStream, Charset.forName("UTF-8"))
        val br = BufferedReader(isr)
        var str = ""
        br.forEachLine { str += it }
        return str
    }

    private fun readHtmlSource(@NotNull source: String): String? {
        val g_imgIndex = source.indexOf("1920x1080")
        if (g_imgIndex > 0) {
            val beforeIndex = source.lastIndexOf("\"", g_imgIndex)
            if (beforeIndex > 0) {
                val afterIndex = source.indexOf("\"", beforeIndex + 1)
                return genUrl + source.substring(beforeIndex + 1, afterIndex)
            }
        }
        return null
    }

    private fun readHtmlSource_xml(@NotNull source: String): String? {
        val index = source.indexOf(".jpg")
        if (index > 0) {
            val beforeIndex = source.lastIndexOf("<url>", index, true)
            val afterIndex = source.indexOf("</url>", index, true)
            if (beforeIndex > 0 && afterIndex > 0) {
                val resultStr = genUrl + source.substring(beforeIndex + "<url>".length, afterIndex)
                if (resultStr.contains("1366x768")) {
                    return resultStr.replace("1366x768", "1920x1080")
                }
                return resultStr
            }
        }
        return null
    }
}