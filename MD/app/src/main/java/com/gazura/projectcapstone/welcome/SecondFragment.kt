package com.gazura.projectcapstone.welcome

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.gazura.projectcapstone.R
import com.gazura.projectcapstone.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {
    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val moveAnimation = ObjectAnimator.ofFloat(binding.ivWelcome, "translationX", 0f, 100f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        moveAnimation.start()

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        binding.btnNext.setOnClickListener {
            viewPager?.currentItem = 2
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}