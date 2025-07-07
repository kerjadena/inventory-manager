package com.example.inventorymanager.ui.categorylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventorymanager.data.local.ProductDatabase
import com.example.inventorymanager.data.network.NetworkModule
import com.example.inventorymanager.data.repository.ProductRepository
import com.example.inventorymanager.databinding.FragmentCategoryListBinding

class CategoryListFragment : Fragment() {

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CategoryListViewModel
    private lateinit var adapter: CategoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupViewModel() {
        val database = ProductDatabase.getDatabase(requireContext())
        val repository = ProductRepository(NetworkModule.apiService, database.productDao())

        // Direct ViewModelProvider without custom factory
        viewModel = ViewModelProvider(this)[CategoryListViewModel::class.java]
        viewModel.initRepository(repository)
    }

    private fun setupRecyclerView() {
        adapter = CategoryListAdapter { category ->
            // Simple navigation without Safe Args for now
            findNavController().navigate(
                com.example.inventorymanager.R.id.action_categoryList_to_productByCategory,
                Bundle().apply { putString("category", category) }
            )
        }

        binding.recyclerViewCategories.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@CategoryListFragment.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshCategories()
        }
    }

    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.submitList(categories)
            binding.textEmpty.visibility = if (categories.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
            binding.progressBar.visibility = if (isLoading && adapter.itemCount == 0) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
