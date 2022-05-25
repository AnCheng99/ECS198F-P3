package com.ecs198f.foodtrucks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.ecs198f.foodtrucks.databinding.ActivityMainBinding
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    lateinit var db: AppDatabase
    lateinit var db2: AppDatabase
    private lateinit var FoodTruckDao: FoodTruckDao
    private lateinit var FoodItemDao: FoodItemDao


    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, object : JsonDeserializer<LocalDateTime> {
            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): LocalDateTime {
                return LocalDateTime.parse(json!!.asString)
            }
        })
        .create()

    val foodTruckService: FoodTruckService =  Retrofit.Builder()
        .baseUrl("https://api.foodtruck.schedgo.com")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(FoodTruckService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "FoodTruck-db")
            .build()


        db2 = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "FoodItem-db")
            .build()

        FoodTruckDao =db.foodtruckDao()
        FoodItemDao = db2.fooditemDao()

        lifecycleScope.launch{
            var trucks = FoodTruckDao.listAllFoodTrucks()
            if (trucks.isEmpty()){
                initDatabase()
                trucks = FoodTruckDao.listAllFoodTrucks()
            }
        }
        Log.d("Database initialization", "Completed")
        title = "Food Trucks"
    }

    private suspend fun initDatabase()
    {
        var list1:List<FoodTruck>
        foodTruckService.listFoodTrucks().enqueue(object : Callback<List<FoodTruck>> {
            override fun onResponse(
                call: Call<List<FoodTruck>>,
                response: Response<List<FoodTruck>>
            ) {
                list1 = response.body()!!
                lifecycleScope.launch {
                    FoodTruckDao.addTrucks(list1)
                }
            }

            override fun onFailure(call: Call<List<FoodTruck>>, t: Throwable) {
                throw t
            }
        })



    }


}