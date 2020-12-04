package com.livestreaming.channelize.io.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadCastSettingUpAndLiveActivity
import com.livestreaming.channelize.io.adapter.LSCBroadcastProductsListingAdapter
import kotlinx.android.synthetic.main.bottom_sheet_fragment_product_list_layout.*

class LSCProductsListDialogFragment : BottomSheetDialogFragment() {

    private val productsItemAdapter: LSCBroadcastProductsListingAdapter by lazy {
        LSCBroadcastProductsListingAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.bottom_sheet_fragment_product_list_layout, container, false)
        view.findViewById<ConstraintLayout>(R.id.root).maxHeight =
            (resources.displayMetrics.heightPixels * 0.5).toInt()
        setupRecyclerView()
        cancelProductsList.setOnClickListener {
            dismiss()
        }
        return view
    }

    private fun setupRecyclerView() {
        activity?.let {activity->
            val listOfProducts =
                (activity as LSCBroadCastSettingUpAndLiveActivity).getProductsListData()
            productListRecyclerView.layoutManager = LinearLayoutManager(activity)
            productListRecyclerView.adapter = productsItemAdapter
            listOfProducts?.let {productList->
                if (productList.isEmpty()) {
                    productListRecyclerView.visibility = View.GONE
                    noProductsTextView.visibility -= View.VISIBLE

                } else {
                    productListRecyclerView.visibility = View.VISIBLE
                    noProductsTextView.visibility -= View.GONE
                    productsItemAdapter.setListData(listOfProducts)
                    productsItemAdapter.notifyDataSetChanged()
                }
            } ?: run {
                productListRecyclerView.visibility = View.GONE
                noProductsTextView.visibility -= View.VISIBLE
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

}