package com.livestreaming.channelize.io.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.livestreaming.channelize.io.ImageLoader
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.model.EventDetailResponse
import kotlinx.android.synthetic.main.adapter_event_broadcast_item_layout.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class EventsBroadCastListingAdapter(
    private val context: Context,
    private val listenerI: IRecyclerViewClickListener,
    private val eventsList: ArrayList<EventDetailResponse>?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_event_broadcast_item_layout, parent, false)
        return ViewHolder(view, listenerI)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.apply {
                eventNameTextView.text = eventsList?.get(position)?.title
                try {
                    ImageLoader.showImage(eventsList?.get(position)?.bannerImageUrl, eventImageView)
                } catch (e: Exception) {
                    Log.d("EventListingAdapterEx", e.toString())
                }
                eventStatus.text = eventsList?.get(position)?.status?.capitalize()
                eventStatus.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.upcoming_event_color_code
                    )
                )
                holder.eventStatus.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.dark_grey
                    )
                )
                printDifferenceDateForHours(
                    holder,
                    startingInDateCounter,
                    eventsList?.get(position)?.startTime
                )
                try {
                    startDateTextView.text =
                        context.getString(R.string.start_event_date_string)
                            .plus(" " + eventsList?.get(position)?.startTime?.changeGMTtoIST())
                    endDateTextView.text =
                        context.getString(R.string.end_event_date_string)
                            .plus(" " + eventsList?.get(position)?.endTime?.changeGMTtoIST())

                } catch (e: Exception) {
                    Log.d("EventListAdapterEx", e.toString())
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return eventsList?.size ?: 0
    }

    class ViewHolder(mView: View, private val listenerI: IRecyclerViewClickListener) :
        RecyclerView.ViewHolder(mView) {
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
        internal val card: CardView = mView.card

        init {
            goLiveButton.setOnClickListener {
                listenerI.onClick(adapterPosition)
            }
            card.setOnClickListener {
                listenerI.onClick(adapterPosition)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun printDifferenceDateForHours(
        holder: ViewHolder,
        dateCounter: TextView,
        startTime: String?
    ) {
        startTime?.let { eventStartTime ->
            holder.countDownTimer?.cancel()
            dateCounter.visibility = View.VISIBLE
            holder.startingInTextView.visibility = View.VISIBLE
            holder.goLiveButton.visibility = View.GONE
            holder.startBroadCastStatus.visibility = View.GONE
            holder.card.isEnabled = false
            holder.card.isClickable = false
            val outputFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            outputFormat.timeZone = TimeZone.getTimeZone("IST")
            val timeFormat = outputFormat.parse(eventStartTime)
            val currentTime = Calendar.getInstance().time
            val different: Long? = timeFormat?.time?.minus(currentTime.time)
            different?.let { timeDiff ->
                holder.countDownTimer = object : CountDownTimer(timeDiff, 1000) {
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
                        var timeValue = ""
                        if (elapsedDays > 0) {
                            timeValue = timeValue.plus("$elapsedDays days ")
                        }
                        if (elapsedHours > 0 || elapsedDays > 0) {
                            timeValue = timeValue.plus("$elapsedHours hs ")
                        }

                        if (elapsedMinutes > 0 || elapsedDays > 0 || elapsedHours > 0) {
                            timeValue = timeValue.plus("$elapsedMinutes min ")
                        }
                        dateCounter.text =
                            "$timeValue $elapsedSeconds sec"
                    }

                    override fun onFinish() {
                        dateCounter.visibility = View.GONE
                        holder.startingInTextView.visibility = View.GONE
                        holder.goLiveButton.visibility = View.VISIBLE
                        holder.startBroadCastStatus.visibility = View.VISIBLE
                        holder.eventStatus.text = context.getString(R.string.live_event_string)
                        holder.eventStatus.setBackgroundResource(
                            R.drawable.available_events_status_backgroung
                        )
                        holder.card.isClickable = true
                        holder.card.isEnabled = true
                    }
                }.start()
            }
        }
    }

    interface IRecyclerViewClickListener {
        fun onClick(position: Int)
    }

}

@SuppressLint("SimpleDateFormat")
fun String.changeGMTtoIST(): String {
    val utcFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    utcFormat.timeZone = TimeZone.getTimeZone("GMT")
    val outputFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    outputFormat.timeZone = TimeZone.getTimeZone("IST")
    val timeFormat = outputFormat.parse(this)
    timeFormat?.let { formatTime ->
        val day = SimpleDateFormat("dd MMM").format(formatTime)
        val time = SimpleDateFormat("H:mm a").format(formatTime)
        return day.plus(", $time")
    } ?: run {
        return ""
    }
}