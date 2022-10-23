package com.agapovp.bignerdranch.android.photogallery

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible

class PhotoPageFragment : VisibleFragment() {

    private lateinit var uri: Uri
    private lateinit var progressBar: ProgressBar
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        uri = arguments?.getParcelable(ARG_URI) ?: Uri.EMPTY

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_photo_page, container, false).apply {

        progressBar = findViewById<ProgressBar?>(R.id.fragment_photo_page_progressbar).apply {
            max = PROGRESS_BAR_MAX_RANGE
        }

        webView = findViewById<WebView?>(R.id.fragment_photo_page_webview).apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            webChromeClient = object : WebChromeClient() {

                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    if (newProgress == 100) {
                        progressBar.isVisible = false
                    } else {
                        progressBar.isVisible = true
                        progressBar.progress = newProgress
                    }
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    (activity as? AppCompatActivity)?.supportActionBar?.subtitle = title
                }
            }
            loadUrl(uri.toString())
        }
    }

    companion object {
        private const val ARG_URI = "PHOTO_PAGE_URL"

        private const val PROGRESS_BAR_MAX_RANGE = 100

        @JvmStatic
        fun newInstance(uri: Uri?) = PhotoPageFragment().apply {
            arguments = bundleOf(ARG_URI to uri)
        }
    }
}
