package com.ilyatoritsyn.petside;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.View;
import android.widget.Toast;

import com.ilyatoritsyn.petside.viewmodel.UserViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FeedFragment extends Fragment {
    private UserViewModel userViewModel;

    public FeedFragment() {
        super(R.layout.fragment_feed);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        userViewModel.getIsApiKey().observe(getViewLifecycleOwner(), isApiKey -> {
            if (!isApiKey) {
                Navigation
                        .findNavController(view)
                        .navigate(FeedFragmentDirections.actionFeedFragmentToAuthGraph());
            } else {
                showFeed();
            }
        });

        if (userViewModel.getIsApiKey().getValue() == null) {
            userViewModel.checkApiKeyAvailability();
        }
    }

    private void showFeed() {
        Toast.makeText(getContext(), "Placeholder as showing the main fragment", Toast.LENGTH_LONG).show();
    }
}
