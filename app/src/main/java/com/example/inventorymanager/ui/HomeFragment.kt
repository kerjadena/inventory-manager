package com.example.inventorymanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventorymanager.databinding.FragmentHomeBinding
import com.example.inventorymanager.ui.adapter.ProductAdapter
import com.example.inventorymanager.viewmodel.ProductViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by activityViewModels()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        loadProducts()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.remoteProducts.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
            binding.progressBar.visibility = View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun loadProducts() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getAllProducts(limit = 20, skip = 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
