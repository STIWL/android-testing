package com.luisansal.jetpack.features.manageusers

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.luisansal.jetpack.core.domain.entity.UserEntity

sealed class UserViewState {
    data class ErrorState(val error: Throwable?) : UserViewState()
    data class LoadingState(val isLoading: Boolean) : UserViewState()
    data class NewUserSuccess(val userEntity: UserEntity?) : UserViewState()
    data class GetUserSuccessState(val userEntity: UserEntity?) : UserViewState()
    data class DeleteSuccessState(val data: Boolean = false) : UserViewState()
    data class DeleteAllSuccessState(val data: Boolean = false) : UserViewState()
    data class ListSuccessState(val data: List<UserEntity>?) : UserViewState()
    data class ListSuccessPagedState(val data: LiveData<PagedList<UserEntity>>?) : UserViewState()
}