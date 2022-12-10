package com.example.gps_shadow_tracker_app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import com.example.gps_shadow_tracker_app.databinding.ActivityMainBinding

class NewUILocationText: Fragment() {

    private var _binding: ActivityMainBinding? = null;

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        _binding = ActivityMainBinding.inflate(inflater, container, false);
        val view = binding.root;
        binding.gameLabels.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    Text("Compose App")
                }
            }
        }
        return view;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}