package com.romanlojko.beactive

import android.os.Bundle
import android.text.style.TtsSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.romanlojko.beactive.databinding.FragmentLoginBinding
import com.romanlojko.beactive.databinding.FragmentMainApplicationBinding
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainApplication.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainApplication : Fragment() {

    lateinit var binding: FragmentMainApplicationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMainApplicationBinding.inflate(layoutInflater)

        binding.buttonAddActivity.setOnClickListener{view : View ->
            view.findNavController().navigate(R.id.action_mainApplication_to_activityCounter)
        }

        return binding.root
    }

}