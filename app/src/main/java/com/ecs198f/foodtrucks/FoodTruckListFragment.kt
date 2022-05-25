package com.ecs198f.foodtrucks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecs198f.foodtrucks.databinding.FragmentFoodTruckListBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodTruckListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFoodTruckListBinding.inflate(inflater, container, false)
        val recyclerViewAdapter = FoodTruckListRecyclerViewAdapter(listOf())



        binding.foodTruckListRecyclerView.apply {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(context)
        }

        (requireActivity() as MainActivity).apply {
            title = "Food Trucks"

            foodTruckService.listFoodTrucks().enqueue(object : Callback<List<FoodTruck>> {
                override fun onResponse(
                    call: Call<List<FoodTruck>>,
                    response: Response<List<FoodTruck>>
                ) {
                    lifecycleScope.launch {
                        db.foodtruckDao().removeAllTruck()
                        db.foodtruckDao().addTrucks(response.body()!!)
                        Log.d("remove&add new trucks", "DONE")
                    }
                    recyclerViewAdapter.updateItems(response.body()!!)
                }

                override fun onFailure(call: Call<List<FoodTruck>>, t: Throwable) {
                    lifecycleScope.launch {
                        recyclerViewAdapter.updateItems(db.foodtruckDao().listAllFoodTrucks())
                    }

                   // throw t
                }
            })
        }

        return binding.root
    }
}
