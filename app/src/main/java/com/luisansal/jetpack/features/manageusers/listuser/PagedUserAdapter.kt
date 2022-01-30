package com.luisansal.jetpack.features.manageusers.listuser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.luisansal.jetpack.databinding.ItemUserListBinding
import com.luisansal.jetpack.core.domain.entity.UserEntity

class PagedUserAdapter : PagedListAdapter<UserEntity, PagedUserAdapter.PagingUserViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingUserViewHolder {
        val binding = ItemUserListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagingUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PagingUserViewHolder, position: Int) {
        val user = getItem(position)
        user?.let { holder.bind(it) }
    }

    inner class PagingUserViewHolder(private val binding: ItemUserListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: UserEntity) = with(binding) {
            tvName.text = "${model.names} ${model.lastNames}"
            tvDni.text = model.dni
        }
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserEntity>() {
            // Concert details may have changed if reloaded from the database,
            // but ID is fixed.
            override fun areItemsTheSame(oldConcert: UserEntity, newConcert: UserEntity): Boolean {
                return oldConcert.id == newConcert.id
            }

            override fun areContentsTheSame(
                oldConcert: UserEntity,
                newConcert: UserEntity
            ): Boolean {
                return oldConcert == newConcert
            }
        }
    }
}


