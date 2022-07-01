package com.ilyatoritsyn.petside;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.ilyatoritsyn.petside.viewmodel.UserViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthApiKeyFragment extends Fragment {
    private UserViewModel userViewModel;

    private ImageButton upButton;
    private TextInputLayout apiKeyField;    // Поле для ввода ключика API
    private Button nextButton;

    private boolean isApiKeyValidationResultShown;
    private boolean isApiKeyValid;

    private long mLastClickTime = 0;

    public AuthApiKeyFragment() {
        super(R.layout.fragment_auth_api_key);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("UnsafeOptInUsageWarning")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        if (getContext() != null) {
            upButton = view.findViewById(R.id.up_button_auth_api_key);
            apiKeyField = view.findViewById(R.id.api_key_field_auth_api_key);
            nextButton = view.findViewById(R.id.next_button_auth_api_key);

            int validFieldColor = ContextCompat.getColor(getContext(), R.color.green_2);

            upButton.setOnClickListener(buttonView -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < MainActivity.clickDelay) {
                    return;
                }

                mLastClickTime = SystemClock.elapsedRealtime();

                Navigation
                        .findNavController(buttonView)
                        .navigateUp();
            });

            if (apiKeyField != null && apiKeyField.getEditText() != null) {
                apiKeyField.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // поле ввода остаётся красным до тех пор,
                        // пока пользователь не введёт корректное значение
                        if (s.toString().trim().isEmpty()) {
                            isApiKeyValid = false;

                            if (isApiKeyValidationResultShown) {
                                apiKeyField.setError(getString(R.string.invalid_api_key_message));
                            }
                        } else {
                            isApiKeyValidationResultShown = true;
                            isApiKeyValid = true;

                            // Как только пользователь вводит валидный ключ API,
                            // поле ввода подсвечено зелёным
                            apiKeyField.setErrorEnabled(false);
                            apiKeyField.setBoxStrokeColor(validFieldColor);
                            apiKeyField.setHintTextColor(ColorStateList.valueOf(validFieldColor));
                        }

                        // Кнопка становится активна,
                        // как только введён непустой ключ API
                        nextButton.setEnabled(isApiKeyValid);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                apiKeyField.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
                    // Если пользователь ввёл некорректный API ключ и клацнул вне поля,
                    // то поле подсвечивается красным
                    if (!hasFocus && !isApiKeyValid) {
                        isApiKeyValidationResultShown = true;

                        apiKeyField.setError(getString(R.string.invalid_api_key_message));
                    }
                });

                nextButton.setOnClickListener(buttonView -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < MainActivity.clickDelay) {
                        return;
                    }

                    mLastClickTime = SystemClock.elapsedRealtime();

                    userViewModel
                            .checkApiKeyServiceability(
                                    apiKeyField.getEditText().getText().toString().trim()
                            );
                });

                userViewModel.getApiKeyMessage().observe(getViewLifecycleOwner(), apiKeyMessage -> {
                    if (!userViewModel.getApiKeyMessageProcessed()) {
                        if (apiKeyMessage == null) {
                            showErrorDialog(getString(R.string.server_connection_error));
                        } else {
                            String error = apiKeyMessage.getError();

                            if (error != null) {
                                showErrorDialog(error);
                            }
                        }

                        userViewModel.notifyApiKeyMessageProcessed();
                    }
                });

                userViewModel
                        .getIsApiKeyUpdated()
                        .observe(getViewLifecycleOwner(), isApiKeyUpdated -> {
                            if (!userViewModel.getApiKeyUpdatedProcessed()) {
                                if (!isApiKeyUpdated) {
                                    showErrorDialog(getString(R.string.update_api_key_error));
                                } else {
                                    NavHostFragment
                                            .findNavController(this)
                                            .navigate(
                                                    AuthApiKeyFragmentDirections
                                                            .actionAuthApiKeyFragmentToFeedFragment()
                                            );
                                }

                                userViewModel.notifyApiKeyUpdatedProcessed();
                            }
                });
            }
        }
    }

    private void showErrorDialog(CharSequence message) {
        if (getContext() != null) {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(getString(R.string.error_title))
                    .setMessage(message)
                    .setPositiveButton(
                            getString(R.string.ok),
                            (dialog, which) -> {

                            })
                    .show();
        }
    }
}
