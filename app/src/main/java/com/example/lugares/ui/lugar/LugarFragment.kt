package com.example.lugares.ui.lugar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lugares.R
import com.example.lugares.adapter.LugarAdapter
import com.example.lugares.databinding.FragmentLugarBinding
import com.example.lugares.viewmodel.LugarViewModel

class LugarFragment : Fragment() {
    private var _binding: FragmentLugarBinding? = null
    private val binding get() = _binding!!
    private lateinit var lugarViewModel: LugarViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lugarViewModel =
            ViewModelProvider(this).get(LugarViewModel::class.java)
        _binding = FragmentLugarBinding.inflate(inflater, container, false)
        binding.addLugar.setOnClickListener {
            findNavController().navigate(R.id.action_nav_lugar_to_addLugarFragment)
        }

        //Activar el reciclador...
        val lugarAdapter=LugarAdapter()
        val reciclador = binding.reciclador
        reciclador.adapter = lugarAdapter
        reciclador.layoutManager = LinearLayoutManager(requireContext())
        lugarViewModel.getAllData.observe(viewLifecycleOwner) {
            lugarAdapter.setData(it)
        }


        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}