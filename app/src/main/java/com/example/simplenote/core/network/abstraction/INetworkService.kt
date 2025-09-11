package com.example.simplenote.core.network.abstraction

import android.content.Context

interface INetworkService {
    fun userIsOnline(context: Context): Boolean
}
