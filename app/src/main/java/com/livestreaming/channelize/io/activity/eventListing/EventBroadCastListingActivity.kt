package com.livestreaming.channelize.io.activity.eventListing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.channelize.apisdk.Channelize
import com.channelize.apisdk.utils.ChannelizePreferences
import com.livestreaming.channelize.io.*
import com.livestreaming.channelize.io.activity.BaseActivity
import com.livestreaming.channelize.io.activity.login.LSCBroadcastLoginActivity
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadCastSettingUpAndLiveActivity
import com.livestreaming.channelize.io.adapter.EventsBroadCastListingAdapter
import com.livestreaming.channelize.io.model.EventDetailResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import kotlinx.android.synthetic.main.activity_event_broadcast_listing.*
import kotlinx.android.synthetic.main.over_lay_refresh_logout_pop_up_layout.*
import javax.inject.Inject

class EventBroadCastListingActivity : BaseActivity(),
    EventsBroadCastListingAdapter.IRecyclerViewClickListener {

    @Inject
    lateinit var eventListingViewModelFact: EventListingViewModelFact
    private lateinit var viewModel: EventsBroadCastListingViewModel
    private var eventListResponse: ArrayList<EventDetailResponse>? = null
    private val eventsBroadCastListingAdapter: EventsBroadCastListingAdapter by lazy {
        EventsBroadCastListingAdapter(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_broadcast_listing)
        (BaseApplication.getInstance() as Injector).createAppComponent()
            .inject(this)
        initRecyclerView()
        initViewModel()
        getEventsList()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = eventsBroadCastListingAdapter
        storeURLTextView.text = SharedPrefUtils.getStoreUrl(BaseApplication.getInstance())
        if (ChannelizePreferences.getCurrentUserProfileImage(BaseApplication.getInstance())
                .isNullOrBlank()
        ) {
            val name = ChannelizePreferences.getCurrentUserName(BaseApplication.getInstance())
            if (!name.isNullOrBlank()) {
                val nameParts = name.split(" ").toTypedArray()
                val initials = when (nameParts.size) {
                    1 -> {
                        nameParts[0][0].toString()
                    }
                    2, 3, 4 -> {
                        nameParts[0][0].toString() + nameParts[1][0]
                    }
                    else -> {
                        "null"
                    }
                }
                nameTextView.text = initials
            } else {
                nameTextView.text = "null"
            }
            userImageView.visibility = View.GONE
            nameTextView.visibility = View.VISIBLE
        } else {
            userImageView.visibility = View.VISIBLE
            nameTextView.visibility = View.GONE
            ImageLoader.showImage(
                imageUrl = ChannelizePreferences.getCurrentUserProfileImage(BaseApplication.getInstance()),
                viewId = userImageView
            )
        }
        onClick
    }

    private val onClick: Unit
        get() {
            nameTextView.setOnClickListener {
                logoutPopUp.visibility = View.VISIBLE
            }
            userImageView.setOnClickListener {
                logoutPopUp.visibility = View.VISIBLE
            }
            logoutPopUp.setOnClickListener {
                logoutPopUp.visibility = View.GONE
            }
            refreshTextView.setOnClickListener {
                eventListResponse?.clear()
                eventsBroadCastListingAdapter.setEventList(eventListResponse)
                eventsBroadCastListingAdapter.notifyDataSetChanged()
                getEventsList()
                logoutPopUp.visibility = View.GONE
            }
            logoutTextView.setOnClickListener {
                try {
                    val progressBar = progressDialog(this)
                    progressBar.show()
                    SharedPrefUtils.setPublicApiKey(this, null)
                    SharedPrefUtils.setLoggedInFlag(this, false)
                    SharedPrefUtils.clearSharedPref(this)
                    viewModel.onUserLogout().observe(this, Observer { isUserLoggedOut ->
                        if (isUserLoggedOut) {
                            progressBar.dismiss()
                            Channelize.disconnect()
                            val intent = Intent(this, LSCBroadcastLoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            Log.d("LOGOUT", "USER_LOGGED_OUT_TRUE")
                        } else {
                            progressBar.dismiss()
                            Log.d("LOGOUT", "USER_LOGGED_OUT_FALSE")
                            showToast(this, "Logout Failed")
                        }
                    })
                } catch (e: Exception) {
                    Log.d("LogoutException", e.toString())
                }
            }
        }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this, eventListingViewModelFact
        ).get(EventsBroadCastListingViewModel::class.java)
    }

    private fun getEventsList() {
        val progressBar = progressDialog(this)
        progressBar.show()
        viewModel.getEventList().observe(this, Observer { eventDetailResource ->
            when (eventDetailResource.status) {
                Resource.Status.SUCCESS -> {
                    if (!eventDetailResource.data.isNullOrEmpty()) {
                        val formatList = ArrayList<EventDetailResponse>()
                        val listData =
                            eventDetailResource.data.toMutableList() as ArrayList<EventDetailResponse>
                        listData.filter { eventDetailResponse -> eventDetailResponse.status != LiveBroadcasterConstants.BROADCAST_EVENT_STATUS_COMPLETED }
                            .let { filteredData ->
                                formatList.addAll(filteredData)
                            }
                        eventListResponse = formatList
                        eventsBroadCastListingAdapter.setEventList(eventListResponse)
                        eventsBroadCastListingAdapter.notifyDataSetChanged()
                        progressBar.dismiss()
                    } else {
                        progressBar.dismiss()
                        noEventContainer.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                }
                Resource.Status.ERROR -> {
                    progressBar.dismiss()
                    showToast(this, eventDetailResource?.message)
                    noEventContainer.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
                Resource.Status.LOADING -> {
                    progressBar.show()
                }
            }
        })
    }

    override fun onClick(position: Int) {
        val productsList = ArrayList<String>()
        eventListResponse?.get(position)?.products?.forEach { products ->
            productsList.add(products.id)
        }
        val intent = Intent(this, LSCBroadCastSettingUpAndLiveActivity::class.java)
        intent.putExtra(LiveBroadcasterConstants.BROADCAST_ID, eventListResponse?.get(position)?.id)
        intent.putStringArrayListExtra(
            LiveBroadcasterConstants.EVENT_PRODUCT_IDS,
            productsList
        )
        intent.putExtra(
            LiveBroadcasterConstants.START_TIME,
            eventListResponse?.get(position)?.startTime
        )
        intent.putExtra(
            LiveBroadcasterConstants.STOP_TIME,
            eventListResponse?.get(position)?.endTime
        )
        intent.putExtra(
            LiveBroadcasterConstants.CONVERSATION_ID,
            eventListResponse?.get(position)?.metaData?.conversationId
        )
        intent.putExtra(
            LiveBroadcasterConstants.EVENT_NAME,
            eventListResponse?.get(position)?.title
        )
        startActivityForResult(intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                eventListResponse?.filter { eventDetailResponse ->
                    eventDetailResponse.id != data?.getStringExtra(LiveBroadcasterConstants.BROADCAST_ID)
                }?.let { filteredList ->
                    eventListResponse?.clear()
                    eventListResponse =
                        filteredList.toMutableList() as ArrayList<EventDetailResponse>
                }
                if (eventListResponse.isNullOrEmpty()) {
                    noEventContainer.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    noEventContainer.visibility = View.GONE
                    eventsBroadCastListingAdapter.setEventList(eventListResponse)
                    eventsBroadCastListingAdapter.notifyDataSetChanged()
                }
            }
        }
    }

}
