package com.livestreaming.channelize.io.di

import com.livestreaming.channelize.io.activity.login.ILoginRepositoryCallBack
import com.livestreaming.channelize.io.activity.login.ILoginRepositoryImpl
import com.livestreaming.channelize.io.activity.login.LoginUserViewModelFact
import dagger.Module
import dagger.Provides

@Module
class LoginUserModule {

    @Provides
    fun providesLoginInstance(): ILoginRepositoryCallBack {
        return ILoginRepositoryImpl()
    }

    @Provides
    fun providesLoginViewModelFactInstance(iLoginRepositoryCallBack: ILoginRepositoryCallBack): LoginUserViewModelFact {
        return LoginUserViewModelFact(iLoginRepositoryCallBack)
    }

}