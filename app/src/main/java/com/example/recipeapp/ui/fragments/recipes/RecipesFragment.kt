package com.example.recipeapp.ui.fragments.recipes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.viewmodels.MainViewModel
import com.example.recipeapp.R
import com.example.recipeapp.adapters.RecipesAdapter
import com.example.recipeapp.util.NetworkResult
import com.example.recipeapp.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint

import com.facebook.shimmer.ShimmerFrameLayout;

@AndroidEntryPoint
class RecipesFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private val mAdapter by lazy { RecipesAdapter() }
    private lateinit var mView: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_recipes, container, false)

        setupRecyclerView()
        requestApiData()


        return mView
    }

    private fun requestApiData() {
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { mAdapter.setData(it) }
                }

                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        })
    }

    private fun setupRecyclerView() {
        mView.findViewById<RecyclerView>(R.id.recyclerview).adapter = mAdapter
        mView.findViewById<RecyclerView>(R.id.recyclerview).layoutManager =
            LinearLayoutManager(requireContext())
        showShimmerEffect()
    }


    private fun showShimmerEffect() {
        mView.findViewById<ShimmerFrameLayout>(R.id.shimmerFrameLayout).startShimmer()
        mView.findViewById<ShimmerFrameLayout>(R.id.shimmerFrameLayout).visibility = View.VISIBLE
        mView.findViewById<RecyclerView>(R.id.recyclerview).visibility = View.GONE
    }

    private fun hideShimmerEffect() {
        mView.findViewById<ShimmerFrameLayout>(R.id.shimmerFrameLayout).stopShimmer()
        mView.findViewById<ShimmerFrameLayout>(R.id.shimmerFrameLayout).visibility = View.GONE
        mView.findViewById<RecyclerView>(R.id.recyclerview).visibility = View.VISIBLE
    }


}