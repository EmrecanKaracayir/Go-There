package com.sep.gothere.features.root_prelog.welcome.leaf_signup.ui

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sep.gothere.R
import com.sep.gothere.base.BaseActivity
import com.sep.gothere.base.BaseFragment
import com.sep.gothere.component.dynamic.impl.input.text.GTTextInputLayout
import com.sep.gothere.component.dynamic.schema.AccentStylableView
import com.sep.gothere.databinding.FragmentSignUpBinding
import com.sep.gothere.helpers.color.FeatureStylizer
import com.sep.gothere.features.root_prelog.welcome.leaf_signup.vm.SignUpViewModel
import com.sep.gothere.helpers.color.HSL
import com.sep.gothere.navigation.NavigationInfoProvider
import com.sep.gothere.util.CustomFitsSystemUI
import com.sep.gothere.util.DelayedTextWatcher
import com.sep.gothere.util.exhaustive
import com.sep.gothere.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : BaseFragment(R.layout.fragment_sign_up), NavigationInfoProvider {

    private val viewModel: SignUpViewModel by viewModels()

    // THIS PAGE HAS RANDOMLY SELECTED STATIC ACCENT COLOR
    private val accentHSL = HSL(
        Color.rgb(
            (Math.random() * 200).toInt(),
            (Math.random() * 200).toInt(),
            (Math.random() * 255).toInt()
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FragmentSignUpBinding.bind(view).apply {
            val fs = FeatureStylizer(requireContext(), accentHSL)
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    fs.findStylableComponentsAndApplyStyle(
                        root
                    )
                }
            }

            CustomFitsSystemUI().fitsSystemUI(
                signUpFragmentHeroLayoutFITSTOP,
                arrayListOf(CustomFitsSystemUI.RequestedInset.TOP)
            )
            CustomFitsSystemUI().fitsSystemUI(
                signUpFragmentContentLayoutFITSBOTTOM,
                arrayListOf(CustomFitsSystemUI.RequestedInset.BOTTOM)
            )

            componentsListenerAssigner(this)

            backButton.setOnClickListener {
                hideKeyboard()
                (activity as BaseActivity).navigateBackRequested()
            }

            this@SignUpFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.isSignUpButtonEnabled.collect { value ->
                        signUpButton.isEnabled = value
                    }
                }
            }
            signUpButton.setOnClickListener {
                hideKeyboard()
                this@SignUpFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.signUpButtonClicked(
                            nameEditText.text.toString(),
                            surnameEditText.text.toString(),
                            usernameEditText.text.toString(),
                            emailEditText.text.toString(),
                            passwordEditText.text.toString()
                        )
                    }
                }
            }

            signUpStateEventsHandler(this)

            fieldsStateEventsHandler(this)
        }
    }

    private fun componentsListenerAssigner(binding: FragmentSignUpBinding) {
        binding.apply {
            nameEditText.addTextChangedListener {
                this@SignUpFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validateNameField(it.toString())
                    }
                }
            }
            surnameEditText.addTextChangedListener {
                this@SignUpFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validateSurnameField(it.toString())
                    }
                }
            }
            usernameEditText.addTextChangedListener(DelayedTextWatcher {
                this@SignUpFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validateUsernameField(it.toString())
                    }
                }
            })
            emailEditText.addTextChangedListener(DelayedTextWatcher {
                this@SignUpFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validateEmailField(it.toString())
                    }
                }
            })
            passwordEditText.addTextChangedListener {
                this@SignUpFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validatePasswordField(it.toString())
                    }
                }
            }
            confirmPasswordEditText.addTextChangedListener {
                this@SignUpFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validateConfirmPasswordField(
                            it.toString(), passwordEditText.text.toString()
                        )
                    }
                }
            }
            termsAndConditionsCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    this@SignUpFragment.lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.validateTermsConditionsField(
                                SignUpViewModel.TermsConditionsFieldState.VALID
                            )
                        }
                    }
                } else {
                    this@SignUpFragment.lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.validateTermsConditionsField(
                                SignUpViewModel.TermsConditionsFieldState.INVALID
                            )
                        }
                    }
                }
            }
            termsAndConditionsButton.setOnClickListener {
                openTermsConditionsDialog(binding)
            }
        }
    }

    private fun openTermsConditionsDialog(binding: FragmentSignUpBinding) {
        MaterialAlertDialogBuilder(
            requireContext(), R.style.GT_ThemeOverlay_App_MaterialAlertDialog
        ).setIcon(
            ResourcesCompat.getDrawable(
                resources, R.drawable.terms_and_conditions, requireContext().theme
            )
        ).setTitle(resources.getString(R.string.terms_and_conditions_dialog_title))
            .setMessage(resources.getString(R.string.terms_and_conditions_full))
            .setNegativeButton(resources.getString(R.string.terms_and_conditions_dialog_decline_button)) { dialog, _ ->
                this@SignUpFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        binding.termsAndConditionsCheckbox.isChecked = false
                        viewModel.validateTermsConditionsField(SignUpViewModel.TermsConditionsFieldState.INVALID)
                    }
                }
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.terms_and_conditions_dialog_accept_button)) { dialog, _ ->
                binding.termsAndConditionsCheckbox.isChecked = true
                this@SignUpFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validateTermsConditionsField(SignUpViewModel.TermsConditionsFieldState.VALID)
                    }
                }
                dialog.dismiss()
            }.show()
    }

    private fun signUpStateEventsHandler(binding: FragmentSignUpBinding) {
        binding.apply {
            this@SignUpFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.signUpEvents.collect { event ->
                        when (event) {
                            is SignUpViewModel.SignUpRequestEvent.SignUpRequestLoading -> makeGeneralProgressBarsVisible(
                                this@apply
                            )
                            is SignUpViewModel.SignUpRequestEvent.SignUpRequestSuccessful -> {
                                makeGeneralProgressBarsInvisible(this@apply)
                                if (event.response.success) {
                                    if (event.response.data != null)
                                        (activity as BaseActivity).loggedIn(event.response.data.accessToken)
                                    else showSnackbar(resources.getString(R.string.uncategorized_error))
                                } else showSnackbar(resources.getString(R.string.uncategorized_error) + " LOG: ${event.response.message}")
                            }
                            is SignUpViewModel.SignUpRequestEvent.SignUpRequestError -> {
                                makeGeneralProgressBarsInvisible(this@apply)
                                showSnackbar(resources.getString(R.string.uncategorized_error) + " LOG: ${event.error.message}")
                            }
                        }.exhaustive
                    }
                }
            }
        }
    }

    //region Field State
    private fun fieldsStateEventsHandler(binding: FragmentSignUpBinding) {
        binding.apply {
            this@SignUpFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.nameFieldStateEvents.collect { event ->
                        when (event) {
                            SignUpViewModel.NameFieldStatus.INITIAL -> {
                                fieldStateChanger(
                                    binding,
                                    binding.nameInputLayout,
                                    AccentStylableView.StateStyle.DEFAULT_STYLE_STATE
                                )
                            }
                            SignUpViewModel.NameFieldStatus.VALID -> {
                                fieldStateChanger(
                                    binding,
                                    binding.nameInputLayout,
                                    AccentStylableView.StateStyle.SUCCESS_STYLE_STATE
                                )
                            }
                        }.exhaustive
                    }
                }
            }

            this@SignUpFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.surnameFieldStateEvents.collect { event ->
                        when (event) {
                            SignUpViewModel.SurnameFieldState.INITIAL -> {
                                fieldStateChanger(
                                    binding,
                                    binding.surnameInputLayout,
                                    AccentStylableView.StateStyle.DEFAULT_STYLE_STATE
                                )
                            }
                            SignUpViewModel.SurnameFieldState.VALID -> {
                                fieldStateChanger(
                                    binding,
                                    binding.surnameInputLayout,
                                    AccentStylableView.StateStyle.SUCCESS_STYLE_STATE
                                )
                            }
                        }.exhaustive
                    }
                }
            }

            this@SignUpFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.usernameFieldStateEvents.collect { event ->
                        usernameCheckPB.visibility = View.GONE
                        when (event) {
                            SignUpViewModel.UsernameFieldState.INITIAL -> {
                                fieldStateChanger(
                                    binding,
                                    binding.usernameInputLayout,
                                    AccentStylableView.StateStyle.DEFAULT_STYLE_STATE
                                )
                            }
                            SignUpViewModel.UsernameFieldState.LOADING_NETWORK_REQUEST -> {
                                fieldStateChanger(
                                    binding,
                                    binding.usernameInputLayout,
                                    AccentStylableView.StateStyle.DEFAULT_STYLE_STATE
                                )
                                usernameCheckPB.visibility = View.VISIBLE
                            }
                            SignUpViewModel.UsernameFieldState.FAILED_NETWORK_REQUEST -> {
                                fieldStateChanger(
                                    binding,
                                    binding.usernameInputLayout,
                                    AccentStylableView.StateStyle.ERROR_STYLE_STATE,
                                    getString(R.string.uncategorized_error)
                                )
                                showSnackbar(resources.getString(R.string.uncategorized_error) + " CAUSE: Check network!")
                            }
                            SignUpViewModel.UsernameFieldState.INVALID_ALREADY_IN_USE -> {
                                fieldStateChanger(
                                    binding,
                                    binding.usernameInputLayout,
                                    AccentStylableView.StateStyle.ERROR_STYLE_STATE,
                                    getString(R.string.signUp_error_username_taken)
                                )
                            }
                            SignUpViewModel.UsernameFieldState.VALID -> {
                                fieldStateChanger(
                                    binding,
                                    binding.usernameInputLayout,
                                    AccentStylableView.StateStyle.SUCCESS_STYLE_STATE
                                )
                            }
                        }.exhaustive
                    }
                }
            }

            this@SignUpFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.emailFieldStateEvents.collect { event ->
                        emailCheckPB.visibility = View.GONE
                        when (event) {
                            SignUpViewModel.EmailFieldState.INITIAL -> {
                                fieldStateChanger(
                                    binding,
                                    binding.emailInputLayout,
                                    AccentStylableView.StateStyle.DEFAULT_STYLE_STATE
                                )
                            }
                            SignUpViewModel.EmailFieldState.LOADING_NETWORK_REQUEST -> {
                                fieldStateChanger(
                                    binding,
                                    binding.emailInputLayout,
                                    AccentStylableView.StateStyle.DEFAULT_STYLE_STATE
                                )
                                emailCheckPB.visibility = View.VISIBLE
                            }
                            SignUpViewModel.EmailFieldState.FAILED_NETWORK_REQUEST -> {
                                fieldStateChanger(
                                    binding,
                                    binding.emailInputLayout,
                                    AccentStylableView.StateStyle.ERROR_STYLE_STATE,
                                    getString(R.string.uncategorized_error)
                                )
                                showSnackbar(resources.getString(R.string.uncategorized_error) + "CAUSE: Check network!")
                            }
                            SignUpViewModel.EmailFieldState.INVALID_FORMAT -> {
                                fieldStateChanger(
                                    binding,
                                    binding.emailInputLayout,
                                    AccentStylableView.StateStyle.ERROR_STYLE_STATE,
                                    getString(R.string.error_email_format)
                                )
                            }
                            SignUpViewModel.EmailFieldState.INVALID_ALREADY_IN_USE -> {
                                fieldStateChanger(
                                    binding,
                                    binding.emailInputLayout,
                                    AccentStylableView.StateStyle.ERROR_STYLE_STATE,
                                    getString(R.string.signUp_error_email_taken)
                                )
                            }
                            SignUpViewModel.EmailFieldState.VALID -> {
                                fieldStateChanger(
                                    binding,
                                    binding.emailInputLayout,
                                    AccentStylableView.StateStyle.SUCCESS_STYLE_STATE
                                )
                            }
                        }.exhaustive
                    }
                }
            }

            this@SignUpFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.passwordFieldStateEvents.collect { event ->
                        when (event) {
                            SignUpViewModel.PasswordFieldState.INITIAL -> {
                                fieldStateChanger(
                                    binding,
                                    binding.passwordInputLayout,
                                    AccentStylableView.StateStyle.DEFAULT_STYLE_STATE
                                )
                            }
                            SignUpViewModel.PasswordFieldState.INVALID_TOO_SHORT -> {
                                fieldStateChanger(
                                    binding,
                                    binding.passwordInputLayout,
                                    AccentStylableView.StateStyle.ERROR_STYLE_STATE,
                                    getString(R.string.signUp_error_short_password)
                                )
                            }
                            SignUpViewModel.PasswordFieldState.VALID -> {
                                fieldStateChanger(
                                    binding,
                                    binding.passwordInputLayout,
                                    AccentStylableView.StateStyle.SUCCESS_STYLE_STATE
                                )
                            }
                        }.exhaustive
                    }
                }
            }

            this@SignUpFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.confirmPasswordFieldStateEvents.collect { event ->
                        when (event) {
                            SignUpViewModel.ConfirmPasswordFieldState.INITIAL -> {
                                fieldStateChanger(
                                    binding,
                                    binding.confirmPasswordInputLayout,
                                    AccentStylableView.StateStyle.DEFAULT_STYLE_STATE
                                )
                            }
                            SignUpViewModel.ConfirmPasswordFieldState.INVALID_PASSWORDS_DO_NOT_MATCH -> {
                                fieldStateChanger(
                                    binding,
                                    binding.confirmPasswordInputLayout,
                                    AccentStylableView.StateStyle.ERROR_STYLE_STATE,
                                    getString(R.string.signUp_error_passwords_do_not_match)
                                )
                            }
                            SignUpViewModel.ConfirmPasswordFieldState.VALID -> {
                                fieldStateChanger(
                                    binding,
                                    binding.confirmPasswordInputLayout,
                                    AccentStylableView.StateStyle.SUCCESS_STYLE_STATE
                                )
                            }
                        }.exhaustive
                    }
                }
            }

            this@SignUpFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.termsConditionsFieldStateEvents.collect { event ->
                        when (event) {
                            SignUpViewModel.TermsConditionsFieldState.INITIAL -> {
                                termsAndConditionsCheckbox.isErrorShown = false
                            }
                            SignUpViewModel.TermsConditionsFieldState.INVALID -> {
                                termsAndConditionsCheckbox.isErrorShown = true
                            }
                            SignUpViewModel.TermsConditionsFieldState.VALID -> {
                                termsAndConditionsCheckbox.isErrorShown = false
                            }
                        }.exhaustive
                    }
                }
            }
        }
    }

    private fun fieldStateChanger(
        binding: FragmentSignUpBinding,
        gtTextInputLayout: GTTextInputLayout,
        stateStyle: AccentStylableView.StateStyle,
        errorMessage: String = "Placeholder"
    ) {
        when (stateStyle) {
            AccentStylableView.StateStyle.DEFAULT_STYLE_STATE -> {
                gtTextInputLayout.error = null
                gtTextInputLayout.isErrorEnabled = false
            }
            AccentStylableView.StateStyle.ERROR_STYLE_STATE -> {
                gtTextInputLayout.error = errorMessage
                gtTextInputLayout.isErrorEnabled = true
            }
            AccentStylableView.StateStyle.SUCCESS_STYLE_STATE -> {
                gtTextInputLayout.error = null
                gtTextInputLayout.isErrorEnabled = false
            }
        }.exhaustive
        gtTextInputLayout.updateStyleState(stateStyle)
        binding.gridLayout.requestLayout()
    }
    //endregion

    private fun makeGeneralProgressBarsVisible(binding: FragmentSignUpBinding) {
        binding.apply {
            personalInformationPB.visibility = View.VISIBLE
            identityInformationPB.visibility = View.VISIBLE
            securityInformationPB.visibility = View.VISIBLE
        }
    }

    private fun makeGeneralProgressBarsInvisible(binding: FragmentSignUpBinding) {
        binding.apply {
            personalInformationPB.visibility = View.INVISIBLE
            identityInformationPB.visibility = View.INVISIBLE
            securityInformationPB.visibility = View.INVISIBLE
        }
    }

    private fun hideKeyboard() {
        (requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            requireView().windowToken, 0
        )
    }

    override fun getNavigationRoot() = NavigationInfoProvider.NavigationRoot.ROOT_PRELOG
    override fun getNavigationBranch() = NavigationInfoProvider.NavigationBranch.BRANCH_WELCOME
    override fun getNavigationTag() = NavigationInfoProvider.NavigationTag.TAG_SIGN_UP_FRAGMENT
    override fun isNavigationBranchOwner() = false
    override fun cacheOnBackPressed() = false
}
