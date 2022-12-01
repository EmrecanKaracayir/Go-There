package com.sep.gothere.features.signup.ui

import android.app.Activity
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.sep.gothere.R
import com.sep.gothere.activity.BaseActivity
import com.sep.gothere.activity.BaseFragment
import com.sep.gothere.databinding.FragmentSignUpBinding
import com.sep.gothere.features.signup.vm.SignUpViewModel
import com.sep.gothere.util.CustomFitsSystemUI
import com.sep.gothere.util.DelayedTextWatcher
import com.sep.gothere.util.exhaustive
import com.sep.gothere.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment(R.layout.fragment_sign_up) {

    private val viewModel: SignUpViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FragmentSignUpBinding.bind(view).apply {

            CustomFitsSystemUI(this@SignUpFragment).fitsSystemUI(signUpFragmentHeroLayoutFITSTOP, arrayListOf(CustomFitsSystemUI.RequestedInset.TOP))
            CustomFitsSystemUI(this@SignUpFragment).fitsSystemUI(signUpFragmentContentLayoutFITSBOTTOM, arrayListOf(CustomFitsSystemUI.RequestedInset.BOTTOM))

            componentsListenerAssigner(this)

            backButton.setOnClickListener {
                hideKeyboard()
                (activity as BaseActivity).previousFragmentRequested()
            }

            this@SignUpFragment.lifecycleScope.launchWhenStarted {
                viewModel.isSignUpButtonEnabled.collect { value ->
                    signUpButton.isEnabled = value
                }
            }
            signUpButton.setOnClickListener {
                hideKeyboard()
                this@SignUpFragment.lifecycleScope.launchWhenStarted {
                    viewModel.signUpButtonClicked(
                        nameEditText.text.toString(),
                        surnameEditText.text.toString(),
                        usernameEditText.text.toString(),
                        emailEditText.text.toString(),
                        passwordEditText.text.toString()
                    )
                }
            }

            signUpStateEventsHandler(this)

            fieldsStateEventsHandler(this)
        }
    }

    private fun componentsListenerAssigner(binding: FragmentSignUpBinding) {
        binding.apply {
            nameEditText.addTextChangedListener {
                this@SignUpFragment.lifecycleScope.launchWhenStarted {
                    viewModel.validateNameField(it.toString())
                }
            }
            surnameEditText.addTextChangedListener {
                this@SignUpFragment.lifecycleScope.launchWhenStarted {
                    viewModel.validateSurnameField(it.toString())
                }
            }
            usernameEditText.addTextChangedListener(DelayedTextWatcher {
                this@SignUpFragment.lifecycleScope.launchWhenStarted {
                    viewModel.validateUsernameField(it.toString())
                }
            })
            emailEditText.addTextChangedListener(DelayedTextWatcher {
                this@SignUpFragment.lifecycleScope.launchWhenStarted {
                    viewModel.validateEmailField(it.toString())
                }
            })
            passwordEditText.addTextChangedListener {
                this@SignUpFragment.lifecycleScope.launchWhenStarted {
                    viewModel.validatePasswordField(it.toString())
                }
            }
            confirmPasswordEditText.addTextChangedListener {
                this@SignUpFragment.lifecycleScope.launchWhenStarted {
                    viewModel.validateConfirmPasswordField(
                        it.toString(), passwordEditText.text.toString()
                    )
                }
            }
            termsAndConditionsCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    this@SignUpFragment.lifecycleScope.launchWhenStarted {
                        viewModel.validateTermsConditionsField(
                            SignUpViewModel.TermsConditionsFieldState.VALID
                        )
                    }
                } else {
                    this@SignUpFragment.lifecycleScope.launchWhenStarted {
                        viewModel.validateTermsConditionsField(
                            SignUpViewModel.TermsConditionsFieldState.INVALID
                        )
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
                this@SignUpFragment.lifecycleScope.launchWhenStarted {
                    binding.termsAndConditionsCheckbox.isChecked = false
                    viewModel.validateTermsConditionsField(SignUpViewModel.TermsConditionsFieldState.INVALID)
                }
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.terms_and_conditions_dialog_accept_button)) { dialog, _ ->
                binding.termsAndConditionsCheckbox.isChecked = true
                this@SignUpFragment.lifecycleScope.launchWhenStarted {
                    viewModel.validateTermsConditionsField(SignUpViewModel.TermsConditionsFieldState.VALID)
                }
                dialog.dismiss()
            }.show()
    }

    private fun signUpStateEventsHandler(binding: FragmentSignUpBinding) {
        binding.apply {
            this@SignUpFragment.lifecycleScope.launchWhenStarted {
                viewModel.signUpEvents.collect { event ->
                    when (event) {
                        is SignUpViewModel.SignUpRequestEvent.SignUpRequestLoading -> makeGeneralProgressBarsVisible(
                            this@apply
                        )
                        is SignUpViewModel.SignUpRequestEvent.SignUpRequestSuccessful -> {
                            makeGeneralProgressBarsInvisible(this@apply)
                            if (event.response.success) (activity as BaseActivity).loggedIn()
                            else showSnackbar(resources.getString(R.string.uncategorized_error) + " LOG: ${event.response.message}")
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

    // region Field State
    private fun fieldsStateEventsHandler(binding: FragmentSignUpBinding) {
        binding.apply {
            this@SignUpFragment.lifecycleScope.launchWhenStarted {
                viewModel.nameFieldStateEvents.collect { event ->
                    when (event) {
                        SignUpViewModel.NameFieldStatus.INITIAL -> {
                            fieldStateChanger(binding, binding.nameInputLayout, FieldStyle.RESET)
                        }
                        SignUpViewModel.NameFieldStatus.VALID -> {
                            fieldStateChanger(binding, binding.nameInputLayout, FieldStyle.SUCCESS)
                        }
                    }.exhaustive
                }
            }

            this@SignUpFragment.lifecycleScope.launchWhenStarted {
                viewModel.surnameFieldStateEvents.collect { event ->
                    when (event) {
                        SignUpViewModel.SurnameFieldState.INITIAL -> {
                            fieldStateChanger(binding, binding.surnameInputLayout, FieldStyle.RESET)
                        }
                        SignUpViewModel.SurnameFieldState.VALID -> {
                            fieldStateChanger(
                                binding, binding.surnameInputLayout, FieldStyle.SUCCESS
                            )
                        }
                    }.exhaustive
                }
            }

            this@SignUpFragment.lifecycleScope.launchWhenStarted {
                viewModel.usernameFieldStateEvents.collect { event ->
                    usernameCheckPB.visibility = View.GONE
                    when (event) {
                        SignUpViewModel.UsernameFieldState.INITIAL -> {
                            fieldStateChanger(
                                binding, binding.usernameInputLayout, FieldStyle.RESET
                            )
                        }
                        SignUpViewModel.UsernameFieldState.LOADING_NETWORK_REQUEST -> {
                            fieldStateChanger(
                                binding, binding.usernameInputLayout, FieldStyle.RESET
                            )
                            usernameCheckPB.visibility = View.VISIBLE
                        }
                        SignUpViewModel.UsernameFieldState.FAILED_NETWORK_REQUEST -> {
                            fieldStateChanger(
                                binding,
                                binding.usernameInputLayout,
                                FieldStyle.ERROR,
                                getString(R.string.uncategorized_error)
                            )
                            showSnackbar(resources.getString(R.string.uncategorized_error) + " CAUSE: Check network!")
                        }
                        SignUpViewModel.UsernameFieldState.INVALID_ALREADY_IN_USE -> {
                            fieldStateChanger(
                                binding,
                                binding.usernameInputLayout,
                                FieldStyle.ERROR,
                                getString(R.string.signUp_error_username_taken)
                            )
                        }
                        SignUpViewModel.UsernameFieldState.VALID -> {
                            fieldStateChanger(
                                binding, binding.usernameInputLayout, FieldStyle.SUCCESS
                            )
                        }
                    }.exhaustive
                }
            }

            this@SignUpFragment.lifecycleScope.launchWhenStarted {
                viewModel.emailFieldStateEvents.collect { event ->
                    emailCheckPB.visibility = View.GONE
                    when (event) {
                        SignUpViewModel.EmailFieldState.INITIAL -> {
                            fieldStateChanger(binding, binding.emailInputLayout, FieldStyle.RESET)
                        }
                        SignUpViewModel.EmailFieldState.LOADING_NETWORK_REQUEST -> {
                            fieldStateChanger(binding, binding.emailInputLayout, FieldStyle.RESET)
                            emailCheckPB.visibility = View.VISIBLE
                        }
                        SignUpViewModel.EmailFieldState.FAILED_NETWORK_REQUEST -> {
                            fieldStateChanger(
                                binding,
                                binding.emailInputLayout,
                                FieldStyle.ERROR,
                                getString(R.string.uncategorized_error)
                            )
                            showSnackbar(resources.getString(R.string.uncategorized_error) + "CAUSE: Check network!")
                        }
                        SignUpViewModel.EmailFieldState.INVALID_FORMAT -> {
                            fieldStateChanger(
                                binding,
                                binding.emailInputLayout,
                                FieldStyle.ERROR,
                                getString(R.string.signUp_error_email_format)
                            )
                        }
                        SignUpViewModel.EmailFieldState.INVALID_ALREADY_IN_USE -> {
                            fieldStateChanger(
                                binding,
                                binding.emailInputLayout,
                                FieldStyle.ERROR,
                                getString(R.string.signUp_error_email_taken)
                            )
                        }
                        SignUpViewModel.EmailFieldState.VALID -> {
                            fieldStateChanger(binding, binding.emailInputLayout, FieldStyle.SUCCESS)
                        }
                    }.exhaustive
                }
            }

            this@SignUpFragment.lifecycleScope.launchWhenStarted {
                viewModel.passwordFieldStateEvents.collect { event ->
                    when (event) {
                        SignUpViewModel.PasswordFieldState.INITIAL -> {
                            fieldStateChanger(
                                binding, binding.passwordInputLayout, FieldStyle.RESET
                            )
                        }
                        SignUpViewModel.PasswordFieldState.INVALID_TOO_SHORT -> {
                            fieldStateChanger(
                                binding,
                                binding.passwordInputLayout,
                                FieldStyle.ERROR,
                                getString(R.string.signUp_error_short_password)
                            )
                        }
                        SignUpViewModel.PasswordFieldState.VALID -> {
                            fieldStateChanger(
                                binding, binding.passwordInputLayout, FieldStyle.SUCCESS
                            )
                        }
                    }.exhaustive
                }
            }

            this@SignUpFragment.lifecycleScope.launchWhenStarted {
                viewModel.confirmPasswordFieldStateEvents.collect { event ->
                    when (event) {
                        SignUpViewModel.ConfirmPasswordFieldState.INITIAL -> {
                            fieldStateChanger(
                                binding, binding.confirmPasswordInputLayout, FieldStyle.RESET
                            )
                        }
                        SignUpViewModel.ConfirmPasswordFieldState.INVALID_PASSWORDS_DO_NOT_MATCH -> {
                            fieldStateChanger(
                                binding,
                                binding.confirmPasswordInputLayout,
                                FieldStyle.ERROR,
                                getString(R.string.signUp_error_passwords_do_not_match)
                            )
                        }
                        SignUpViewModel.ConfirmPasswordFieldState.VALID -> {
                            fieldStateChanger(
                                binding, binding.confirmPasswordInputLayout, FieldStyle.SUCCESS
                            )
                        }
                    }.exhaustive
                }
            }

            this@SignUpFragment.lifecycleScope.launchWhenStarted {
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

    enum class FieldStyle {
        RESET, ERROR, SUCCESS
    }

    private fun fieldStateChanger(
        binding: FragmentSignUpBinding,
        textInputLayout: TextInputLayout,
        fieldStyle: FieldStyle,
        errorMessage: String = "Placeholder"
    ) {
        when (fieldStyle) {
            FieldStyle.RESET -> {
                textInputLayout.error = null
                textInputLayout.isErrorEnabled = false
                changeFieldStyle(textInputLayout, fieldStyle)
            }
            FieldStyle.ERROR -> {
                textInputLayout.error = errorMessage
                textInputLayout.isErrorEnabled = true
                changeFieldStyle(textInputLayout, fieldStyle)
            }
            FieldStyle.SUCCESS -> {
                textInputLayout.error = null
                textInputLayout.isErrorEnabled = false
                changeFieldStyle(textInputLayout, fieldStyle)
            }
        }.exhaustive
        binding.gridLayout.requestLayout()
    }

    private fun changeFieldStyle(textInputLayout: TextInputLayout, fieldStyle: FieldStyle) {
        when (fieldStyle) {
            FieldStyle.RESET, FieldStyle.ERROR -> {
                textInputLayout.hintTextColor = MaterialColors.getColorStateList(
                    requireContext(),
                    R.attr.GT_colorAccent,
                    ColorStateList.valueOf(R.attr.GT_colorAccent)
                )
                textInputLayout.defaultHintTextColor = ContextCompat.getColorStateList(
                    requireContext(), R.color.text_field_text_color_hint
                )!!
                textInputLayout.setBoxStrokeColorStateList(
                    ContextCompat.getColorStateList(
                        requireContext(), R.color.text_field_box_stroke_color
                    )!!
                )
                textInputLayout.boxStrokeErrorColor = MaterialColors.getColorStateList(
                    requireContext(),
                    R.attr.GT_colorError,
                    ColorStateList.valueOf(R.attr.GT_colorError)
                )
                textInputLayout.setErrorIconTintList(
                    MaterialColors.getColorStateList(
                        requireContext(),
                        R.attr.GT_colorError,
                        ColorStateList.valueOf(R.attr.GT_colorError)
                    )
                )
                textInputLayout.setErrorTextColor(
                    MaterialColors.getColorStateList(
                        requireContext(),
                        R.attr.GT_colorError,
                        ColorStateList.valueOf(R.attr.GT_colorError)
                    )
                )
                textInputLayout.counterOverflowTextColor = MaterialColors.getColorStateList(
                    requireContext(),
                    R.attr.GT_colorError,
                    ColorStateList.valueOf(R.attr.GT_colorError)
                )
            }
            FieldStyle.SUCCESS -> {
                textInputLayout.hintTextColor = MaterialColors.getColorStateList(
                    requireContext(),
                    R.attr.GT_colorSuccess,
                    ColorStateList.valueOf(R.attr.GT_colorSuccess)
                )
                textInputLayout.defaultHintTextColor = ContextCompat.getColorStateList(
                    requireContext(), R.color.text_field_success_text_color_hint
                )!!
                textInputLayout.setBoxStrokeColorStateList(
                    ContextCompat.getColorStateList(
                        requireContext(), R.color.text_field_success_box_stroke_color
                    )!!
                )
                textInputLayout.boxStrokeErrorColor = MaterialColors.getColorStateList(
                    requireContext(),
                    R.attr.GT_colorSuccess,
                    ColorStateList.valueOf(R.attr.GT_colorSuccess)
                )
                textInputLayout.setErrorIconTintList(
                    MaterialColors.getColorStateList(
                        requireContext(),
                        R.attr.GT_colorSuccess,
                        ColorStateList.valueOf(R.attr.GT_colorSuccess)
                    )
                )
                textInputLayout.setErrorTextColor(
                    MaterialColors.getColorStateList(
                        requireContext(),
                        R.attr.GT_colorSuccess,
                        ColorStateList.valueOf(R.attr.GT_colorSuccess)
                    )
                )
                textInputLayout.counterOverflowTextColor = MaterialColors.getColorStateList(
                    requireContext(),
                    R.attr.GT_colorSuccess,
                    ColorStateList.valueOf(R.attr.GT_colorSuccess)
                )
            }
        }.exhaustive
    }
    // endregion

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
}
