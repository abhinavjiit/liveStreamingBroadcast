package com.livestreaming.channelize.io.adapter

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.model.EventDetailResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adapter_event_broadcast_item_layout.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class EventsBroadCastListingAdapter(val listener: RecyclerViewClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var eventsList: List<EventDetailResponse>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_event_broadcast_item_layout, parent, false)
        return ViewHolder(view, listener)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.apply {

                eventNameTextView.text = eventsList?.get(position)?.title

                try {
                    Picasso.get().load(eventsList?.get(position)?.bannerImageUrl?.trim())
                        .into(eventImageView)
                } catch (e: Exception) {

                }
                eventStatus.text = eventsList?.get(position)?.status

                try {
                    startDateTextView.text =
                        "start:".plus(changeGMTtoIST(eventsList?.get(position)?.startTime!!))
                    endDateTextView.text =
                        "end:".plus(changeGMTtoIST(eventsList?.get(position)?.endTime!!))

                    printDifferenceDateForHours(
                        holder,
                        startingInDateCounter,
                        eventsList?.get(position)?.startTime!!,
                        eventsList?.get(position)?.endTime!!
                    )

                } catch (e: Exception) {
/////////////////////////////////////////////////////////////////////////
                }


            }

        }

    }


    override fun getItemCount(): Int {
        return if (eventsList.isNullOrEmpty()) 0 else
            eventsList?.size!!
    }

    fun setEventList(listData: List<EventDetailResponse>?) {
        this.eventsList = listData
    }


    class ViewHolder(mView: View, private val listener: RecyclerViewClickListener) :
        RecyclerView.ViewHolder(mView), View.OnClickListener {
        var countDownTimer: CountDownTimer? = null
        val eventImageView: ImageView = mView.eventImageView
        val eventStatus: TextView = mView.eventStatus
        val eventNameTextView: TextView = mView.eventNameTextView
        val startDateTextView: TextView = mView.startDateTextView
        val endDateTextView: TextView = mView.endDateTextView
        val startingInDateCounter: TextView = mView.startingInDateCounter
        val startingInTextView: TextView = mView.startingInTextView
        internal val goLiveButton: TextView = mView.goLiveButton
        val startBroadCastStatus: TextView = mView.startBroadCastStatus
        private val card: CardView = mView.card

        init {
            goLiveButton.setOnClickListener(this)
            card.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onClick()
        }


    }

    @SuppressLint("SimpleDateFormat")
    private fun changeGMTtoIST(date: String): String? {


        val utcFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        utcFormat.timeZone = TimeZone.getTimeZone("GMT")


        val outputFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

        outputFormat.timeZone = TimeZone.getTimeZone("IST")
        val timeFormate = outputFormat.parse(date)

        val day = SimpleDateFormat("dd MMM").format(timeFormate!!)
        val time = SimpleDateFormat("H:mm a").format(timeFormate)

        return day.plus(", $time")
    }


    @SuppressLint("SimpleDateFormat")
    private fun printDifferenceDateForHours(
        holder: ViewHolder,
        dateCounter: TextView,
        startTime: String,
        endTime: String
    ) {
        if (holder.countDownTimer != null) {
            holder.countDownTimer?.cancel()
        }


        val outputFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val timeFormate = outputFormat.parse(startTime)
        outputFormat.timeZone = TimeZone.getTimeZone("IST")

        val currentTime = Calendar.getInstance().time
        val startDay = SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(timeFormate!!)
        val format1 = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault())
        val startTime = format1.parse(startDay)


        val different = startTime?.time!! - currentTime.time
        holder.countDownTimer = object : CountDownTimer(different, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                var diff = millisUntilFinished
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60
                val daysInMilli = hoursInMilli * 24

                val elapsedDays = diff / daysInMilli
                diff %= daysInMilli

                val elapsedHours = diff / hoursInMilli
                diff %= hoursInMilli

                val elapsedMinutes = diff / minutesInMilli
                diff %= minutesInMilli

                val elapsedSeconds = diff / secondsInMilli

                dateCounter.text =
                    "$elapsedDays days $elapsedHours hs $elapsedMinutes min $elapsedSeconds sec"

            }

            override fun onFinish() {
                dateCounter.visibility = View.GONE
                holder.startingInTextView.visibility = View.GONE
                holder.goLiveButton.visibility = View.VISIBLE
                holder.startBroadCastStatus.visibility = View.VISIBLE
                holder.eventStatus.text = "Available"


            }
        }.start()

    }


    interface RecyclerViewClickListener {
        fun onClick()
    }
}