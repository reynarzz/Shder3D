package com.reynarz.minityeditor.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.reynarz.minityeditor.R

class SceneFragmentView : Fragment(R.layout.scene_view_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_openHierarchy).setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().apply {

                replace(R.id.mainFragment, HierarchyFragmentView())
                commit()
            }
        }
    }
}