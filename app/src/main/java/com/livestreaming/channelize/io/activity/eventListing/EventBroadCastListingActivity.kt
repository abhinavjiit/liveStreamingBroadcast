package com.livestreaming.channelize.io.activity.eventListing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.channelize.apisdk.utils.ChannelizePreferences
import com.livestreaming.channelize.io.BaseApplication
import com.livestreaming.channelize.io.Injector
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.SharedPrefUtils
import com.livestreaming.channelize.io.activity.BaseActivity
import com.livestreaming.channelize.io.activity.lscSettingUp.LSCBroadCastSettingUpActivity
import com.livestreaming.channelize.io.adapter.EventsBroadCastListingAdapter
import com.livestreaming.channelize.io.model.EventDetailResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import com.squareup.picasso.Picasso
import javax.inject.Inject

class EventBroadCastListingActivity : BaseActivity(), View.OnClickListener,
    EventsBroadCastListingAdapter.RecyclerViewClickListener {


    private val eventsBroadCastListingAdapter: EventsBroadCastListingAdapter by lazy {
        EventsBroadCastListingAdapter(this)
    }
    private lateinit var recyclerView: RecyclerView

    private var eventListResponse: ArrayList<EventDetailResponse>? = null


    @Inject
    lateinit var eventListingViewModelFact: EventListingViewModelFact

    private lateinit var viewModel: EventsBroadCastListingViewModel
    private lateinit var userImageView: ImageView
    private lateinit var logoutPopUp: View
    private lateinit var refreshTextView: TextView
    private lateinit var logoutTextView: TextView
    private lateinit var noEventContainer: RelativeLayout


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
        logoutPopUp = findViewById(R.id.logoutPopUp)
        userImageView = findViewById(R.id.userImageView)
        recyclerView = findViewById(R.id.recyclerView)
        refreshTextView = logoutPopUp.findViewById(R.id.refreshTextView)
        logoutTextView = logoutPopUp.findViewById(R.id.logoutTextView)
        noEventContainer = findViewById(R.id.noEventContainer)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = eventsBroadCastListingAdapter
        Picasso.get()
            .load(ChannelizePreferences.getCurrentUserProfileImage(BaseApplication.getInstance()))
            .into(userImageView)
        userImageView.setOnClickListener(this)
        logoutPopUp.setOnClickListener(this)
        refreshTextView.setOnClickListener(this)
        logoutTextView.setOnClickListener(this)

    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this, eventListingViewModelFact
        ).get(EventsBroadCastListingViewModel::class.java)
    }

    private fun getEventsList() {
        val progressBar = progressDialog(this)
        progressBar.show()
        viewModel.getEventList().observe(this, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    progressBar.dismiss()
                    if (!it.data.isNullOrEmpty()) {
                        eventListResponse =
                            it.data.toMutableList() as ArrayList<EventDetailResponse>
                        eventsBroadCastListingAdapter.setEventList(eventListResponse)
                        eventsBroadCastListingAdapter.notifyDataSetChanged()
                    } else {
                        noEventContainer.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                }
                Resource.Status.ERROR -> {
                    progressBar.dismiss()
                    showToast(this, it?.message!!)
                    noEventContainer.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
                Resource.Status.LOADING -> {
                    progressBar.show()
                }
            }

        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.userImageView -> {
                logoutPopUp.visibility = View.VISIBLE
            }
            R.id.logoutPopUp -> {
                logoutPopUp.visibility = View.GONE
            }
            R.id.refreshTextView -> {
                eventListResponse?.clear()
                eventsBroadCastListingAdapter.setEventList(eventListResponse)
                eventsBroadCastListingAdapter.notifyDataSetChanged()
                getEventsList()
                logoutPopUp.visibility = View.GONE
            }
            R.id.logoutTextView -> {
                SharedPrefUtils.setPublicApiKey(this, null)
                SharedPrefUtils.setLoggedInFlag(this, false)
                SharedPrefUtils.clearSharedPref(this)
                Log.d("LOGOUT", "USER LOGGED OUT")
                finish()
            }
        }
    }

    override fun onClick() {
        val intent = Intent(this, LSCBroadCastSettingUpActivity::class.java)
        startActivity(intent)
    }
}