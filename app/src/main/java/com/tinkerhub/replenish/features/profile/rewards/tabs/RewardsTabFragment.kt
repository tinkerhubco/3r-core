package com.tinkerhub.replenish.features.profile.rewards.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tinkerhub.replenish.common.utils.Event
import com.tinkerhub.replenish.common.utils.autoCleared
import com.tinkerhub.replenish.data.adapters.RewardItemAdapter
import com.tinkerhub.replenish.data.models.RewardItem
import com.tinkerhub.replenish.databinding.FragmentRewardsTabBinding
import com.tinkerhub.replenish.features.profile.rewards.RewardsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RewardsTabFragment : Fragment(), RewardItemAdapter.RewardItemListener {
    
    companion object {
        private const val IS_REDEEMED_TAB_ARG = "isRedeemedTabArg"
        
        fun getFragment(isRedeemedTab: Boolean): RewardsTabFragment {
            return RewardsTabFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_REDEEMED_TAB_ARG, isRedeemedTab)
                }
            }
        }
    }
    
    private var binding: FragmentRewardsTabBinding by autoCleared()
    private val rewardsViewModel: RewardsViewModel by activityViewModels()
    private val viewModel: RewardsTabViewModel by viewModels()
    private lateinit var rewardItemAdapter: RewardItemAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRewardsTabBinding.inflate(inflater, container, false)
        viewModel.isRedeemedTab = arguments?.getBoolean(IS_REDEEMED_TAB_ARG) ?: false
        rewardItemAdapter = RewardItemAdapter(this, isRedeemed = viewModel.isRedeemedTab)
        
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        rewardsViewModel.availableRewardsList.observe(viewLifecycleOwner) {
            if (viewModel.isRedeemedTab) return@observe
            rewardItemAdapter.submitList(it)
        }
        
        rewardsViewModel.redeemedRewardsList.observe(viewLifecycleOwner) {
            if (!viewModel.isRedeemedTab) return@observe
            rewardItemAdapter.submitList(it)
        }
        
        binding.textviewRewardForYou.isVisible = !viewModel.isRedeemedTab
        
        binding.recyclerViewRewards.apply {
            adapter = rewardItemAdapter
            layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        }
        
        binding.layoutSwipeToRefresh.setOnRefreshListener {
            viewModel.viewModelScope.launch {
                rewardsViewModel.loadRewards(redeemed = viewModel.isRedeemedTab)
                binding.layoutSwipeToRefresh.isRefreshing = false
            }
        }
    }
    
    override fun onRewardItemClicked(item: RewardItem) {
        rewardsViewModel.selectedRewardItem.value = Event(item)
    }
}