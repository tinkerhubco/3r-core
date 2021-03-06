package com.tinkerhub.replenish.sources.user

import android.content.Context
import com.tinkerhub.replenish.data.managers.IDataStoreManager
import com.tinkerhub.replenish.data.models.RewardItem
import com.tinkerhub.replenish.data.models.User
import com.tinkerhub.replenish.network.ApiService
import com.tinkerhub.replenish.network.Result
import com.tinkerhub.replenish.network.wrapWithResult
import com.tinkerhub.replenish.sources.BaseRemoteSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRemoteSource(
    context: Context,
    private val apiService: ApiService
) : BaseRemoteSource(context), IUserRemoteSource {
    
    override suspend fun requestGetUserProfile(): Result<User> {
        return try {
            val response = withContext(Dispatchers.IO) {
                apiService.getSelf()
            }
            response.wrapWithResult()
        } catch (exception: CancellationException) {
            Result.Cancelled()
        } catch (exception: Exception) {
            getDefaultErrorResponse()
        }
    }
}

interface IUserRemoteSource {
    suspend fun requestGetUserProfile(): Result<User>
}