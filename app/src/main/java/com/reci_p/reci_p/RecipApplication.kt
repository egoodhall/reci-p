package com.reci_p.reci_p

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.reci_p.reci_p.helpers.DataManager

/**
 * Created by jordan on 11/11/17.
 */
class RecipApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        DataManager.initialize(applicationContext)
    }
}