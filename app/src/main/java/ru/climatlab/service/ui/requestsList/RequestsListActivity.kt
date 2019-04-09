package ru.climatlab.service.ui.requestsList

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_requests_list.*
import org.jetbrains.anko.intentFor
import ru.climatlab.service.R
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.ui.BaseActivity
import ru.climatlab.service.ui.requestDetailsInfo.RequestDetailsActivity

class RequestsListActivity : BaseActivity(), RequestsListView {

    @InjectPresenter
    lateinit var presenter: RequestsListPresenter

    private lateinit var requestsAdapter: RequestsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests_list)
        requestsRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        requestsAdapter =
            RequestsRecyclerViewAdapter(mutableListOf(), object : RequestsRecyclerViewAdapter.InteractionListener {
                override fun onClick(request: RequestModel) {
                    presenter.onRequestClick(request)
                }
            })
        requestsRecyclerView.adapter = requestsAdapter
    }

    override fun updateData(requests: List<RequestModel>) {
        requestsAdapter.updateDataSet(requests)
    }

    override fun showRequestDetailsScreen(request: RequestModel) {
        startActivity(intentFor<RequestDetailsActivity>(RequestDetailsActivity.EXTRA_KEY_REQUEST_ID to request.id))
    }

    override fun showRequestReportScreen(request: RequestModel) {

    }
}
