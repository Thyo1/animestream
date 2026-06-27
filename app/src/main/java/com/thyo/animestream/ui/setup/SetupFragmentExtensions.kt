package com.thyo.animestream.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.thyo.animestream.APIHolder.apis
import com.thyo.animestream.MainActivity.Companion.afterRepositoryLoadedEvent
import com.thyo.animestream.R
import com.thyo.animestream.databinding.FragmentSetupExtensionsBinding
import com.thyo.animestream.mvvm.safe
import com.thyo.animestream.plugins.RepositoryManager
import com.thyo.animestream.plugins.RepositoryManager.PREBUILT_REPOSITORIES
import com.thyo.animestream.ui.settings.extensions.PluginsViewModel
import com.thyo.animestream.ui.settings.extensions.RepoAdapter
import com.thyo.animestream.utils.Coroutines.main
import com.thyo.animestream.utils.UIHelper.fixPaddingStatusbar


class SetupFragmentExtensions : Fragment() {
    companion object {
        const val SETUP_EXTENSION_BUNDLE_IS_SETUP = "isSetup"

        /**
         * If false then this is treated a singular screen with a done button
         * */
        fun newInstance(isSetup: Boolean): Bundle {
            return Bundle().apply {
                putBoolean(SETUP_EXTENSION_BUNDLE_IS_SETUP, isSetup)
            }
        }
    }

    var binding: FragmentSetupExtensionsBinding? = null

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val localBinding = FragmentSetupExtensionsBinding.inflate(inflater, container, false)
        binding = localBinding
        return localBinding.root
        //return inflater.inflate(R.layout.fragment_setup_extensions, container, false)
    }


    override fun onResume() {
        super.onResume()
        afterRepositoryLoadedEvent += ::setRepositories
    }

    override fun onStop() {
        super.onStop()
        afterRepositoryLoadedEvent -= ::setRepositories
    }

    private fun setRepositories(success: Boolean = true) {
        main {
            val repositories = RepositoryManager.getRepositories() + PREBUILT_REPOSITORIES
            val hasRepos = repositories.isNotEmpty()
            binding?.repoRecyclerView?.isVisible = hasRepos
            binding?.blankRepoScreen?.isVisible = !hasRepos
//            view_public_repositories_button?.isVisible = hasRepos

            if (hasRepos) {
                binding?.repoRecyclerView?.adapter = RepoAdapter(true, {}, {
                    PluginsViewModel.downloadAll(activity, it.url, null)
                }).apply { updateList(repositories) }
            }
//            else {
//                list_repositories?.setOnClickListener {
//                    // Open webview on tv if browser fails
//                    openBrowser(PUBLIC_REPOSITORIES_LIST, isTvSettings(), this)
//                }
//            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fixPaddingStatusbar(binding?.setupRoot)
        val isSetup = arguments?.getBoolean(SETUP_EXTENSION_BUNDLE_IS_SETUP) ?: false

//        view_public_repositories_button?.setOnClickListener {
//            openBrowser(PUBLIC_REPOSITORIES_LIST, isTvSettings(), this)
//        }

        safe {
           // val ctx = context ?: return@safe
            setRepositories()
            binding?.apply {
                if (!isSetup) {
                    nextBtt.setText(R.string.setup_done)
                }
                prevBtt.isVisible = isSetup

                nextBtt.setOnClickListener {
                    // Continue setup
                    if (isSetup)
                        if (
                        // If any available languages
                            synchronized(apis) { apis.distinctBy { it.lang }.size > 1 }
                        ) {
                            findNavController().navigate(R.id.action_navigation_setup_extensions_to_navigation_setup_provider_languages)
                        } else {
                            findNavController().navigate(R.id.action_navigation_setup_extensions_to_navigation_setup_media)
                        }
                    else
                        findNavController().navigate(R.id.navigation_home)
                }

                prevBtt.setOnClickListener {
                    findNavController().navigate(R.id.navigation_setup_language)
                }
            }
        }
    }


}