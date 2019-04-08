package ru.climatlab.service.ui.requestsList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.request_list_item.view.*
import ru.climatlab.service.R
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.data.model.RequestType

class RequestsRecyclerViewAdapter(
    private var requestItems: MutableList<RequestModel>,
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

    fun updateDataSet(orders: List<RequestModel>) {
        val lastItem = requestItems.size
        requestItems.addAll(orders)
        notifyItemRangeInserted(lastItem, requestItems.size)
    }

    interface InteractionListener {
        fun onClick(request: RequestModel)
    }

    class RequestViewHolder(itemView: View, private val interactionListener: InteractionListener) :
        RecyclerView.ViewHolder(itemView) {

        lateinit var request: RequestModel

        fun bindItem(request: RequestModel) {
            this.request = request
            itemView.clientFullNameTextView.text = request.clientId
            itemView.officeTitleNameTextView.text = request.office
            itemView.equipmentTextView.text = request.equipmentId
            itemView.addressTextView.text = request.address
            itemView.typeTextView.text = when(request.type){
                RequestType.Mounting -> itemView.context.getString(R.string.request_type_mounting)
                RequestType.Service -> itemView.context.getString(R.string.request_type_service)
                RequestType.OrderEquipment -> itemView.context.getString(R.string.request_type_order)
            }
            itemView.setOnClickListener { interactionListener.onClick(request) }
        }
    }
}
