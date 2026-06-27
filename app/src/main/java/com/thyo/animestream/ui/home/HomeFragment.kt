package com.thyo.animestream.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.thyo.animestream.*
import com.thyo.animestream.APIHolder.apis
import com.thyo.animestream.CommonActivity.showToast
import com.thyo.animestream.databinding.FragmentHomeBinding
import com.thyo.animestream.databinding.HomeEpisodesExpandedBinding
import com.thyo.animestream.databinding.HomeSelectMainpageBinding
import com.thyo.animestream.databinding.TvtypesChipsBinding
import com.thyo.animestream.mvvm.Resource
import com.thyo.animestream.mvvm.logError
import com.thyo.animestream.mvvm.observe
import com.thyo.animestream.mvvm.observeNullable
import com.thyo.animestream.ui.APIRepository.Companion.noneApi
import com.thyo.animestream.ui.APIRepository.Companion.randomApi
import com.thyo.animestream.ui.account.AccountHelper.showAccountSelectLinear
import com.thyo.animestream.utils.txt
import com.thyo.animestream.ui.search.*
import com.thyo.animestream.ui.search.SearchHelper.handleSearchClickCallback
import com.thyo.animestream.ui.settings.Globals.EMULATOR
import com.thyo.animestream.ui.settings.Globals.PHONE
import com.thyo.animestream.ui.settings.Globals.TV
import com.thyo.animestream.ui.settings.Globals.isLayout
import com.thyo.animestream.utils.AppContextUtils.filterProviderByPreferredMedia
import com.thyo.animestream.utils.AppContextUtils.getApiProviderLangSettings
import com.thyo.animestream.utils.AppContextUtils.isNetworkAvailable
import com.thyo.animestream.utils.AppContextUtils.isRecyclerScrollable
import com.thyo.animestream.utils.AppContextUtils.loadSearchResult
import com.thyo.animestream.utils.AppContextUtils.ownHide
import com.thyo.animestream.utils.AppContextUtils.ownShow
import com.thyo.animestream.utils.AppContextUtils.setDefaultFocus
import com.thyo.animestream.utils.Coroutines.ioSafe
import com.thyo.animestream.utils.DataStoreHelper
import com.thyo.animestream.utils.Event
import com.thyo.animestream.utils.SubtitleHelper.getFlagFromIso
import com.thyo.animestream.utils.UIHelper.dismissSafe
import com.thyo.animestream.utils.UIHelper.getSpanCount
import com.thyo.animestream.utils.UIHelper.navigate
import com.thyo.animestream.utils.UIHelper.popupMenuNoIconsAndNoStringRes
import java.util.*


