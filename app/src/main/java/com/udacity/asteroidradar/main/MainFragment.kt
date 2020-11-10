package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.AsteroidAdapter
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {
/*
    private val viewModel: MainViewModel by lazy {

       val application =requireNotNull(this.activity).application
        val viewModelFactory = ViewModelFactory(application)
        ViewModelProvider(this,viewModelFactory)
            .get(MainViewModel::class.java)
    }*/

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {}
        val viewModelFactory = ViewModelFactory(activity.application)
        ViewModelProvider(this,viewModelFactory)
            .get(MainViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        setHasOptionsMenu(true)

        viewModel.navigateToDetailFragment.observe(viewLifecycleOwner, Observer {
            if(it != null){
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.shownAsteroidDetail()
            }
        })

        binding.asteroidRecycler.adapter = AsteroidAdapter(
            AsteroidAdapter.OnClickListener{
            viewModel.displayAsteroidDetails(it)
        }
        )

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}
