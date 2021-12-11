package com.example.chatting_program


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting_program.databinding.ItemLeftBinding
import com.example.chatting_program.databinding.ItemRightBinding

import com.example.chatting_program.recycler.DiffUtilCallback
import com.example.chatting_program.recycler.MessageData
import java.security.CodeSource

class ChatProgramAdapter(val messageList: MutableList<MessageData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class LeftViewHolder(val binding: ItemLeftBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(messageList: MessageData) {
            messageList.message = binding.tvMessage.text.toString()
        }
    }

    class RightViewHolder(val binding: ItemRightBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(messageList: MessageData) {
            messageList.message = binding.tvMessage.text.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == RIGHT_CONTENT) {
            val view = ItemRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return RightViewHolder(view)
        } else {
            val view = ItemLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LeftViewHolder(view)
        }
    }

    override fun getItemCount() = messageList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RightViewHolder) {
            holder.bind(messageList[position])
        } else if (holder is LeftViewHolder) {
            holder.bind(messageList[position])
        }
    }

    companion object {
        val RIGHT_CONTENT = 1
        val LEFT_CONTENT = 2
    }

}

