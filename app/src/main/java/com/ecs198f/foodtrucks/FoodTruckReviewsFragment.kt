package com.ecs198f.foodtrucks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ecs198f.foodtrucks.databinding.FragmentFoodTruckReviewsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


const val RC_SIGN_IN = 123

class FoodTruckReviewsFragment(list: List<FoodReview>, truckId: String) : Fragment() {



    private lateinit var bindingReview: FragmentFoodTruckReviewsBinding
    val truckId = truckId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingReview = FragmentFoodTruckReviewsBinding
            .inflate(inflater, container, false)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("699423245763-insnbbp034ep600msiqfan5g0b2pau67.apps.googleusercontent.com")
            .requestEmail()
            .build()



        lateinit var mGoogleSignInClient:GoogleSignInClient
        (requireActivity() as MainActivity).apply {
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
//            fun signIn() {
//                Log.d("Sign In", "called")
//                val signInIntent = mGoogleSignInClient.signInIntent
//                startActivityForResult(signInIntent, RC_SIGN_IN)
//            }


            var avatarUrl:List<String>





            var reviewContent: String

            bindingReview.BottonPostReview.setOnClickListener{

                reviewContent = bindingReview.editTextReview.getText().toString()
                Log.d("Review Content = ", reviewContent)
                val PostContent: FoodReviewPost
                val acct = GoogleSignIn.getLastSignedInAccount(this)
                if (acct != null) {
                    //val personName = acct.displayName.toString()
                    //Log.d("personName =", personName)
                    val personPhoto: String = acct.photoUrl.toString()
                    val idToken = acct.idToken.toString()
                    avatarUrl = listOf(personPhoto)
                    foodTruckService.createFoodReviews( truckId,
                        FoodReviewPost(
                            reviewContent,
                            avatarUrl), "Bearer " + idToken).enqueue(object : Callback<Unit>{
                        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                            Log.d("Sent Post Request", idToken)
                            Log.d("truck id", truckId)
                        }

                        override fun onFailure(call: Call<Unit>, t: Throwable) {
                            throw t
                        }

                    })

                }


            }

        }

        bindingReview.BottonSignIn.setOnClickListener{
            //signIn()
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

        }

        return bindingReview.root
        //return inflater.inflate(R.layout.fragment_food_truck_reviews, container, false)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        Log.d("onActivityResult", requestCode.toString())
        if (requestCode == RC_SIGN_IN) {
            Log.d("onActivityResult", "success")
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }


    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        Log.d("handleSignIn", "handleSignIn")
        try {
            val account:GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            Log.d("Catch", e.statusCode.toString())
            bindingReview.BottonSignIn.visibility = View.VISIBLE
        }
    }

    val list = list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.foodReviewListRecyclerView)
        val recyclerViewAdapter = FoodReviewRecyclerViewAdapter(listOf())
        recyclerView.apply {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(context)
        }
        recyclerViewAdapter.updateItems(list)

    }

    private fun updateUI(account:GoogleSignInAccount?) {
        Log.d("updateUI", if (account == null) "null" else account.displayName!!)
        if(account != null)
        {
            bindingReview.BottonSignIn.visibility = View.INVISIBLE
        }
        else
        {
            bindingReview.BottonSignIn.visibility = View.VISIBLE
        }
    }

    override fun onStart(){
        super.onStart()


        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        (requireActivity() as MainActivity).apply {
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account != null) {
                updateUI(account)
            }

        }


    }
}