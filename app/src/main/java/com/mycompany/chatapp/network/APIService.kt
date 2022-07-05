package com.mycompany.chatapp.network

import com.mycompany.chatapp.notifications.MyResponse
import com.mycompany.chatapp.notifications.Sender
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {
    @Headers(
        "Content-type:application/json",
        "Authorization:key=AAAA5xvVfkY:APA91bEDAea_P7IymqY7gkd2vf1bBbFxlO_Fq6Zv8V59bOO4vwjjtzhqcTfjou1QWzsyEveQ55Z0-r1N_2rGPlrD7iOVtp-A8z84Tjo80LmgiTMt2NmDwaXm3JcEMbzNS9apjz66NOX4"
    )
    @POST("fcm/send")
    fun sendNotifications(@Body body: Sender): Call<MyResponse>
}