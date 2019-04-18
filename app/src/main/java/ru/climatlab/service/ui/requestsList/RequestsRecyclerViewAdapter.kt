package ru.climatlab.service.ui.requestsList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.request_list_item.view.*
import ru.climatlab.service.R
import ru.climatlab.service.data.model.Request
import ru.climatlab.service.data.model.RequestResponseModel
import ru.climatlab.service.data.model.RequestType

class RequestsRecyclerViewAdapter(
    private var requestItems: MutableList<Request>,
    private val interactionListener: InteractionListener
) : RecyclerView.Adapter<RequestsRecyclerViewAdapter.RequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.request_list_item, parent, false)
        return RequestViewHolder(view, interactionListener)
    }

    override fun getItemCount(): Int {
        return requestItems.size
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bindItem(requestItems[position])
    }

    fun updateDataSet(requests: List<Request>) {
        requestItems.clear()
        requestItems.addAll(requests)
        notifyDataSetChanged()
    }

    interface InteractionListener {
        fun onClick(request: Request)
    }

    class RequestViewHolder(itemView: View, private val interactionListener: InteractionListener) :
        RecyclerView.ViewHolder(itemView) {

        lateinit var request: Request

        fun bindItem(request: Request) {
            this.request = request
            itemView.clientFullNameTextView.text = request.clientResponseModel.fullName()
            itemView.officeTitleNameTextView.text = request.requestInfo.office
            itemView.equipmentTextView.text = request.requestInfo.equipmentId
            itemView.addressTextView.text = request.requestInfo.address
            itemView.typeTextView.text = request.requestInfo.type?.title?:""
            itemView.setOnClickListener { interactionListener.onClick(request) }
        }
    }
}
