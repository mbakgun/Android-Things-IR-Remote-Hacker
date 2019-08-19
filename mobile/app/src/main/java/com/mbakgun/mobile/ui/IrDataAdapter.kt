package com.mbakgun.mobile.ui

/**
 * Created by burakakgun on 9.06.2019.
 */
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mbakgun.mobile.R
import com.mbakgun.mobile.data.IrData
import com.mbakgun.mobile.data.NearbyMessage
import com.mbakgun.mobile.data.NearbyType
import com.mbakgun.mobile.databinding.EmptyItemBinding
import com.mbakgun.mobile.databinding.IrItemBinding
import com.mbakgun.mobile.util.showAlertWithTextInputLayout
import javax.inject.Inject

class IrDataAdapter @Inject constructor(private val vm: MainActivityVM) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val irDataList = mutableListOf<IrData?>()

    companion object {
        const val EMPTY = 0
        const val IR_ITEM = 1
    }

    fun updateList(list: List<IrData>) {
        Log.d("IrDataAdapter", "current list: $list")
        irDataList.clear()
        if (list.isEmpty()) {
            irDataList.add(null)
        } else {
            irDataList.addAll(list)
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = if (irDataList[0] == null) EMPTY else IR_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            IR_ITEM -> {
                val binding = inflate<IrItemBinding>(
                    inflater, R.layout.ir_item, parent, false
                )
                IrViewHolder(binding)
            }
            else -> { // EMPTY
                val binding = inflate<EmptyItemBinding>(
                    inflater, R.layout.empty_item, parent, false
                )
                EmptyViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            EMPTY -> holder as EmptyViewHolder
            IR_ITEM -> {
                irDataList[position]?.let {
                    holder as IrViewHolder
                    holder.bind(it)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return irDataList.size
    }

    inner class IrViewHolder(private val binding: IrItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: IrData) {
            binding.root.setOnClickListener {
                vm.send(NearbyMessage(NearbyType.MESSAGE, "send:${item.hexCode}"))
            }
            binding.imageViewDelete.setOnClickListener {
                vm.send(NearbyMessage(NearbyType.DELETE, Gson().toJson(item)))
            }
            binding.imageViewEdit.setOnClickListener {
                showAlertWithTextInputLayout(binding.root.context, vm, item)
            }
            binding.item = item
            binding.executePendingBindings()
        }
    }

    inner class EmptyViewHolder(binding: EmptyItemBinding) : RecyclerView.ViewHolder(binding.root)
}
