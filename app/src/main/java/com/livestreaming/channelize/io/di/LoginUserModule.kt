package com.livestreaming.channelize.io.di

import com.livestreaming.channelize.io.activity.login.LoginUserViewModelFact
import com.livestreaming.channelize.io.activity.login.UserLoginRepository
import dagger.Module
import dagger.Provides

@Module
class LoginUserModule {

    @Provides
    fun providesLoginInstance(): UserLoginRepository {
        return UserLoginRepository()
    }

    @Provides
    fun providesLoginViewModelFactInstance(userLoginRepository: UserLoginRepository): LoginUserViewModelFact {
        return LoginUserViewModelFact(userLoginRepository)
    }

}