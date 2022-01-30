package com.luisansal.jetpack.features.miniUber.auth.login

import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import com.luisansal.jetpack.R
import com.luisansal.jetpack.core.base.BaseBindingFragment
import com.luisansal.jetpack.databinding.FragmentMiniuberLoginBinding

class LoginFragment : BaseBindingFragment() {
    private val binding by lazy {
        FragmentMiniuberLoginBinding.inflate(layoutInflater).apply { lifecycleOwner = this@LoginFragment }
    }

    private val navController by lazy {
        Navigation.findNavController(requireActivity(), R.id.auth_nav_host_fragment)
    }

    override fun getViewResource() = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNewUser.setOnClickListener {
            navController.navigate(R.id.action_formLogin_to_newUser)
        }
    }
}