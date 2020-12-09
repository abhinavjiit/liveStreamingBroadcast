package com.livestreaming.channelize.io.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
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

    private var productsList: ArrayList<ProductDetailResponse>? = null
    private lateinit var lscBroadCastViewModel: LSCLiveBroadCastViewModel
    private var productsIds: ArrayList<String>? = null

    private val productsItemAdapter: LSCBroadcastProductsListingAdapter by lazy {
        LSCBroadcastProductsListingAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.bottom_sheet_fragment_product_list_layout,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ConstraintLayout>(R.id.root).maxHeight =
            (resources.displayMetrics.heightPixels * 0.5).toInt()
        productsIds = arguments?.getStringArrayList(LiveBroadcasterConstants.EVENT_PRODUCT_IDS)
        setupRecyclerView(view)
        iniViewModel()
        cancelProductsList.setOnClickListener {
            dismiss()
        }
    }

    private fun setupRecyclerView(view: View) {
        activity?.let { activity ->
            view.productListRecyclerView.layoutManager = LinearLayoutManager(activity)
            view.productListRecyclerView.adapter = productsItemAdapter
            productsItemAdapter.setListData(null)
            productsItemAdapter.notifyDataSetChanged()
        }
    }

    private fun iniViewModel() {
        lscBroadCastViewModel =
            ViewModelProvider(requireActivity()).get(LSCLiveBroadCastViewModel::class.java)
        getProductsList()
    }

    private fun getProductsList() {
        val productsIdsCommaSeparated: String? = productsIds?.let { productsId ->
            var id = ""
            productsId.forEach { productIds ->
                id = if (id.isBlank())
                    id.plus(productIds)
                else
                    id.plus(",$productIds")
            }
            id
        }
        lscBroadCastViewModel.getProductItems(productsIdsCommaSeparated)
            .observe(requireActivity(), Observer { productItemsResource ->
                when (productItemsResource?.status) {
                    Resource.Status.SUCCESS -> {
                        productItemsResource.data?.let { productItemsRes ->
                            if (productItemsRes.statusCode == 200 && productItemsRes.success == "OK") {
                                productItemsRes.data.products?.let { itemList ->
                                    productsList =
                                        itemList.toMutableList() as ArrayList<ProductDetailResponse>
                                    if (itemList.isEmpty()) {
                                        productListRecyclerView.visibility = View.GONE
                                        view?.noProductsTextView?.visibility = View.VISIBLE
                                        progressBar.visibility = View.GONE

                                    } else {
                                        productListRecyclerView.visibility = View.VISIBLE
                                        view?.noProductsTextView?.visibility = View.GONE
                                        progressBar.visibility = View.GONE
                                        productsItemAdapter.setListData(itemList)
                                        productsItemAdapter.notifyDataSetChanged()
                                    }
                                } ?: run {
                                    productListRecyclerView.visibility = View.GONE
                                    view?.noProductsTextView?.visibility = View.VISIBLE
                                    progressBar.visibility = View.GONE
                                }
                            }
                        }
                    }
                    Resource.Status.ERROR -> {
                        productsList = null
                        productListRecyclerView.visibility = View.GONE
                        view?.noProductsTextView?.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    }
                    Resource.Status.LOADING -> {
                        Log.d("ProductItemResponse", "Loading")
                    }
                }
            })
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

}