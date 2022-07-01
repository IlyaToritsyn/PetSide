package com.ilyatoritsyn.petside;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.ilyatoritsyn.petside.viewmodel.UserViewModel;
import java.util.regex.Pattern;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthEmailDescFragment extends Fragment {
    private UserViewModel userViewModel;

    private TextInputLayout email;    // Поле для ввода email
    private TextInputLayout desc;     // Поле для описания цели использования api
    private Button button;

    private boolean isEmailValidationResultShown;
    private boolean isDescValidationResultShown;
    private boolean isEmailValid;
    private boolean isDescValid;

    private long mLastClickTime = 0;

    public AuthEmailDescFragment() {
        super(R.layout.fragment_auth_email_desc);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
            email = view.findViewById(R.id.email_layout_auth_email_desc);
            desc = view.findViewById(R.id.desc_layout_auth_email_desc);
            button = view.findViewById(R.id.button_auth_email_desc);
            int validFieldColor = ContextCompat.getColor(getContext(), R.color.green_2);

            if (email != null &&
                    email.getEditText() != null &&
                    desc != null &&
                    desc.getEditText() != null) {
                email.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    /*
                    ВАЛИДАЦИЯ E-MAIL

                    Локальная часть:
                    - цифры 0-9
                    - большие и маленькие латинские буквы
                    - разрешены символы: нижнее подчёркивание "_", дефис "-" и точка "."
                    - запрещена точка "." в начале и в конце
                    - запрещены повторяющиеся точки "."
                    - макс. 64 символа

                    Доменная часть:
                    - цифры 0-9
                    - большие и маленькие латинские буквы
                    - дефис "-" и точка "." запрещены в начале и в конце
                    - запрещены повторяющиеся точки "."
                    */

                        // поле ввода остаётся красным до тех пор,
                        // пока пользователь не введёт корректное значение
                        if (!Pattern.matches(
                                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                                        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
                                s.toString().trim())) {
                            isEmailValid = false;

                            if (isEmailValidationResultShown) {
                                email.setError(getString(R.string.invalid_email_message));
                            }
                        } else {
                            isEmailValidationResultShown = true;
                            isEmailValid = true;

                            // Как только пользователь вводит валидный email,
                            // поле ввода подсвечено зелёным
                            email.setErrorEnabled(false);
                            email.setBoxStrokeColor(validFieldColor);
                            email.setHintTextColor(ColorStateList.valueOf(validFieldColor));
                        }

                        // Кнопка становится активна,
                        // как только введён корректный email и непустое описание
                        button.setEnabled(isEmailValid && isDescValid);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                email.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
                    // Если пользователь ввёл некорректный email и перешёл к вводу описания,
                    // то первое поле подсвечивается красным
                    if (!hasFocus && !isEmailValid) {
                        isEmailValidationResultShown = true;

                        email.setError(getString(R.string.invalid_email_message));
                    }
                });

                desc.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // поле ввода остаётся красным до тех пор,
                        // пока пользователь не введёт корректное значение
                        if (s.toString().trim().isEmpty()) {
                            isDescValid = false;

                            if (isDescValidationResultShown) {
                                desc.setError(getString(R.string.invalid_desc_message));
                            }
                        } else {
                            isDescValidationResultShown = true;
                            isDescValid = true;

                            // Как только пользователь вводит валидное описание,
                            // поле ввода подсвечено зелёным
                            desc.setErrorEnabled(false);
                            desc.setBoxStrokeColor(validFieldColor);
                            desc.setHintTextColor(ColorStateList.valueOf(validFieldColor));
                        }

                        // Кнопка становится активна,
                        // как только введён корректный email и непустое описание
                        button.setEnabled(isEmailValid && isDescValid);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                desc.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
                    // Если пользователь ввёл некорректное описание и перешёл к вводу email,
                    // то поле с описанием подсвечивается красным
                    if (!hasFocus && !isDescValid) {
                        isDescValidationResultShown = true;

                        desc.setError(getString(R.string.invalid_desc_message));
                    }
                });

                button.setOnClickListener(buttonView -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < MainActivity.clickDelay) {
                        return;
                    }

                    mLastClickTime = SystemClock.elapsedRealtime();

                    userViewModel
                            .signUp(
                                    email.getEditText().getText().toString().trim(),
                                    desc.getEditText().getText().toString().trim()
                            );
                });

                userViewModel.getSignUpMessage().observe(getViewLifecycleOwner(), signUpMessage -> {
                    if (!userViewModel.getSignUpMessageProcessed()) {
                        if (signUpMessage == null) {
                            showErrorDialog(getString(R.string.server_connection_error));
                        } else {
                            String error = signUpMessage.getError();

                            if (error != null) {
                                showErrorDialog(error);
                            } else {
                                NavHostFragment.findNavController(this)
                                        .navigate(
                                                AuthEmailDescFragmentDirections
                                                        .actionAuthEmailDescFragmentToAuthApiKeyFragment()
                                        );
                            }
                        }

                        userViewModel.notifySignUpMessageProcessed();
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
