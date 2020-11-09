package edu.stanford.chiajui.simpleyelp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val TAG = "MainActivity"
private const val BASE_URL = "https://api.yelp.com/v3/"
private const val API_KEY = "-qG-MyrGB_d1dck2dAjK8qYr0pbjW9EaNLcrFmRTNGfble3VxOWglfhYFOUVPn_Nf9xsE0pdE0hvRgT0RgG7vCti_Xpz71nIwL6fkJq_E-Z6lGGnrWeat0ycYRynX3Yx"
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val restaurants = mutableListOf<YelpRestaurant>()
        val adapter = RestaurantsAdapter(this, restaurants)
        rvRestaurants.adapter = adapter
        rvRestaurants.layoutManager = LinearLayoutManager(this)

        val retrofit =
                Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                        .build()
        val yelpService = retrofit.create(YelpService::class.java)
        yelpService.searchRestaurants("Bearer $API_KEY", "Avocado Toast", "New York").enqueue(object :
            Callback<YelpSearchResult> {
            override fun onResponse(
                call: Call<YelpSearchResult>,
                response: Response<YelpSearchResult>
            ) {
                Log.i(TAG, "onResponse $response")
                val body = response.body()
                if (body == null) {
                    Log.w(TAG, "Did not receive valid response body from Yelp API... exiting")
                    return
                }
                restaurants.addAll(body.restaurants)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error, please check your internet connection and try again!", Toast.LENGTH_LONG).show()
                Log.i(TAG, "onFailure $t")
            }

        })
    }
}