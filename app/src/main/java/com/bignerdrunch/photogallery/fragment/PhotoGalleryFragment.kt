package com.bignerdrunch.photogallery.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.*
import com.bignerdrunch.photogallery.R
import com.bignerdrunch.photogallery.adapter.PhotoListAdapter
import com.bignerdrunch.photogallery.backend.PollWorker
import com.bignerdrunch.photogallery.databinding.FragmentPhotoGalleryBinding
import com.bignerdrunch.photogallery.viewmodel.PhotoGalleryViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private const val TAG = "PhotoGalleryFragment"
private const val POLL_WORK = "POLL_WORK"

class PhotoGalleryFragment : Fragment() {
    private var _binding: FragmentPhotoGalleryBinding? = null
    val binding
        get() = checkNotNull(_binding) {
            "Can't access binding because it is null. Is the view visible?"
        }

    private val photoGalleryViewModel: PhotoGalleryViewModel by viewModels()

    private var searchView: SearchView? = null

    /**
     * polling indicator
     */
    private var pollingMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //fragment's registration for getting a callbacks from the Menu
        setHasOptionsMenu(true)

        //create constraints
//        val constraints = Constraints.Builder()

        //required using wifi for using the WorkRequest
//            .setRequiredNetworkType(NetworkType.UNMETERED)
//            .build()
//
//        val workRequest = OneTimeWorkRequest

        //create the OneTimeWorkRequest from our class
        //subclass of the WorkRequest class
//            .Builder(PollWorker::class.java)

        //adding constraints
//            .setConstraints(constraints)
//            .build()
//
//        //planing the request
//        WorkManager.getInstance(requireContext()).enqueue(workRequest)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_photo_gallery, container, false)

        _binding = FragmentPhotoGalleryBinding.inflate(inflater, container, false)
        binding.photoGrid.layoutManager = GridLayoutManager(context, 3)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {

            /**
             * receive data from ViewModel
             */
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                //StateFlow listener
                photoGalleryViewModel.uiState.collect { state ->
//                    Log.d(TAG, "Response received: $items")

                    /**
                     * filling the RecyclerView and Adapter from PhotoGalleryUiState
                     */
                    binding.photoGrid.adapter = PhotoListAdapter(state.images)
                    searchView?.setQuery(state.query, false)
                    updatePollingState(state.isPolling)
                }
            }
        }
    }

    //inflates the Menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery, menu)

        /**
         * the search field
         */
        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        searchView = searchItem.actionView as? SearchView
        pollingMenuItem = menu.findItem(R.id.menu_item_toggle_polling)

        /**
         * listening the SearchView
         */
        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

            //Called when the user submits the query
            override fun onQueryTextSubmit(query: String?) : Boolean {
                Log.d(TAG, "QueryTextSubmit: $query")
                photoGalleryViewModel.setQuery(query ?: "")

                //true - the query was sent
                return true
            }

            /**
             * Called when a single character is changed
             */
            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG, "QueryTextChange: $newText")

                /**
                 * false - event didn't happen for this listener, send event to next listener
                 */
                return false
            }
        })
    }

    //This hook is called whenever an item in your options menu is selected.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            /**
             * clear the query from the file
             */
            R.id.menu_item_clear -> {
                photoGalleryViewModel.setQuery("")
                true
            }
            R.id.menu_item_toggle_polling -> {

                /**
                 * change the polling state
                 */
                photoGalleryViewModel.toggleIsPolling()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updatePollingState(isPolling: Boolean) {
        val toggleItemTitle = if(isPolling) {
            R.string.stop_polling
        } else R.string.start_polling

        pollingMenuItem?.setTitle(toggleItemTitle)

        /**
         * create a new Worker
         */
        if (isPolling) {

            //create constraints
            val constraints = Constraints.Builder()

                //required using wifi for using the WorkRequest
                .setRequiredNetworkType(NetworkType.UNMETERED).build()

            //create the PeriodicWorkRequest from our class
            //subclass of the WorkRequest class
            val periodicRequest =
                PeriodicWorkRequestBuilder<PollWorker>(15, TimeUnit.MINUTES)

                    //required using wifi for using the WorkRequest
                    .setConstraints(constraints).build()

            //planing a new Worker
            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(

                //name
                POLL_WORK,

                //cancel a new request with the same name and save the current
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        } else {

            //cancel the Work
            WorkManager.getInstance(requireContext()).cancelUniqueWork(POLL_WORK)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        pollingMenuItem = null
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        pollingMenuItem = null
        searchView = null
    }

}