package ru.climatlab.service.ui.requestsList

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.request_list_item.view.*
import ru.climatlab.service.R
import ru.climatlab.service.data.model.Request
import java.text.SimpleDateFormat
import java.util.*

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
            itemView.descriptionTextView.text = request.requestInfo.description
            itemView.commentTextView.text = request.requestInfo.comment
            itemView.dateTextView.text = SimpleDateFormat("dd.MM hh:mm", Locale.getDefault()).format(Date(request.requestInfo.date))
            itemView.contactTextView.text = if (request.clientResponseModel.reserveContact.isNotBlank()) request.clientResponseModel.reserveContact else itemView.context.getString(R.string.empty)
            itemView.contractDateAndNumberTextView.text = "${request.clientResponseModel.contractDate} №${request.clientResponseModel.contractNumber}"
            itemView.addressTextView.text = """${request.requestInfo.address}
                |${request.requestInfo.addressDetails}
            """.trimMargin()
            itemView.setOnClickListener { interactionListener.onClick(request) }
            itemView.phoneNumber.text = request.clientResponseModel.phone
            itemView.callButton.setOnClickListener {
                itemView.context.startActivity(
                    Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:8${request.clientResponseModel.phone}")
                    )
                )
            }
            itemView.buildRouteButton.setOnClickListener {
                itemView.context.startActivity(
                    Intent.createChooser(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q=${request.requestInfo.getCoordinates()?.latitude},${request.requestInfo.getCoordinates()?.longitude}(${request.requestInfo.address})")
                        ), itemView.context.getString(R.string.map_chooser_title)
                    )
                )
            }

        }
    }
}
