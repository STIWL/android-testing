package com.luisansal.jetpack.features.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.luisansal.jetpack.R
import com.luisansal.jetpack.base.BaseFragment
import com.luisansal.jetpack.common.interfaces.TitleListener
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpAuthorizer
import kotlinx.android.synthetic.main.fragment_chat.*


class ChatFragment : BaseFragment(), TitleListener {

    override fun getViewIdResource() = R.layout.fragment_chat

    private val pusher by lazy {
        val options = PusherOptions()
        options.setCluster("us2")

        val authorizer = HttpAuthorizer("http://192.168.8.131:8080/broadcasting/auth")
        val headers = HashMap<String, String>()
        headers.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC9sb2NhbGhvc3Q6ODA4MFwvYXBpXC9sb2dpbiIsImlhdCI6MTYwNDAwMTQ0MSwiZXhwIjoxNjA0MDA1MDQxLCJuYmYiOjE2MDQwMDE0NDEsImp0aSI6IlpwbHVyR2lIMTF1c25Fa1MiLCJzdWIiOjIsInBydiI6Ijg3ZTBhZjFlZjlmZDE1ODEyZmRlYzk3MTUzYTE0ZTBiMDQ3NTQ2YWEifQ.36Sahq9YSZqlFXFfxwgDYi5tG1KX1plIoInSG3vyqzI")
        authorizer.setHeaders(headers)
        options.setAuthorizer(authorizer)
        Pusher("4e35e36665678aca4b6b", options)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                Log.i("Pusher", "State changed from ${change.previousState} to ${change.currentState}")
            }

            override fun onError(
                    message: String,
                    code: String?,
                    e: Exception?
            ) {
                Log.i("Pusher", "There was a problem connecting! code ($code), message ($message), exception($e)")
            }
        }, ConnectionState.ALL)

        val channel = pusher.subscribePrivate("private-chat.2")

        channel.bind("App\\Events\\MessageEvent", object : PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent?) {
                Log.i("event2", "$event")
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "$event", Toast.LENGTH_LONG).show()
                }
            }

            override fun onAuthenticationFailure(message: String?, e: java.lang.Exception?) {
                Log.i("message2", "$message")
            }

            override fun onSubscriptionSucceeded(channelName: String?) {
                Log.i("channelName2", "$channelName")

            }
        })



        onClickBtnSend()
    }

    fun onClickBtnSend() {
        btnSendMessage?.setOnClickListener {
            pusher.connect()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChatFragment()
    }

    override val title: String = "Chat Pusher"
}