package ru.climatlab.service.ui.requestsList

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_requests_list.*
import org.jetbrains.anko.intentFor
import ru.climatlab.service.R
import ru.climatlab.service.data.model.Request
import ru.climatlab.service.data.model.RequestStatus
import ru.climatlab.service.ui.BaseActivity
import ru.climatlab.service.ui.requestDetailsInfo.RequestDetailsActivity
import ru.climatlab.service.ui.requestReport.RequestReportActivity

class RequestsListActivity : BaseActivity(), RequestsListView {

    companion object {
        /** Must be one of {@link RequestStatus} or empty if filter disabled*/
        const val EXTRA_REQUESTS_FILTER = "EXTRA_REQUESTS_FILTER"
    }

    @InjectPresenter
    lateinit var presenter: RequestsListPresenter

    private lateinit var requestsAdapter: RequestsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests_list)
        requestsRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        requestsAdapter =
            RequestsRecyclerViewAdapter(mutableListOf(), object : RequestsRecyclerViewAdapter.InteractionListener {
                override fun onClick(request: Request) {
                    presenter.onRequestClick(request)
                }
            })
        requestsRecyclerView.adapter = requestsAdapter
        val requestFilter = intent.getSerializableExtra(EXTRA_REQUESTS_FILTER) as RequestStatus?
        presenter.onAttach(requestFilter)
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun updateData(requests: List<Request>) {
        requestsAdapter.updateDataSet(requests)
    }

    override fun showRequestDetailsScreen(request: Request) {
        startActivity(intentFor<RequestDetailsActivity>(RequestDetailsActivity.EXTRA_KEY_REQUEST_ID to request.requestInfo.id))
    }

    override fun showRequestReportScreen(request: Request) {
        startActivity(intentFor<RequestReportActivity>(RequestDetailsActivity.EXTRA_KEY_REQUEST_ID to request.requestInfo.id))
    }
}
