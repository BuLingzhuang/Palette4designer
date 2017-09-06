package com.bulingzhuang.palette4designer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.bulingzhuang.palette4designer.adapters.ContentAdapter
import com.bulingzhuang.palette4designer.bean.ContentModel
import com.bulingzhuang.palette4designer.utils.BaseObserver
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    companion object {
        val REQUEST_CODE_CHOOSE = 466
        val REQUEST_CODE_PERMISSIONS = 699
    }

    private lateinit var mAdapter: ContentAdapter
    private val mCompositeSubscription: CompositeDisposable = CompositeDisposable()

//    private val mHandler: Handler = Handler {
//        val data = it.data
//        val key = data.getInt("key", -1)
//        if (key ==233) {
//            val htmlSource = data.getString("imgUrl","")
//            if (htmlSource.isNotEmpty()) {
//                Glide.with(this).load(htmlSource).into(iv_content)
//            }
//        }
//        true
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_CODE_PERMISSIONS)
            } else {
                getImg()
            }
        }
        mAdapter = ContentAdapter(this)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_content.layoutManager = layoutManager
        rv_content.adapter = mAdapter
    }

    override fun onDestroy() {
        if (mCompositeSubscription.isDisposed) {
            mCompositeSubscription.dispose()
        }
        super.onDestroy()
    }

    private fun getImg() {
        Matisse.from(this).choose(MimeType.of(MimeType.PNG, MimeType.JPEG)).countable(true).maxSelectable(9)
                .imageEngine(GlideEngine()).theme(R.style.Matisse_Dracula).forResult(REQUEST_CODE_CHOOSE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (permissions.isNotEmpty()) {
                getImg()
            } else {
                Toast.makeText(this, "没有权限，干不了活", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            val matisseData = Matisse.obtainResult(data)
            Log.e("blz", matisseData.toString())

            addSubscription(Observable.create { e: ObservableEmitter<List<ContentModel>>? ->
                run {
                    Log.e("blz","开始后台计算：${System.currentTimeMillis()}")
                    val resultList: MutableList<ContentModel> = ArrayList()

                    var bitmap:Bitmap? = null

                    matisseData.forEach {
                        Log.e("blz","图片：$it")
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)

                        //处理图片宽高
                        val heightDp = 300f * bitmap!!.height / bitmap!!.width
                        //计算色值
                        val builder = Palette.from(bitmap)
                        val palette = builder.generate()

                        resultList.add(ContentModel(it,heightDp,300f,
                                palette.dominantSwatch,palette.vibrantSwatch,
                                palette.darkVibrantSwatch,palette.lightVibrantSwatch,
                                palette.mutedSwatch,palette.darkMutedSwatch,palette.lightMutedSwatch))
                    }
                    bitmap?.recycle()
                    bitmap = null

                    e?.onNext(resultList)
                }
            }, object : BaseObserver<List<ContentModel>>() {
                override fun onComplete() {
                }

                override fun onNext(data: List<ContentModel>?) {
                    Log.e("blz","计算完毕：${System.currentTimeMillis()},listSize=${data?.size}")
                    if (data != null) {
                        mAdapter.refreshData(data)
                    }
                }

                override fun onError(e: Throwable?) {

                }

            })





        }
    }


    private fun <T> addSubscription(observable: Observable<T>, observer: BaseObserver<T>) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
        mCompositeSubscription.add(observer.disposable)
    }
}
