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

const val REQUEST_CODE = 1000

class EventBroadCastListingActivity : BaseActivity(),
    EventsBroadCastListingAdapter.IRecyclerViewClickListener {

    @Inject
    lateinit var eventListingViewModelFact: EventListingViewModelFact
    private lateinit var eventsBroadCastListingViewModel: EventsBroadCastListingViewModel
    private val eventListResponse = ArrayList<EventDetailResponse>()
    private val eventsBroadCastListingAdapter: EventsBroadCastListingAdapter by lazy {
        EventsBroadCastListingAdapter(this, this, eventListResponse)
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
        tvStoreURL.text = SharedPrefUtils.getStoreUrl(BaseApplication.getInstance())
        if (ChannelizePreferences.getCurrentUserProfileImage(BaseApplication.getInstance()).isNullOrBlank()) {
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
                        getString(R.string.null_user_name_string)
                    }
                }
                tvUserName.text = initials
            } else {
                tvUserName.text = getString(R.string.null_user_name_string)
            }
            ivUserProfile.visibility = View.GONE
            tvUserName.visibility = View.VISIBLE
        } else {
            ivUserProfile.visibility = View.VISIBLE
            tvUserName.visibility = View.GONE
            ImageLoader.showImage(
                imageUrl = ChannelizePreferences.getCurrentUserProfileImage(BaseApplication.getInstance()),
                viewId = ivUserProfile
            )
        }
        onClick
    }

    private val onClick: Unit
        get() {
            tvUserName.setOnClickListener {
                viewLogoutPopUp.visibility = View.VISIBLE
            }
            ivUserProfile.setOnClickListener {
                viewLogoutPopUp.visibility = View.VISIBLE
            }
            viewLogoutPopUp.setOnClickListener {
                viewLogoutPopUp.visibility = View.GONE
            }
            tvRefresh.setOnClickListener {
                eventListResponse.clear()
                eventsBroadCastListingAdapter.notifyDataSetChanged()
                getEventsList()
                viewLogoutPopUp.visibility = View.GONE
            }
            tvLogout.setOnClickListener {
                try {
                    val progressBar = progressDialog(this)
                    progressBar.show()
                    SharedPrefUtils.setPublicApiKey(this, null)
                    SharedPrefUtils.isUserLoggedIn(this, false)
                    SharedPrefUtils.onClearSharedPref(this)
                    eventsBroadCastListingViewModel.onUserLogout().observe(this, Observer { isUserLoggedOut ->
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
        eventsBroadCastListingViewModel =
            ViewModelProvider(this, eventListingViewModelFact).get(EventsBroadCastListingViewModel::class.java)
    }

    private fun getEventsList() {
        val progressBar = progressDialog(this)
        progressBar.show()
        eventsBroadCastListingViewModel.getEventList().observe(this, Observer { eventDetailResource ->
            when (eventDetailResource.status) {
                Resource.Status.SUCCESS -> {
                    if (!eventDetailResource.data.isNullOrEmpty()) {
                        rlNoEventContainer.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        eventListResponse.clear()
                        eventDetailResource.data.filter { eventDetailResponse -> eventDetailResponse.status != LiveBroadcasterConstants.BROADCAST_EVENT_STATUS_COMPLETED }
                            .let { filteredData ->
                                eventListResponse.addAll(filteredData)
                            }
                        if (eventListResponse.isNullOrEmpty()) {
                            rlNoEventContainer.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        }
                        eventsBroadCastListingAdapter.notifyDataSetChanged()
                        progressBar.dismiss()
                    } else {
                        progressBar.dismiss()
                        rlNoEventContainer.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                }
                Resource.Status.ERROR -> {
                    progressBar.dismiss()
                    rlNoEventContainer.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    showToast(this, eventDetailResource?.message)
                }
            }
        })
    }

    override fun onClick(position: Int) {
        val productsList = ArrayList<String>()
        eventListResponse[position].products.forEach {
                products -> productsList.add(products.id)
        }
        val intent = Intent(this, LSCBroadCastSettingUpAndLiveActivity::class.java)
        intent.putExtra(LiveBroadcasterConstants.BROADCAST_ID, eventListResponse[position].id)
        intent.putStringArrayListExtra(LiveBroadcasterConstants.EVENT_PRODUCT_IDS, productsList)
        intent.putExtra(LiveBroadcasterConstants.START_TIME, eventListResponse[position].startTime)
        intent.putExtra(LiveBroadcasterConstants.STOP_TIME, eventListResponse[position].endTime)
        intent.putExtra(LiveBroadcasterConstants.CONVERSATION_ID, eventListResponse[position].metaData.conversationId)
        intent.putExtra(LiveBroadcasterConstants.EVENT_NAME, eventListResponse[position].title)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            eventListResponse.filter { eventDetailResponse ->
                eventDetailResponse.id != data?.getStringExtra(LiveBroadcasterConstants.BROADCAST_ID)
            }.let { filteredList ->
                eventListResponse.clear()
                eventListResponse.addAll(filteredList)
            }
            if (eventListResponse.isNullOrEmpty()) {
                rlNoEventContainer.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                recyclerView.visibility = View.VISIBLE
                rlNoEventContainer.visibility = View.GONE
                eventsBroadCastListingAdapter.notifyDataSetChanged()
            }
        }
    }

}
