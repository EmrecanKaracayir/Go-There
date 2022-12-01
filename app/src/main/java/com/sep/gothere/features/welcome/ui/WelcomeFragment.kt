package com.sep.gothere.features.welcome.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.sep.gothere.R
import com.sep.gothere.activity.BaseActivity
import com.sep.gothere.activity.BaseFragment
import com.sep.gothere.databinding.FragmentWelcomeBinding
import com.sep.gothere.features.welcome.vm.WelcomeViewModel
import com.sep.gothere.util.CustomFitsSystemUI
import com.sep.gothere.util.exhaustive
import com.sep.gothere.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WelcomeFragment : BaseFragment(R.layout.fragment_welcome) {

    private val viewModel: WelcomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FragmentWelcomeBinding.bind(view).apply {

            CustomFitsSystemUI(this@WelcomeFragment).fitsSystemUI(
                welcomeFragmentHeroLayoutFITSTOP, arrayListOf(
                    CustomFitsSystemUI.RequestedInset.TOP
                )
            )
            CustomFitsSystemUI(this@WelcomeFragment).fitsSystemUI(
                welcomeFragmentContentLayoutFITSBOTTOM, arrayListOf(
                    CustomFitsSystemUI.RequestedInset.BOTTOM
                )
            )

            usernameEditText.addTextChangedListener {
                if (usernameInputLayout.error != null || passwordInputLayout.error != null) {
                    usernameInputLayout.error = null
                    passwordInputLayout.error = null

                    usernameInputLayout.isErrorEnabled = false
                    passwordInputLayout.isErrorEnabled = false
                }
                viewModel.setUsername(it.toString())
            }

            passwordEditText.addTextChangedListener {
                if (usernameInputLayout.error != null || passwordInputLayout.error != null) {
                    usernameInputLayout.error = null
                    passwordInputLayout.error = null

                    usernameInputLayout.isErrorEnabled = false
                    passwordInputLayout.isErrorEnabled = false
                }
                viewModel.setPassword(it.toString())
            }

            this@WelcomeFragment.lifecycleScope.launchWhenStarted {
                viewModel.isLoginButtonEnabled.collect { value ->
                    loginButton.isEnabled = value
                }
            }

            signUpButton.setOnClickListener {
                hideKeyboard()
                (activity as BaseActivity).signUpFragmentRequested()
            }

            loginButton.setOnClickListener {
                hideKeyboard()
                this@WelcomeFragment.lifecycleScope.launchWhenStarted {
                    viewModel.loginButtonClicked(
                        usernameEditText.text.toString(), passwordEditText.text.toString()
                    )
                }
            }

            this@WelcomeFragment.lifecycleScope.launchWhenStarted {
                viewModel.events.collect { event ->
                    when (event) {
                        is WelcomeViewModel.Event.LoginLoading -> loginProgressBar.visibility =
                            View.VISIBLE
                        is WelcomeViewModel.Event.LoginSuccessful -> {
                            loginProgressBar.visibility = View.INVISIBLE

                            if (event.response.success) (activity as BaseActivity).loggedIn()
                            else {
                                usernameInputLayout.isErrorEnabled = true
                                passwordInputLayout.isErrorEnabled = true
                                usernameInputLayout.error =
                                    resources.getString(R.string.login_error_wrong_credentials)
                                passwordInputLayout.error =
                                    resources.getString(R.string.login_error_wrong_credentials)

                                showSnackbar(resources.getString(R.string.uncategorized_error))
                            }
                        }
                        is WelcomeViewModel.Event.LoginError -> {
                            loginProgressBar.visibility = View.INVISIBLE
                            showSnackbar(resources.getString(R.string.uncategorized_error) + "LOG: ${event.error.message}")
                        }
                    }.exhaustive
                }
            }
        }
    }

    private fun hideKeyboard() {
        (requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            requireView().windowToken, 0
        )
    }
}