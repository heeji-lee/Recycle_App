package com.appliances.recycle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // 버튼 찾기 및 클릭 리스너 설정
        val requestPickupButton: Button = view.findViewById(R.id.btnRequestPickup)
        requestPickupButton.setOnClickListener {
            navigateToFragment(ProductFragment(), R.id.navigation_camera)
        }

        val noticeButton: Button = view.findViewById(R.id.btnNotice)
        noticeButton.setOnClickListener {
            navigateToFragment(NoticeListFragment(), R.id.navigation_alert)
        }

        val statusButton: Button = view.findViewById(R.id.btnStatus)
        statusButton.setOnClickListener {
            navigateToFragment(ProductListFragment(), R.id.navigation_pickup)
        }

        return view
    }

    private fun navigateToFragment(fragment: Fragment, bottomNavItemId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_home, fragment)  // 프래그먼트를 담을 컨테이너 ID
            .addToBackStack(null)  // 뒤로가기 버튼으로 이전 프래그먼트로 돌아갈 수 있도록 함
            .commit()

        // BottomNavigationView 상태 변경
        updateBottomNavigation(bottomNavItemId)
    }

    // BottomNavigationView의 활성 상태를 변경하는 함수
    private fun updateBottomNavigation(menuItemId: Int) {
        val bottomNavigationView: BottomNavigationView? = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNavigationView?.selectedItemId = menuItemId
    }

}