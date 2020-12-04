package com.livestreaming.channelize.io.di

import com.livestreaming.channelize.io.activity.login.ILoginRepository
import com.livestreaming.channelize.io.activity.login.ILoginRepositoryImpl
import com.livestreaming.channelize.io.activity.login.LoginUserViewModelFact
import dagger.Module
import dagger.Provides

@Module
class LoginUserModule {

    @Provides
    fun providesLoginInstance(): ILoginRepository {
        return ILoginRepositoryImpl()
    }

    @Provides
    fun providesLoginViewModelFactInstance(iLoginRepository: ILoginRepository): LoginUserViewModelFact {
        return LoginUserViewModelFact(iLoginRepository)
    }

}