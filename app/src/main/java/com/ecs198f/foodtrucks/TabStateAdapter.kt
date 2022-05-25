package com.ecs198f.foodtrucks

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter



class TabStateAdapter(fragment: Fragment, list1: List<FoodItem>, list2: List<FoodReview>, truckId: String): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2
    val list1 = list1
    val list2 = list2
    val truckid = truckId
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment

        when(position){
            0 ->{
                 fragment = FoodTruckMenuFragment(list1)
            }
            1 ->{
                 fragment = FoodTruckReviewsFragment(list2, truckid)
            }
            else ->{
                throw Exception("exception")
            }
        }

        return fragment
    }


}