package com.example.chatting_program.recycler

import androidx.recyclerview.widget.DiffUtil

class DiffUtilCallback : DiffUtil.ItemCallback<MessageData>() {
    override fun areItemsTheSame(
        oldItem: MessageData,
        newItem: MessageData
    ) =
        (oldItem.message == newItem.message)

    override fun areContentsTheSame(
        oldItem: MessageData,
        newItem: MessageData
    ) =
        (oldItem == newItem)
}