
package com.livestreaming.channelize.io

import com.livestreaming.channelize.io.di.ApplicationComponent

interface Injector {

    fun createAppComponent(): ApplicationComponent

}