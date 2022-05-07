package com.agapovp.bignerdranch.android.criminalintent

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.agapovp.bignerdranch.android.criminalintent.utils.getScaledBitmap
import com.agapovp.bignerdranch.android.criminalintent.utils.setSafeOnClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CrimeFragment : Fragment() {

    private val chooseSuspectLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when {
                result.resultCode != Activity.RESULT_OK -> return@registerForActivityResult
                result.data != null -> {
                    val contactUri: Uri = result.data?.data ?: return@registerForActivityResult
                    val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                    val cursor = requireActivity().contentResolver.query(
                        contactUri,
                        queryFields,
                        null,
                        null,
                        null
                    )
                    cursor?.use {
                        if (!it.moveToFirst()) return@registerForActivityResult

                        crime.suspect = it.getString(0)
                        viewModel.saveCrime(crime)
                        buttonChooseSuspect.text = crime.suspect
                    }
                }
            }
        }

    private val callSuspectLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when {
                result.resultCode != Activity.RESULT_OK -> return@registerForActivityResult
                result.data != null -> {
                    val contactUri: Uri = result.data?.data ?: return@registerForActivityResult
                    val contactsCursor = requireActivity().contentResolver.query(
                        contactUri,
                        null,
                        null,
                        null,
                        null
                    )
                    contactsCursor?.use { contacts ->
                        if (!contacts.moveToFirst()) return@registerForActivityResult

                        val id = contactsCursor.getString(
                            contactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
                        )
                        val hasPhone = contactsCursor.getString(
                            contactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                        )

                        if (!hasPhone.equals("1", true)) {
                            Toast.makeText(
                                context,
                                "There is no phone in the selected contact.",
                                Toast.LENGTH_LONG
                            ).show()
                            return@registerForActivityResult
                        }

                        val phonesCursor = requireActivity().contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null,
                            null
                        )
                        phonesCursor?.use { phones ->
                            if (!phones.moveToFirst()) return@registerForActivityResult

                            val contactNumber = phonesCursor.getString(
                                phonesCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )
                            Log.d(TAG, contactNumber)

                            val pickContactIntent =
                                Intent(Intent.ACTION_DIAL, Uri.parse("tel:$contactNumber"))
                            startActivity(pickContactIntent)
                        }
                    }
                }
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                val pickContactIntent =
                    Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

                callSuspectLauncher.launch(pickContactIntent)
            } else {
                Toast.makeText(context, "Read Contacts permission required", Toast.LENGTH_LONG)
                    .show()
            }
        }

    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                requireActivity().revokeUriPermission(
                    photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                if (photoFile.exists()) photoFile.delete()
                tempFile.renameTo(photoFile)
            } else {
                tempFile.delete()
            }
        }

    private lateinit var crime: Crime
    private lateinit var tempFile: File
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    private lateinit var imagePhoto: ImageView
    private lateinit var imageButtonCamera: ImageButton
    private lateinit var editTextTitle: EditText
    private lateinit var buttonDate: Button
    private lateinit var checkBoxSolved: CheckBox
    private lateinit var checkBoxRequiresPolice: CheckBox
    private lateinit var buttonChooseSuspect: Button
    private lateinit var buttonSendReport: Button
    private lateinit var buttonCallSuspect: Button

    private val viewModel: CrimeViewModel by lazy {
        ViewModelProvider(this).get(CrimeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        viewModel.loadCrime(arguments?.getSerializable(ARG_CRIME_ID) as UUID)
        childFragmentManager.setFragmentResultListener(REQUEST_DATE, this) { requestKey, result ->
            val date = result.getSerializable(requestKey) as Date
            crime.date = date
            updateUI()
            TimePickerFragment
                .newInstance(date, REQUEST_TIME)
                .show(childFragmentManager, TAG_DIALOG_TIME)
        }
        childFragmentManager.setFragmentResultListener(REQUEST_TIME, this) { requestKey, result ->
            crime.date = result.getSerializable(requestKey) as Date
            updateUI()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_crime, container, false).also { view ->
        imagePhoto = view.findViewById(R.id.fragment_crime_image_photo)
        imageButtonCamera = view.findViewById(R.id.fragment_crime_imagebutton_camera)
        editTextTitle = view.findViewById(R.id.fragment_crime_edittext_title)
        buttonDate = view.findViewById(R.id.fragment_crime_button_date)
        checkBoxSolved = view.findViewById(R.id.fragment_crime_checkbox_solved)
        checkBoxRequiresPolice = view.findViewById(R.id.fragment_crime_checkbox_requires_police)
        buttonChooseSuspect = view.findViewById(R.id.fragment_crime_button_choose_suspect)
        buttonSendReport = view.findViewById(R.id.fragment_crime_button_send_report)
        buttonCallSuspect = view.findViewById(R.id.fragment_crime_button_call_suspect)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.crime.observe(viewLifecycleOwner) { crime ->
            crime?.let {
                this.crime = crime
                photoFile = viewModel.getPhotoFile(crime)
                tempFile = File(requireContext().applicationContext.filesDir, TEMP_FILE_NAME)
                photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    getString(R.string.file_uri),
                    tempFile
                )
                updateUI()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        imagePhoto.run {
            setSafeOnClickListener {
                if (photoFile.exists()) {
                    CrimePhotoFragment
                        .newInstance(photoFile.path)
                        .show(childFragmentManager, TAG_DIALOG_PHOTO)
                }
            }
        }

        imageButtonCamera.run {
            val packageManager = requireActivity().packageManager

            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolveActivity =
                packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolveActivity == null) {
                isEnabled = false
            }

            setSafeOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                val cameraActivities = packageManager.queryIntentActivities(
                    captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY
                )

                cameraActivities.forEach { cameraActivity ->
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }

                photoPickerLauncher.launch(captureImage)
            }
        }

        editTextTitle.doOnTextChanged { text, _, _, _ ->
            crime.title = text.toString()
        }
        buttonDate.setSafeOnClickListener {
            DatePickerFragment
                .newInstance(crime.date, REQUEST_DATE)
                .show(childFragmentManager, TAG_DIALOG_DATE)
        }
        checkBoxSolved.setOnCheckedChangeListener { _, isChecked ->
            crime.isSolved = isChecked
        }
        checkBoxRequiresPolice.setOnCheckedChangeListener { _, isChecked ->
            crime.requiresPolice = isChecked
        }
        buttonChooseSuspect.run {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            requireActivity().packageManager.resolveActivity(
                pickContactIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            ) ?: run { isEnabled = false }

            setSafeOnClickListener {
                chooseSuspectLauncher.launch(pickContactIntent)
            }
        }
        buttonSendReport.setSafeOnClickListener {
            startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
                        putExtra(Intent.EXTRA_TEXT, getCrimeReportText())
                    },
                    getString(R.string.crime_report_send_via)
                )
            )
        }
        buttonCallSuspect.run {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            requireActivity().packageManager.resolveActivity(
                pickContactIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            ) ?: run { isEnabled = false }

            setSafeOnClickListener {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveCrime(crime)
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

    private fun updateUI() {
        editTextTitle.setText(crime.title)
        buttonDate.text = dateFormatter.format(crime.date)
        checkBoxSolved.run {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        checkBoxRequiresPolice.run {
            isChecked = crime.requiresPolice
            jumpDrawablesToCurrentState()
        }
        if (crime.suspect.isNotEmpty()) buttonChooseSuspect.text = crime.suspect
        updateImagePhoto()
    }

    private fun getCrimeReportText(): String {
        val dateString = dateFormatter.format(crime.date)
        val solvedString = getString(
            if (crime.isSolved) R.string.crime_report_solved
            else R.string.crime_report_unsolved
        )
        val suspectString =
            if (crime.suspect.isBlank()) {
                getString(R.string.crime_report_no_suspect)
            } else {
                getString(R.string.crime_report_suspect, crime.suspect)
            }
        val policeString = getString(
            if (crime.requiresPolice) R.string.crime_report_police_required
            else R.string.crime_report_police_not_required
        )

        return getString(
            R.string.crime_report_text,
            crime.title,
            dateString,
            solvedString,
            suspectString,
            policeString
        )
    }

    private fun updateImagePhoto() {
        if (photoFile.exists()) {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val bitmap = getScaledBitmap(photoFile.path, imagePhoto.width, imagePhoto.height)
                withContext(Dispatchers.Main) {
                    imagePhoto.setImageBitmap(bitmap)
                }
            }
        } else {
            imagePhoto.setImageDrawable(null)
        }
    }

    companion object {

        private const val TAG = "CrimeFragment"
        private const val ARG_CRIME_ID = "${TAG}_ARG_CRIME_ID"
        private const val TAG_DIALOG_PHOTO = "${TAG}_TAG_DIALOG_PHOTO"
        private const val TAG_DIALOG_DATE = "${TAG}_TAG_DIALOG_DATE"
        private const val TAG_DIALOG_TIME = "${TAG}_TAG_DIALOG_TIME"
        private const val REQUEST_DATE = "${TAG}_REQUEST_DATE"
        private const val REQUEST_TIME = "${TAG}_REQUEST_TIME"
        private const val TEMP_FILE_NAME = "temp_file"

        private val dateFormatter = SimpleDateFormat("HH:mm EEEE, MMM dd, yyyy", Locale.US)

        fun newInstance(crimeId: UUID): CrimeFragment =
            CrimeFragment().apply {
                arguments = bundleOf(
                    ARG_CRIME_ID to crimeId
                )
            }
    }
}