class HomeFragment : Fragment() {
    companion object {
        val configEvent = Event<Int>()
        var currentSpan = 1
        val listHomepageItems = mutableListOf<SearchResponse>()

        private val errorProfilePics = listOf(
            R.drawable.monke_benene,
            R.drawable.monke_burrito,
            R.drawable.monke_coco,
            R.drawable.monke_cookie,
            R.drawable.monke_flusdered,
            R.drawable.monke_funny,
            R.drawable.monke_like,
            R.drawable.monke_party,
            R.drawable.monke_sob,
            R.drawable.monke_drink,
        )

        val errorProfilePic = errorProfilePics.random()

        fun Activity.loadHomepageList(
            expand: HomeViewModel.ExpandableHomepageList,
            deleteCallback: (() -> Unit)? = null,
            expandCallback: (suspend (String) -> HomeViewModel.ExpandableHomepageList?)? = null,
            dismissCallback: (() -> Unit),
        ): BottomSheetDialog {
            val context = this
            val bottomSheetDialogBuilder = BottomSheetDialog(context)
            val binding: HomeEpisodesExpandedBinding = HomeEpisodesExpandedBinding.inflate(
                bottomSheetDialogBuilder.layoutInflater,
                null,
                false
            )
            bottomSheetDialogBuilder.setContentView(binding.root)

            val item = expand.list
            binding.homeExpandedText.text = item.name

            binding.homeExpandedDelete.isGone = deleteCallback == null
            if (deleteCallback != null) {
                binding.homeExpandedDelete.setOnClickListener {
                    try {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                        val dialogClickListener =
                            DialogInterface.OnClickListener { _, which ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> {
                                        deleteCallback.invoke()
                                        bottomSheetDialogBuilder.dismissSafe(this)
                                    }

                                    DialogInterface.BUTTON_NEGATIVE -> {}
                                }
                            }

                        builder.setTitle(R.string.clear_history)
                            .setMessage(
                                context.getString(R.string.delete_message).format(
                                    item.name
                                )
                            )
                            .setPositiveButton(R.string.delete, dialogClickListener)
                            .setNegativeButton(R.string.cancel, dialogClickListener)
                            .show().setDefaultFocus()
                    } catch (e: Exception) {
                        logError(e)
                    }
                }
            }
            binding.homeExpandedDragDown.setOnClickListener {
                bottomSheetDialogBuilder.dismissSafe(this)
            }


            // Span settings
            binding.homeExpandedRecycler.spanCount = currentSpan

            binding.homeExpandedRecycler.adapter =
                SearchAdapter(item.list.toMutableList(), binding.homeExpandedRecycler) { callback ->
                    handleSearchClickCallback(callback)
                    if (callback.action == SEARCH_ACTION_LOAD || callback.action == SEARCH_ACTION_PLAY_FILE) {
                        bottomSheetDialogBuilder.ownHide() // we hide here because we want to resume it later
                    }
                }.apply {
                    hasNext = expand.hasNext
                }

            binding.homeExpandedRecycler.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                var expandCount = 0
                val name = expand.list.name

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    val adapter = recyclerView.adapter
                    if (adapter !is SearchAdapter) return

                    val count = adapter.itemCount
                    val currentHasNext = adapter.hasNext

                    if (!recyclerView.isRecyclerScrollable() && currentHasNext && expandCount != count) {
                        expandCount = count
                        ioSafe {
                            expandCallback?.invoke(name)?.let { newExpand ->
                                (recyclerView.adapter as? SearchAdapter?)?.apply {
                                    hasNext = newExpand.hasNext
                                    updateList(newExpand.list.list)
                                }
                            }
                        }
                    }
                }
            })

            val spanListener = { span: Int ->
                binding.homeExpandedRecycler.spanCount = span
            }

            configEvent += spanListener

            bottomSheetDialogBuilder.setOnDismissListener {
                dismissCallback.invoke()
                configEvent -= spanListener
            }

            bottomSheetDialogBuilder.show()
            return bottomSheetDialogBuilder
        }

        private fun getPairList(
            anime: Chip?,
            cartoons: Chip?,
            tvs: Chip?,
            docs: Chip?,
            movies: Chip?,
            asian: Chip?,
            livestream: Chip?,
            torrent: Chip?,
            nsfw: Chip?,
            others: Chip?,
        ): List<Pair<Chip?, List<TvType>>> {
            // This list should be same order as home screen to aid navigation
            return listOf(
                Pair(movies, listOf(TvType.Movie)),
                Pair(tvs, listOf(TvType.TvSeries)),
                Pair(anime, listOf(TvType.Anime, TvType.OVA, TvType.AnimeMovie)),
                Pair(asian, listOf(TvType.AsianDrama)),
                Pair(cartoons, listOf(TvType.Cartoon)),
                Pair(docs, listOf(TvType.Documentary)),
                Pair(livestream, listOf(TvType.Live)),
                Pair(torrent, listOf(TvType.Torrent)),
                Pair(nsfw, listOf(TvType.NSFW)),
                Pair(others, listOf(TvType.Others)),
            )
        }

        private fun getPairList(header: TvtypesChipsBinding) = getPairList(
            header.homeSelectAnime,
            header.homeSelectCartoons,
            header.homeSelectTvSeries,
            header.homeSelectDocumentaries,
            header.homeSelectMovies,
            header.homeSelectAsian,
            header.homeSelectLivestreams,
            header.homeSelectTorrents,
            header.homeSelectNsfw,
            header.homeSelectOthers
        )

        fun validateChips(header: TvtypesChipsBinding?, validTypes: List<TvType>) {
            if (header == null) return
            val pairList = getPairList(header)
            for ((button, types) in pairList) {
                val isValid = validTypes.any { types.contains(it) }
                button?.isVisible = isValid
            }
        }

        fun updateChips(header: TvtypesChipsBinding?, selectedTypes: List<TvType>) {
            if (header == null) return
            val pairList = getPairList(header)
            for ((button, types) in pairList) {
                button?.isChecked =
                    button?.isVisible == true && selectedTypes.any { types.contains(it) }
            }
        }

        fun bindChips(
            header: TvtypesChipsBinding?,
            selectedTypes: List<TvType>,
            validTypes: List<TvType>,
            callback: (List<TvType>) -> Unit
        ) {
            bindChips(header, selectedTypes, validTypes, callback, null, null)
        }

        fun bindChips(
            header: TvtypesChipsBinding?,
            selectedTypes: List<TvType>,
            validTypes: List<TvType>,
            callback: (List<TvType>) -> Unit,
            nextFocusDown: Int?,
            nextFocusUp: Int?
        ) {
            if (header == null) return
            val pairList = getPairList(header)
            for ((button, types) in pairList) {
                val isValid = validTypes.any { types.contains(it) }
                button?.isVisible = isValid
                button?.isChecked = isValid && selectedTypes.any { types.contains(it) }
                button?.isFocusable = true
                if (isLayout(TV)) {
                    button?.isFocusableInTouchMode = true
                }

                if (nextFocusDown != null)
                    button?.nextFocusDownId = nextFocusDown

                if (nextFocusUp != null)
                    button?.nextFocusUpId = nextFocusUp

                button?.setOnCheckedChangeListener { _, _ ->
                    val list = ArrayList<TvType>()
                    for ((sbutton, vvalidTypes) in pairList) {
                        if (sbutton?.isChecked == true)
                            list.addAll(vvalidTypes)
                    }
                    callback(list)
                }
            }
        }

        fun Context.selectHomepage(selectedApiName: String?, callback: (String) -> Unit) {
            val validAPIs = filterProviderByPreferredMedia().toMutableList()

            validAPIs.add(0, randomApi)
            validAPIs.add(0, noneApi)

            val builder =
                BottomSheetDialog(this)

            builder.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            val binding: HomeSelectMainpageBinding = HomeSelectMainpageBinding.inflate(
                builder.layoutInflater,
                null,
                false
            )

            builder.setContentView(binding.root)
            builder.show()
            builder.let { dialog ->
                val isMultiLang = getApiProviderLangSettings().let { set ->
                    set.size > 1 || set.contains(AllLanguagesName)
                }

                var currentApiName = selectedApiName

                var currentValidApis: MutableList<MainAPI> = mutableListOf()
                val preSelectedTypes = DataStoreHelper.homePreference.toMutableList()

                binding.cancelBtt.setOnClickListener {
                    dialog.dismissSafe()
                }

                binding.applyBtt.setOnClickListener {
                    if (currentApiName != selectedApiName) {
                        currentApiName?.let(callback)
                    }
                    dialog.dismissSafe()
                }

                var pinnedphashset = DataStoreHelper.pinnedProviders.toHashSet()

                val listView = dialog.findViewById<ListView>(R.id.listview1)

                val arrayAdapter = object : ArrayAdapter<String>(this, R.layout.sort_bottom_single_provider_choice,
                    mutableListOf()
                ) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.sort_bottom_single_provider_choice, parent, false)
                        val titleText = view.findViewById<TextView>(R.id.text1)
                        val pinIcon = view.findViewById<ImageView>(R.id.pinicon)
                        val name = getItem(position)
                        titleText?.text = name
                        val isPinned = pinnedphashset.contains(currentValidApis[position].name ?: "")
                        pinIcon.visibility = if (isPinned) View.VISIBLE else View.GONE
                        return view
                    }
                }
                listView?.adapter = arrayAdapter
                listView?.choiceMode = AbsListView.CHOICE_MODE_SINGLE

                listView?.setOnItemClickListener { _, _, i, _ ->
                    if (currentValidApis.isNotEmpty()) {
                        currentApiName = currentValidApis[i].name
                        currentApiName?.let(callback)
                        dialog.dismissSafe()
                    }
                }

                fun updateList() {
                    DataStoreHelper.homePreference = preSelectedTypes
                    val pinnedp = DataStoreHelper.pinnedProviders.toList()
                    pinnedphashset = pinnedp.toHashSet()
                    arrayAdapter.clear()
                    val sortedApis = validAPIs
                        .filter {it.hasMainPage && (pinnedphashset.contains(it.name) ||  it.supportedTypes.any(preSelectedTypes::contains)) }
                        .sortedBy { it.name.lowercase(Locale.ROOT) }

                    val sortedApiMap = LinkedHashMap<String, MainAPI>().apply {
                        sortedApis.forEach { put(it.name, it) }
                    }

                    val pinnedApis = pinnedp.asReversed().mapNotNull { name ->
                        sortedApiMap[name]
                    }

                    val remainingApis = sortedApis.filterNot { pinnedphashset.contains(it.name) }

                    currentValidApis = mutableListOf<MainAPI>().apply {
                        addAll(validAPIs.take(2))
                        addAll(pinnedApis)
                        addAll(remainingApis)
                    }

                    val names =
                        currentValidApis.map { if (isMultiLang) "${getFlagFromIso(it.lang)?.plus(" ") ?: ""}${it.name}" else it.name }
                    val index = currentValidApis.map { it.name }.indexOf(currentApiName)
                    listView?.setItemChecked(index, true)
                    arrayAdapter.addAll(names)
                    arrayAdapter.notifyDataSetChanged()
                }

                listView?.setOnItemLongClickListener { _, _, i, _ ->
                    if (currentValidApis.isNotEmpty() && i>1) {
                        val pinnedp = DataStoreHelper.pinnedProviders.toMutableList()
                        val thisapi = currentValidApis[i].name
                        if(pinnedp.contains(thisapi)){
                            pinnedp.remove(thisapi)
                        }else{
                            pinnedp.add(thisapi)
                        }
                        DataStoreHelper.pinnedProviders = pinnedp.toTypedArray()
                        updateList()
                    }
                    true
                }

                bindChips(
                    binding.tvtypesChipsScroll.tvtypesChips,
                    preSelectedTypes,
                    validAPIs.flatMap { it.supportedTypes }.distinct()
                ) { list ->
                    preSelectedTypes.clear()
                    preSelectedTypes.addAll(list)
                    updateList()
                }
                updateList()
            }
        }
    }

    private val homeViewModel: HomeViewModel by activityViewModels()

    var binding: FragmentHomeBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomSheetDialog?.ownShow()
        val layout =
            if (isLayout(TV or EMULATOR)) R.layout.fragment_home_tv else R.layout.fragment_home
        val root = inflater.inflate(layout, container, false)
        binding = try {
            FragmentHomeBinding.bind(root)
        } catch (t: Throwable) {
            showToast(txt(R.string.unable_to_inflate, t.message ?: ""), Toast.LENGTH_LONG)
            logError(t)
            null
        }

        return root
    }

    override fun onDestroyView() {
        bottomSheetDialog?.ownHide()
        binding = null
        super.onDestroyView()
    }

    private fun fixGrid() {
        activity?.getSpanCount()?.let {
            currentSpan = it
        }
        configEvent.invoke(currentSpan)
    }

    private val apiChangeClickListener = View.OnClickListener { view ->
        view.context.selectHomepage(currentApiName) { api ->
            homeViewModel.loadAndCancel(api, forceReload = true, fromUI = true)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        fixGrid()
    }

    private var currentApiName: String? = null
    private var toggleRandomButton = false

    private var bottomSheetDialog: BottomSheetDialog? = null
    private var homeMasterAdapter: HomeParentItemAdapterPreview? = null

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fixGrid()

        binding?.apply {
            homeApiFab.setOnClickListener(apiChangeClickListener)
            homeApiFab.setOnLongClickListener{
                if(currentApiName == noneApi.name) return@setOnLongClickListener false
                homeViewModel.loadAndCancel(currentApiName, forceReload = true, fromUI = true)
                showToast(R.string.action_reload,Toast.LENGTH_SHORT)
                true
            }
            homeChangeApi.setOnClickListener(apiChangeClickListener)
            homeSwitchAccount.setOnClickListener {
                activity?.showAccountSelectLinear()
            }

            homeRandom.setOnClickListener {
                if (listHomepageItems.isNotEmpty()) {
                    activity?.loadSearchResult(listHomepageItems.random())
                }
            }
            homeMasterAdapter = HomeParentItemAdapterPreview(
                fragment = this@HomeFragment,
                homeViewModel,
            )
            homeMasterRecycler.adapter = homeMasterAdapter
            homeApiFab.isVisible = isLayout(PHONE)

            homeMasterRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) { 
                        homeApiFab.shrink() 
                        homeRandom.shrink()
                    } else if (dy < -5) {
                        if (isLayout(PHONE)) {
                            homeApiFab.extend() 
                            homeRandom.extend()
                        }
                    }
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }


        context?.let {
            val settingsManager = PreferenceManager.getDefaultSharedPreferences(it)
            toggleRandomButton =
                settingsManager.getBoolean(
                    getString(R.string.random_button_key),
                    false
                ) && isLayout(PHONE)
            binding?.homeRandom?.visibility = View.GONE
        }

        observe(homeViewModel.apiName) { apiName ->
            currentApiName = apiName
            binding?.homeApiFab?.text = apiName
            binding?.homeChangeApi?.text = apiName
        }

        observe(homeViewModel.page) { data ->
            binding?.apply {
                when (data) {
                    is Resource.Success -> {
                        homeLoadingShimmer.stopShimmer()

                        val d = data.value
                        val mutableListOfResponse = mutableListOf<SearchResponse>()
                        listHomepageItems.clear()

                        (homeMasterRecycler.adapter as? ParentItemAdapter)?.submitList(d.values.map {
                            it.copy(
                                list = it.list.copy(list = it.list.list.toMutableList())
                            )
                        }.toMutableList())

                        homeLoading.isVisible = false
                        homeLoadingError.isVisible = false
                        homeMasterRecycler.isVisible = true
                        
                        if (toggleRandomButton) {
                            d.values.forEach { dlist ->
                                mutableListOfResponse.addAll(dlist.list.list)
                            }
                            listHomepageItems.addAll(mutableListOfResponse.distinctBy { it.url })

                            homeRandom.isVisible = listHomepageItems.isNotEmpty()
                        } else {
                            homeRandom.isGone = true
                        }
                    }

                    is Resource.Failure -> {
                        homeLoadingShimmer.stopShimmer()
                        homeReloadConnectionerror.setOnClickListener(apiChangeClickListener)
                        homeReloadConnectionOpenInBrowser.setOnClickListener { view ->
                            val validAPIs = apis

                            view.popupMenuNoIconsAndNoStringRes(validAPIs.mapIndexed { index, api ->
                                Pair(
                                    index,
                                    api.name
                                )
                            }) {
                                try {
                                    val i = Intent(Intent.ACTION_VIEW)
                                    i.data = Uri.parse(validAPIs[itemId].mainUrl)
                                    startActivity(i)
                                } catch (e: Exception) {
                                    logError(e)
                                }
                            }
                        }

                        homeLoading.isVisible = false
                        homeLoadingError.isVisible = true
                        homeMasterRecycler.isVisible = false

                        val hasNoNetworkConnection = context?.isNetworkAvailable() == false
                        val isNetworkError = data.isNetworkError

                        homeReloadConnectionGoToDownloads.isVisible =
                            hasNoNetworkConnection || isNetworkError

                        homeReloadConnectionOpenInBrowser.isGone = hasNoNetworkConnection

                        resultErrorText.text = if (hasNoNetworkConnection) {
                            getString(R.string.no_internet_connection)
                        } else {
                            data.errorString
                        }

                        homeReloadConnectionGoToDownloads.setOnClickListener {
                            activity?.navigate(R.id.navigation_downloads)
                        }
                    }

                    is Resource.Loading -> {
                        (homeMasterRecycler.adapter as? ParentItemAdapter)?.submitList(listOf())
                        homeLoadingShimmer.startShimmer()
                        homeLoading.isVisible = true
                        homeLoadingError.isVisible = false
                        homeMasterRecycler.isVisible = false
                    }
                }
            }
        }

        observeNullable(homeViewModel.popup) { item ->
            if (item == null) {
                bottomSheetDialog?.dismissSafe()
                bottomSheetDialog = null
                return@observeNullable
            }

            if (bottomSheetDialog != null) {
                return@observeNullable
            }

            val (items, delete) = item

            bottomSheetDialog = activity?.loadHomepageList(items, expandCallback = {
                homeViewModel.expandAndReturn(it)
            }, dismissCallback = {
                homeViewModel.popup(null)
                bottomSheetDialog = null
            }, deleteCallback = delete)
        }

        homeViewModel.reloadStored()
        homeViewModel.loadAndCancel(DataStoreHelper.currentHomePage, false)
    }
}
