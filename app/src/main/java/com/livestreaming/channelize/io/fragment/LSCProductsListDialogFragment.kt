package com.livestreaming.channelize.io.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.activity.lscSettingUp.LSCBroadCastSettingUpAndLiveActivity
import com.livestreaming.channelize.io.adapter.LSCBroadcastProductsListingAdapter

class LSCProductsListDialogFragment : BottomSheetDialogFragment() {


    private lateinit var cancelProductsList: ImageView
    private lateinit var productsHeaderTextView: TextView
    private lateinit var productListRecyclerView: RecyclerView
    private lateinit var noProductsTextView: TextView


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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cancelProductsList = view.findViewById(R.id.cancelProductsList)
        productsHeaderTextView = view.findViewById(R.id.productsHeaderTextView)
        productListRecyclerView = view.findViewById(R.id.productListRecyclerView)
        noProductsTextView = view.findViewById(R.id.noProductsTextView)
        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        activity?.let {
            val listOfProducts =
                (it as LSCBroadCastSettingUpAndLiveActivity).getProductsListData()
            productListRecyclerView.layoutManager = LinearLayoutManager(it)
            productListRecyclerView.adapter = productsItemAdapter
            listOfProducts?.let {
                productsItemAdapter.setListData(listOfProducts)
                productsItemAdapter.notifyDataSetChanged()
            } ?: run {
                productsItemAdapter.setListData(null)
                productsItemAdapter.notifyDataSetChanged()

            }

        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }


    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

}