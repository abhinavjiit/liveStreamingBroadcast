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
import com.channelize.apisdk.Channelize
import com.channelize.apisdk.utils.ChannelizePreferences
import com.livestreaming.channelize.io.BaseApplication
import com.livestreaming.channelize.io.Injector
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.SharedPrefUtils
import com.livestreaming.channelize.io.activity.BaseActivity
import com.livestreaming.channelize.io.activity.login.LSCBroadcastLoginActivity
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadCastSettingUpAndLiveActivity
import com.livestreaming.channelize.io.adapter.EventsBroadCastListingAdapter
import com.livestreaming.channelize.io.model.EventDetailResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import com.squareup.picasso.Picasso
import javax.inject.Inject

class EventBroadCastListingActivity : BaseActivity(), View.OnClickListener,
    EventsBroadCastListingAdapter.RecyclerViewClickListener {


    private val eventsBroadCastListingAdapter: EventsBroadCastListingAdapter by lazy {
        EventsBroadCastListingAdapter(this, this)
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
    private lateinit var storeURLTextView: TextView
    private lateinit var nameTextView: TextView


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
        storeURLTextView = findViewById(R.id.storeURLTextView)
        nameTextView = findViewById(R.id.nameTextView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = eventsBroadCastListingAdapter
        storeURLTextView.text = SharedPrefUtils.getStoreUrl(BaseApplication.getInstance())
        if (ChannelizePreferences.getCurrentUserProfileImage(BaseApplication.getInstance())
                .isNullOrBlank()
        ) {
            val name = ChannelizePreferences.getCurrentUserName(BaseApplication.getInstance())
            if (name.isNotBlank()) {
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
            } else
                nameTextView.text = "null"
            userImageView.visibility = View.GONE
            nameTextView.visibility = View.VISIBLE
        } else {
            userImageView.visibility = View.VISIBLE
            nameTextView.visibility = View.GONE
            Picasso.get()
                .load(ChannelizePreferences.getCurrentUserProfileImage(BaseApplication.getInstance()))
                .into(userImageView)
        }
        nameTextView.setOnClickListener(this)
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
                    if (!it.data.isNullOrEmpty()) {
                        val formatList = ArrayList<EventDetailResponse>()
                        val listData =
                            it.data.toMutableList() as ArrayList<EventDetailResponse>
                        listData.forEach {
                            if (it.status != "completed") {
                                formatList.add(it)
                            }
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
            R.id.userImageView, R.id.nameTextView -> {
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
                try {
                    val progressBar = progressDialog(this)
                    progressBar.show()
                    SharedPrefUtils.setPublicApiKey(this, null)
                    SharedPrefUtils.setLoggedInFlag(this, false)
                    SharedPrefUtils.clearSharedPref(this)
                    Channelize.logout { result, _ ->
                        if (result.isSuccessful && result != null) {
                            runOnUiThread {
                                try {
                                    progressBar.dismiss()
                                    val intent = Intent(this, LSCBroadcastLoginActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    Log.d("LOGOUT", "USER LOGGED OUT")
                                } catch (e: Exception) {
                                    progressBar.dismiss()
                                    Log.d("Exception", e.toString())
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.d("LogoutException", e.toString())
                }
            }
        }
    }

    override fun onClick(position: Int) {
        val productsList = ArrayList<String>()
        eventListResponse?.get(position)?.products?.forEach {
            productsList.add(it.id)
        }
        val intent = Intent(this, LSCBroadCastSettingUpAndLiveActivity::class.java)
        intent.putExtra("broadCastId", eventListResponse?.get(position)?.id)
        intent.putStringArrayListExtra(
            "productsIds",
            productsList
        )
        intent.putExtra("startTime", eventListResponse?.get(position)?.startTime)
        intent.putExtra("endTime", eventListResponse?.get(position)?.endTime)
        intent.putExtra(
            "conversationId",
            eventListResponse?.get(position)?.metaData?.conversationId
        )
        intent.putExtra("eventName", eventListResponse?.get(position)?.title)
        startActivityForResult(intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                for (i in eventListResponse?.indices!!) {
                    if (eventListResponse?.get(i)?.id == data?.getStringExtra("broadCastId")) {
                        eventListResponse?.removeAt(i)
                        break
                    }
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