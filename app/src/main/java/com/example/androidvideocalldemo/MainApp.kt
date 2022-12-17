package com.example.androidvideocalldemo


import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gcore.videocalls.meet.GCoreMeet

class MainApp: Application() {
    override fun onCreate(){
        super.onCreate()

        initializeGCoreSDK()
        checkInternetAvailability()
    }
    private fun initializeGCoreSDK(){
        GCoreMeet.instance.init(this@MainApp, null  , false)

        var webRtcHost = getString(R.string.webrtc_host)
        var port = resources.getInteger(R.integer.webrtc_port)
        var clientHostName = getString(R.string.client_host_name)
        var hostName = getString(R.string.hostname)


    }
    private fun checkInternetAvailability(){
        (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply{
            registerDefaultNetworkCallback(defaultNetworkCallback)
        }
    }
    private val defaultNetworkCallback = object : ConnectivityManager.NetworkCallback(){
        override fun onAvailable(network: Network) {
            mutableNetworkIsAvailable.postValue(true)
        }
    }

    override fun onUnavailable(){
        mutableNetworkIsAvailable.postValue(false)
    }
    override  fun onLost(network: Network) {
        mutableNetworkIsAvailable.postValue(false)
    }

    companion object{
        private val mutableNetworkIsAvailable = MutableLiveData(false)
        val isAvailableNetwork: LiveData<Boolean> = mutableNetworkIsAvailable
    }
}