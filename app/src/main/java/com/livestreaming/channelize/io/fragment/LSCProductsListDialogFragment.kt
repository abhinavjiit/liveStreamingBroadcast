package com.livestreaming.channelize.io.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.livestreaming.channelize.io.LiveBroadcasterConstants
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCLiveBroadCastViewModel
import com.livestreaming.channelize.io.adapter.LSCBroadcastProductsListingAdapter
import com.livestreaming.channelize.io.model.productdetailModel.ProductDetailResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import kotlinx.android.synthetic.main.bottom_sheet_fragment_product_list_layout.*
import kotlinx.android.synthetic.main.bottom_sheet_fragment_product_list_layout.view.*

class LSCProductsListDialogFragment : BottomSheetDialogFragment() {

    private val productsList = ArrayList<ProductDetailResponse>()
    private lateinit var lscBroadCastViewModel: LSCLiveBroadCastViewModel
    private var productsIds: ArrayList<String>? = null
    private  lateinit var mContext:Context

    private val productsItemAdapter: LSCBroadcastProductsListingAdapter by lazy {
            LSCBroadcastProductsListingAdapter(mContext,productsList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_fragment_product_list_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productsIds = arguments?.getStringArrayList(LiveBroadcasterConstants.EVENT_PRODUCT_IDS)
        setupRecyclerView(view)
        iniViewModel()
        ivCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupRecyclerView(view: View) {
        activity?.let { activity ->
            mContext=activity
            view.rvProductList.layoutManager = LinearLayoutManager(activity)
            view.rvProductList.adapter = productsItemAdapter
            productsItemAdapter.notifyDataSetChanged()
        }
    }

    private fun iniViewModel() {
        lscBroadCastViewModel = ViewModelProvider(requireActivity()).get(LSCLiveBroadCastViewModel::class.java)
        getProductsList()
    }

    private fun getProductsList() {
        val productsIdsCommaSeparated: String? = productsIds?.let { productsId ->
            var id = ""
            productsId.forEach { productIds ->
                id = if (id.isBlank()) {
                    id.plus(productIds)
                } else {
                    id.plus(",$productIds")
                }
            }
            id
        }
        lscBroadCastViewModel.getProductItems(productsIdsCommaSeparated)
            .observe(requireActivity(), Observer { productItemsResource ->
                when (productItemsResource?.status) {
                    Resource.Status.SUCCESS -> {
                        productItemsResource.data?.let { productItemsRes ->
                            if (productItemsRes.statusCode == LiveBroadcasterConstants.SUCCESS_CODE && productItemsRes.success == LiveBroadcasterConstants.SUCCESS) {
                                productItemsRes.data.products?.let { itemList ->
                                    productsList.addAll(itemList)
                                    if (productsList.isEmpty()) {
                                        rvProductList.visibility = View.GONE
                                        view?.tvNoProducts?.visibility = View.VISIBLE
                                        progressBar.visibility = View.GONE
                                    } else {
                                        rvProductList.visibility = View.VISIBLE
                                        view?.tvNoProducts?.visibility = View.GONE
                                        progressBar.visibility = View.GONE
                                        productsItemAdapter.notifyDataSetChanged()
                                    }
                                } ?: run {
                                    rvProductList.visibility = View.GONE
                                    view?.tvNoProducts?.visibility = View.VISIBLE
                                    progressBar.visibility = View.GONE
                                }
                            }
                        }
                    }
                    Resource.Status.ERROR -> {
                        rvProductList.visibility = View.GONE
                        view?.tvNoProducts?.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    }
                }
            })
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

}