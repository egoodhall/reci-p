package com.reci_p.reci_p

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.reci_p.reci_p.helpers.DataManager
import io.realm.Realm

/**
 * Created by jordan on 11/11/17.
 */
class RecipApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        Realm.init(applicationContext)
        DataManager.initialize(applicationContext)
    }
}