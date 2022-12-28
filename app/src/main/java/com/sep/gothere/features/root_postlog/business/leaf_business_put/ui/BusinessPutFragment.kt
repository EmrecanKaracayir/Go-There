package com.sep.gothere.features.root_postlog.business.leaf_business_put.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sep.gothere.R
import com.sep.gothere.api.model.venue.get.request.GetVenueRequest
import com.sep.gothere.base.BaseActivity
import com.sep.gothere.base.BaseFragment
import com.sep.gothere.component.dynamic.impl.input.text.GTTextInputLayout
import com.sep.gothere.component.dynamic.schema.AccentStylableView
import com.sep.gothere.data.NetworkVenueRepository
import com.sep.gothere.databinding.FragmentBusinessPutBinding
import com.sep.gothere.features.root_postlog.business.leaf_business_put.vm.BusinessPutViewModel
import com.sep.gothere.helpers.color.FeatureStylizer
import com.sep.gothere.helpers.color.HSL
import com.sep.gothere.navigation.NavigationInfoProvider
import com.sep.gothere.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class BusinessPutFragment : BaseFragment(R.layout.fragment_business_put), NavigationInfoProvider {

    @Inject
    lateinit var networkVenueRepository: NetworkVenueRepository

    private val viewModel: BusinessPutViewModel by viewModels()

    // THIS PAGE HAS RANDOMLY SELECTED STATIC ACCENT COLOR
    private val accentHSL = HSL(
        Color.rgb(
            (Math.random() * 200).toInt(),
            (Math.random() * 200).toInt(),
            (Math.random() * 255).toInt()
        )
    )

    private var venueID: Long? = null

    private var _binding: FragmentBusinessPutBinding? = null
    private val binding get() = _binding!!

    enum class FragmentMode {
        REGISTER, PUT
    }

    private lateinit var mode: FragmentMode

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBusinessPutBinding.inflate(inflater, container, false)
        val bundle = arguments
        if (bundle != null) {
            venueID = bundle.getLong(BUNDLE_ID_KEY)
            mode = FragmentMode.PUT
        } else {
            mode = FragmentMode.REGISTER
        }
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBusinessPutBinding.bind(view).apply {
            (activity as BaseActivity).hideNavbar()
            val fs = FeatureStylizer(requireContext(), accentHSL)

            if (venueID != null && !viewModel.isVenueFetched) {
                binding.venueUsernameInputLayout.isEnabled = false
                lifecycleScope.launch {
                    viewModel.getVenueVM(
                        GetVenueRequest(
                            venueId = venueID
                        )
                    )
                }
            }

            if (viewModel.profileImageBase64 != null) {
                Glide.with(requireContext())
                    .asBitmap()
                    .load(viewModel.profileImageBase64)
                    .placeholder(R.drawable.photo_placeholder)
                    .into(binding.venueProfileImage)
            }

            if (viewModel.coverImageBase64 != null) {
                Glide.with(requireContext())
                    .asBitmap()
                    .load(viewModel.coverImageBase64)
                    .placeholder(R.drawable.photo_placeholder)
                    .into(binding.venueCoverImage)
            }

            bGetStateEventsHandler(this)

            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    fs.findStylableComponentsAndApplyStyle(
                        root
                    )
                }
            }

            CustomFitsSystemUI().fitsSystemUI(
                bPutFragmentHeroLayoutFITSTOP,
                arrayListOf(CustomFitsSystemUI.RequestedInset.TOP)
            )

            CustomFitsSystemUI().fitsSystemUI(
                bPutFragmentContentLayoutFITSBOTTOM,
                arrayListOf(CustomFitsSystemUI.RequestedInset.BOTTOM)
            )

            fieldsListenerAssigner()

            backButton.setOnClickListener {
                hideKeyboard()
                (activity as BaseActivity).navigateBackRequested()
            }

            this@BusinessPutFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.isCompleteButtonEnabled.collect { value ->
                        completeButton.isEnabled = value
                    }
                }
            }

            completeButton.setOnClickListener {
                hideKeyboard()
                this@BusinessPutFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        when (mode) {
                            FragmentMode.REGISTER -> {
                                viewModel.bPutButtonClickedRegister(
                                    venueID = 0,
                                    binding.nameEditText.text.toString(),
                                    binding.venueUsernameEditText.text.toString(),
                                    binding.descEditText.text.toString(),
                                    binding.bioEditText.text.toString(),
                                    binding.emailEditText.text.toString(),
                                    binding.phoneEditText.text.toString(),
                                    binding.addressEditText.text.toString(),
                                    viewModel.profileImageBase64!!,
                                    viewModel.coverImageBase64!!
                                )
                            }
                            FragmentMode.PUT -> {
                                viewModel.bPutButtonClickedUpdate(
                                    venueID!!,
                                    binding.nameEditText.text.toString(),
                                    binding.venueUsernameEditText.text.toString(),
                                    binding.descEditText.text.toString(),
                                    binding.bioEditText.text.toString(),
                                    binding.emailEditText.text.toString(),
                                    binding.phoneEditText.text.toString(),
                                    binding.addressEditText.text.toString(),
                                    viewModel.profileImageBase64!!,
                                    viewModel.coverImageBase64!!
                                )
                            }
                        }.exhaustive
                    }
                }
            }

            bPutStateEventsHandler(this)

            fieldsStateEventsHandler(this)
        }
    }

    private fun fieldsListenerAssigner() {
        binding.apply {
            nameEditText.addTextChangedListener {
                this@BusinessPutFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validateNameField(it.toString())
                    }
                }
            }
            venueUsernameEditText.addTextChangedListener(DelayedTextWatcher {
                this@BusinessPutFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validateVenueUsernameField(it.toString())
                    }
                }
            })
            descEditText.addTextChangedListener {
                this@BusinessPutFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validateDescField(it.toString())
                    }
                }
            }
            bioEditText.addTextChangedListener {
                this@BusinessPutFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validateBioField(it.toString())
                    }
                }
            }

            emailEditText.addTextChangedListener {
                this@BusinessPutFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validateEmailField(it.toString())
                    }
                }
            }
            phoneEditText.addTextChangedListener {
                this@BusinessPutFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validatePhoneField(it.toString())
                    }
                }
            }
            addressEditText.addTextChangedListener {
                this@BusinessPutFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validateAddressField(it.toString())
                    }
                }
            }

            profileImageUploadButton.setOnClickListener {
                val pickIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultProfileImageLauncher.launch(pickIntent)
            }
            coverImageUploadButton.setOnClickListener {
                val pickIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultCoverImageLauncher.launch(pickIntent)
            }

            termsAndConditionsCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    this@BusinessPutFragment.lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.validateTermsConditionsField(
                                BusinessPutViewModel.TermsConditionsFieldState.VALID
                            )
                        }
                    }
                } else {
                    this@BusinessPutFragment.lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.validateTermsConditionsField(
                                BusinessPutViewModel.TermsConditionsFieldState.INVALID
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

    private var resultProfileImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val pickedImageURI = result.data!!.data
                setProfileImageFromURI(pickedImageURI!!)
            } else {
                this@BusinessPutFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validateProfileImageField(
                            BusinessPutViewModel.ProfileImageFieldState.INVALID
                        )
                    }
                }
            }
        }

    private fun setProfileImageFromURI(uri: Uri) {
        Glide.with(requireContext()).asBitmap().load(uri).override(1080).centerCrop()
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap, transition: Transition<in Bitmap>?
                ) {
                    binding.venueProfileImage.setImageBitmap(resource)

                    val byteArrayOutputStream = ByteArrayOutputStream()
                    resource.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream)
                    val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                    val encoded: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                    viewModel.profileImageBase64 = encoded

                    this@BusinessPutFragment.lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.validateProfileImageField(
                                BusinessPutViewModel.ProfileImageFieldState.VALID
                            )
                        }
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private var resultCoverImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val pickedImageURI = result.data!!.data
                setCoverImageFromURI(pickedImageURI!!)
            } else {
                this@BusinessPutFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validateCoverImageField(
                            BusinessPutViewModel.CoverImageFieldState.INVALID
                        )
                    }
                }
            }
        }

    private fun setCoverImageFromURI(uri: Uri) {
        Glide.with(requireContext()).asBitmap().load(uri).override(1080).centerCrop()
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap, transition: Transition<in Bitmap>?
                ) {
                    binding.venueCoverImage.setImageBitmap(resource)

                    val byteArrayOutputStream = ByteArrayOutputStream()
                    resource.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream)
                    val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                    val encoded: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                    viewModel.coverImageBase64 = encoded

                    this@BusinessPutFragment.lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.validateCoverImageField(
                                BusinessPutViewModel.CoverImageFieldState.VALID
                            )
                        }
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun openTermsConditionsDialog(binding: FragmentBusinessPutBinding) {
        MaterialAlertDialogBuilder(
            requireContext(), R.style.GT_ThemeOverlay_App_MaterialAlertDialog
        ).setIcon(
            ResourcesCompat.getDrawable(
                resources, R.drawable.terms_and_conditions, requireContext().theme
            )
        ).setTitle(resources.getString(R.string.terms_and_conditions_dialog_title))
            .setMessage(resources.getString(R.string.terms_and_conditions_full))
            .setNegativeButton(resources.getString(R.string.terms_and_conditions_dialog_decline_button)) { dialog, _ ->
                this@BusinessPutFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        binding.termsAndConditionsCheckbox.isChecked = false
                        viewModel.validateTermsConditionsField(BusinessPutViewModel.TermsConditionsFieldState.INVALID)
                    }
                }
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.terms_and_conditions_dialog_accept_button)) { dialog, _ ->
                binding.termsAndConditionsCheckbox.isChecked = true
                this@BusinessPutFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.validateTermsConditionsField(BusinessPutViewModel.TermsConditionsFieldState.VALID)
                    }
                }
                dialog.dismiss()
            }.show()
    }

    private fun bPutStateEventsHandler(binding: FragmentBusinessPutBinding) {
        binding.apply {
            this@BusinessPutFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.venuePutEvents.collect { event ->
                        when (event) {
                            is BusinessPutViewModel.VenuePutRequestEvent.VenuePutRequestLoading -> makeGeneralProgressBarsVisible(
                                this@apply
                            )
                            is BusinessPutViewModel.VenuePutRequestEvent.VenuePutRequestSuccessful -> {
                                makeGeneralProgressBarsInvisible(this@apply)
                                if (event.response.success) {
                                    hideKeyboard()
                                    (activity as BaseActivity).navigateBackRequested()
                                } else showSnackbar(resources.getString(R.string.uncategorized_error) + " LOG: ${event.response.message}")
                            }
                            is BusinessPutViewModel.VenuePutRequestEvent.VenuePutRequestError -> {
                                makeGeneralProgressBarsInvisible(this@apply)
                                showSnackbar(resources.getString(R.string.uncategorized_error) + " LOG: ${event.error.message}")
                            }
                        }.exhaustive
                    }
                }
            }
        }
    }

    private fun bGetStateEventsHandler(binding: FragmentBusinessPutBinding) {
        binding.apply {
            this@BusinessPutFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.venueGetEvents.collect { event ->
                        when (event) {
                            is BusinessPutViewModel.VenueGetRequestEvent.VenueGetRequestLoading -> makeGeneralProgressBarsVisible(
                                this@apply
                            )
                            is BusinessPutViewModel.VenueGetRequestEvent.VenueGetRequestSuccessful -> {
                                makeGeneralProgressBarsInvisible(this@apply)
                                if (event.response.success) {
                                    if (event.response.data != null) {
                                        nameEditText.setText(event.response.data[0].name)
                                        venueUsernameEditText.setText(event.response.data[0].username)
                                        descEditText.setText(event.response.data[0].shortDescription)
                                        bioEditText.setText(event.response.data[0].biography)
                                        emailEditText.setText(event.response.data[0].mail)
                                        phoneEditText.setText(event.response.data[0].phone)
                                        addressEditText.setText(event.response.data[0].address)
                                        setProfileImageFromApi(event.response.data[0].id)
                                        setCoverImageFromApi(event.response.data[0].id)
                                    } else throw IllegalArgumentException()
                                } else showSnackbar(resources.getString(R.string.uncategorized_error) + " LOG: ${event.response.message}")
                            }
                            is BusinessPutViewModel.VenueGetRequestEvent.VenueGetRequestError -> {
                                makeGeneralProgressBarsInvisible(this@apply)
                                showSnackbar(resources.getString(R.string.uncategorized_error) + " LOG: ${event.error.message}")
                            }
                        }.exhaustive
                    }
                }
            }
        }
    }

    private suspend fun setProfileImageFromApi(venueID: Long) {
        withContext(Dispatchers.Main) {
            val profileImageByteArray: ByteArray = Base64.decode(
                networkVenueRepository.getVenueProfileImageRP(venueID).data,
                Base64.DEFAULT
            )

            Glide.with(requireContext())
                .asBitmap()
                .load(profileImageByteArray)
                .placeholder(R.drawable.photo_placeholder)
                .into(binding.venueProfileImage)
        }
    }

    private suspend fun setCoverImageFromApi(venueID: Long) {
        withContext(Dispatchers.Main) {
            val profileImageByteArray: ByteArray = Base64.decode(
                networkVenueRepository.getVenueCoverImageRP(venueID).data,
                Base64.DEFAULT
            )

            Glide.with(requireContext())
                .asBitmap()
                .load(profileImageByteArray)
                .placeholder(R.drawable.photo_placeholder)
                .into(binding.venueCoverImage)
        }
    }

    //region Field State
    private fun fieldsStateEventsHandler(binding: FragmentBusinessPutBinding) {
        binding.apply {
            this@BusinessPutFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.nameFieldStateEvents.collect { event ->
                        when (event) {
                            BusinessPutViewModel.NameFieldState.INITIAL -> {
                                fieldStateChanger(
                                    binding,
                                    binding.nameInputLayout,
                                    AccentStylableView.StateStyle.DEFAULT_STYLE_STATE
                                )
                            }
                            BusinessPutViewModel.NameFieldState.VALID -> {
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

            this@BusinessPutFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.venueUsernameFieldStateEvents.collect { event ->
                        venueUsernameCheckPB.visibility = View.GONE
                        when (event) {
                            BusinessPutViewModel.VenueUsernameFieldState.INITIAL -> {
                                fieldStateChanger(
                                    binding,
                                    binding.venueUsernameInputLayout,
                                    AccentStylableView.StateStyle.DEFAULT_STYLE_STATE
                                )
                            }
                            BusinessPutViewModel.VenueUsernameFieldState.LOADING_NETWORK_REQUEST -> {
                                fieldStateChanger(
                                    binding,
                                    binding.venueUsernameInputLayout,
                                    AccentStylableView.StateStyle.DEFAULT_STYLE_STATE
                                )
                                venueUsernameCheckPB.visibility = View.VISIBLE
                            }
                            BusinessPutViewModel.VenueUsernameFieldState.FAILED_NETWORK_REQUEST -> {
                                fieldStateChanger(
                                    binding,
                                    binding.venueUsernameInputLayout,
                                    AccentStylableView.StateStyle.ERROR_STYLE_STATE,
                                    getString(R.string.uncategorized_error)
                                )
                                showSnackbar(resources.getString(R.string.uncategorized_error) + " CAUSE: Check network!")
                            }
                            BusinessPutViewModel.VenueUsernameFieldState.INVALID_ALREADY_IN_USE -> {
                                fieldStateChanger(
                                    binding,
                                    binding.venueUsernameInputLayout,
                                    AccentStylableView.StateStyle.ERROR_STYLE_STATE,
                                    getString(R.string.signUp_error_username_taken)
                                )
                            }
                            BusinessPutViewModel.VenueUsernameFieldState.VALID -> {
                                fieldStateChanger(
                                    binding,
                                    binding.venueUsernameInputLayout,
                                    AccentStylableView.StateStyle.SUCCESS_STYLE_STATE
                                )
                            }
                        }.exhaustive
                    }
                }
            }

            this@BusinessPutFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.descFieldStateEvents.collect { event ->
                        when (event) {
                            BusinessPutViewModel.DescFieldState.INITIAL -> {
                                fieldStateChanger(
                                    binding,
                                    binding.descInputLayout,
                                    AccentStylableView.StateStyle.DEFAULT_STYLE_STATE
                                )
                            }
                            BusinessPutViewModel.DescFieldState.VALID -> {
                                fieldStateChanger(
                                    binding,
                                    binding.descInputLayout,
                                    AccentStylableView.StateStyle.SUCCESS_STYLE_STATE
                                )
                            }
                        }.exhaustive
                    }
                }
            }

            this@BusinessPutFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.bioFieldStateEvents.collect { event ->
                        when (event) {
                            BusinessPutViewModel.BioFieldState.INITIAL -> {
                                fieldStateChanger(
                                    binding,
                                    binding.bioInputLayout,
                                    AccentStylableView.StateStyle.DEFAULT_STYLE_STATE
                                )
                            }
                            BusinessPutViewModel.BioFieldState.VALID -> {
                                fieldStateChanger(
                                    binding,
                                    binding.bioInputLayout,
                                    AccentStylableView.StateStyle.SUCCESS_STYLE_STATE
                                )
                            }
                        }.exhaustive
                    }
                }
            }

            this@BusinessPutFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.emailFieldStateEvents.collect { event ->
                        when (event) {
                            BusinessPutViewModel.EmailFieldState.INITIAL -> {
                                fieldStateChanger(
                                    binding,
                                    binding.emailInputLayout,
                                    AccentStylableView.StateStyle.DEFAULT_STYLE_STATE
                                )
                            }
                            BusinessPutViewModel.EmailFieldState.INVALID_FORMAT -> {
                                fieldStateChanger(
                                    binding,
                                    binding.emailInputLayout,
                                    AccentStylableView.StateStyle.ERROR_STYLE_STATE,
                                    getString(R.string.error_email_format)
                                )
                            }
                            BusinessPutViewModel.EmailFieldState.VALID -> {
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

            this@BusinessPutFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.phoneFieldStateEvents.collect { event ->
                        when (event) {
                            BusinessPutViewModel.PhoneFieldState.INITIAL -> {
                                fieldStateChanger(
                                    binding,
                                    binding.phoneInputLayout,
                                    AccentStylableView.StateStyle.DEFAULT_STYLE_STATE
                                )
                            }
                            BusinessPutViewModel.PhoneFieldState.VALID -> {
                                fieldStateChanger(
                                    binding,
                                    binding.phoneInputLayout,
                                    AccentStylableView.StateStyle.SUCCESS_STYLE_STATE
                                )
                            }
                        }.exhaustive
                    }
                }
            }

            this@BusinessPutFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.addressFieldStateEvents.collect { event ->
                        when (event) {
                            BusinessPutViewModel.AddressFieldState.INITIAL -> {
                                fieldStateChanger(
                                    binding,
                                    binding.addressInputLayout,
                                    AccentStylableView.StateStyle.DEFAULT_STYLE_STATE
                                )
                            }
                            BusinessPutViewModel.AddressFieldState.VALID -> {
                                fieldStateChanger(
                                    binding,
                                    binding.addressInputLayout,
                                    AccentStylableView.StateStyle.SUCCESS_STYLE_STATE
                                )
                            }
                        }.exhaustive
                    }
                }
            }

            this@BusinessPutFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.profileImageFieldStateEvents.collect { event ->
                        when (event) {
                            BusinessPutViewModel.ProfileImageFieldState.INITIAL -> {
                                binding.venueProfileImageText.setTextColor(
                                    MaterialColors.getColor(
                                        binding.venueProfileImageText,
                                        R.attr.GT_basicHcPrimary
                                    )
                                )
                            }
                            BusinessPutViewModel.ProfileImageFieldState.INVALID -> {
                                binding.venueProfileImageText.setTextColor(
                                    MaterialColors.getColor(
                                        binding.venueProfileImageText,
                                        R.attr.GT_paletteRed
                                    )
                                )
                            }
                            BusinessPutViewModel.ProfileImageFieldState.VALID -> {
                                binding.venueProfileImageText.setTextColor(
                                    MaterialColors.getColor(
                                        binding.venueProfileImageText,
                                        R.attr.GT_paletteGreen
                                    )
                                )
                            }
                        }.exhaustive
                    }
                }
            }

            this@BusinessPutFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.coverImageFieldStateEvents.collect { event ->
                        when (event) {
                            BusinessPutViewModel.CoverImageFieldState.INITIAL -> {
                                binding.venueCoverImageText.setTextColor(
                                    MaterialColors.getColor(
                                        binding.venueCoverImageText,
                                        R.attr.GT_basicHcPrimary
                                    )
                                )
                            }
                            BusinessPutViewModel.CoverImageFieldState.INVALID -> {
                                binding.venueCoverImageText.setTextColor(
                                    MaterialColors.getColor(
                                        binding.venueCoverImageText,
                                        R.attr.GT_paletteRed
                                    )
                                )
                            }
                            BusinessPutViewModel.CoverImageFieldState.VALID -> {
                                binding.venueCoverImageText.setTextColor(
                                    MaterialColors.getColor(
                                        binding.venueCoverImageText,
                                        R.attr.GT_paletteGreen
                                    )
                                )
                            }
                        }.exhaustive
                    }
                }
            }

            this@BusinessPutFragment.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.termsConditionsFieldStateEvents.collect { event ->
                        when (event) {
                            BusinessPutViewModel.TermsConditionsFieldState.INITIAL -> {
                                termsAndConditionsCheckbox.isErrorShown = false
                            }
                            BusinessPutViewModel.TermsConditionsFieldState.INVALID -> {
                                termsAndConditionsCheckbox.isErrorShown = true
                            }
                            BusinessPutViewModel.TermsConditionsFieldState.VALID -> {
                                termsAndConditionsCheckbox.isErrorShown = false
                            }
                        }.exhaustive
                    }
                }
            }
        }
    }

    private fun fieldStateChanger(
        binding: FragmentBusinessPutBinding,
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

    private fun makeGeneralProgressBarsVisible(binding: FragmentBusinessPutBinding) {
        binding.apply {
            generalInformationPB.visibility = View.VISIBLE
            communicationInformationPB.visibility = View.VISIBLE
            mediaInformationPB.visibility = View.VISIBLE
        }
    }

    private fun makeGeneralProgressBarsInvisible(binding: FragmentBusinessPutBinding) {
        binding.apply {
            generalInformationPB.visibility = View.INVISIBLE
            communicationInformationPB.visibility = View.INVISIBLE
            mediaInformationPB.visibility = View.INVISIBLE
        }
    }

    private fun hideKeyboard() {
        (requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            requireView().windowToken, 0
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun getNavigationRoot() = NavigationInfoProvider.NavigationRoot.ROOT_POSTLOG
    override fun getNavigationBranch() = NavigationInfoProvider.NavigationBranch.BRANCH_BUSINESS
    override fun getNavigationTag() = NavigationInfoProvider.NavigationTag.TAG_BUSINESS_PUT_FRAGMENT
    override fun isNavigationBranchOwner() = false
    override fun cacheOnBackPressed() = false
}
