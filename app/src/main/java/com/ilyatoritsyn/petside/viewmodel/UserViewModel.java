package com.ilyatoritsyn.petside.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.ilyatoritsyn.petside.data.auth.AuthRepository;
import com.ilyatoritsyn.petside.data.auth.model.SignUpMessage;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

// ViewModel для аутентификации
@HiltViewModel
public class UserViewModel extends AndroidViewModel {
    // Репозиторий для работы с данными аутентификации
    private final AuthRepository authRepository;

    // Сообщение-ответ сервера на E-mail и описание, преобразованное к модели
    private MutableLiveData<SignUpMessage> signUpMessage;

    // Сообщение-ответ сервера на ключ API, преобразованное к модели
    private MutableLiveData<SignUpMessage> apiKeyMessage;

    // Есть ли в SharedPreferences сохранённый ключ API
    private MutableLiveData<Boolean> isApiKey;

    // Удалось ли перезаписать новый ключ API в SharedPreferences
    private MutableLiveData<Boolean> isApiKeyUpdated;

    // Обработан ли ответ сервера на E-mail и описание в интерфейсе
    // Нужно, чтобы не отображать уже показанные ошибки при повороте экрана и не совершать переходы
    private boolean isSignUpMessageProcessed;

    // Обработан ли ответ сервера на ключ API в интерфейсе
    private boolean isApiKeyMessageProcessed;

    // Обработана ли в интерфейсе отметка об успешности перезаписи ключа API в SharedPreferences
    private boolean isApiKeyUpdatedProcessed;

    @Inject
    public UserViewModel(
            @NonNull Application application,
            AuthRepository authRepository) {
        super(application);

        this.authRepository = authRepository;
    }

    // Геттер для обсервера в FeedFragment
    public MutableLiveData<Boolean> getIsApiKey() {
        if (isApiKey == null) {
            isApiKey = new MutableLiveData<>();
        }

        return isApiKey;
    }

    // Геттер для обсервера в AuthEmailDescFragment
    public MutableLiveData<SignUpMessage> getSignUpMessage() {
        if (signUpMessage == null) {
            signUpMessage = new MutableLiveData<>();
        }

        return signUpMessage;
    }

    public boolean getSignUpMessageProcessed() {
        return isSignUpMessageProcessed;
    }

    // Геттер для обсервера в AuthApiKeyFragment
    public MutableLiveData<SignUpMessage> getApiKeyMessage() {
        if (apiKeyMessage == null) {
            apiKeyMessage = new MutableLiveData<>();
        }

        return apiKeyMessage;
    }

    // Геттер для обсервера в AuthApiKeyFragment
    public MutableLiveData<Boolean> getIsApiKeyUpdated() {
        if (isApiKeyUpdated == null) {
            isApiKeyUpdated = new MutableLiveData<>();
        }

        return isApiKeyUpdated;
    }

    public boolean getApiKeyMessageProcessed() {
        return isApiKeyMessageProcessed;
    }

    public boolean getApiKeyUpdatedProcessed() {
        return isApiKeyUpdatedProcessed;
    }

    // Проверить: есть ли сохранённый ключ API в SharedPreferences
    public void checkApiKeyAvailability() {
        authRepository.isApiKey(getApplication(), isApiKey::postValue);
    }

    // Отправить E-mail и описание на сервер и получить ответ
    public void signUp(String email, String desc) {
        authRepository.signUp(getApplication(), email, desc, result -> {
            isSignUpMessageProcessed = false;

            signUpMessage.postValue(result);
        });
    }

    // Чтобы повторно в обсервере не обрабатывать данные при повороте экрана
    public void notifySignUpMessageProcessed() {
        isSignUpMessageProcessed = true;
    }

    // Проверить: рабочий ли ключ API
    public void checkApiKeyServiceability(String apiKey) {
        authRepository.checkApiKeyServiceability(getApplication(), apiKey, checkingResult -> {
            isApiKeyMessageProcessed = false;

            apiKeyMessage.postValue(checkingResult);

            // Если ключ рабочий, то перезаписываем его в SharedPreferences
            if (checkingResult != null && checkingResult.getError() == null) {
                authRepository.updateApiKey(getApplication(), apiKey, updatingResult -> {
                    isApiKeyUpdatedProcessed = false;

                    if (updatingResult) {
                        // Чтобы FeedFragment сразу знал, что ключ API есть в SharedPreferences
                        if (isApiKey == null) {
                            // Возможно, в этом нет смысла, т.к. isApiKey уже инициализировался
                            // при создании обсервера в FeedFragment
                            isApiKey = new MutableLiveData<>(true);
                        } else if (Boolean.FALSE.equals(isApiKey.getValue())) {
                            isApiKey.postValue(true);
                        }
                    }

                    isApiKeyUpdated.postValue(updatingResult);
                });
            }
        });
    }

    // Чтобы повторно в обсервере не обрабатывать данные при повороте экрана
    public void notifyApiKeyMessageProcessed() {
        isApiKeyMessageProcessed = true;
    }

    // Чтобы повторно в обсервере не обрабатывать данные при повороте экрана
    public void notifyApiKeyUpdatedProcessed() {
        isApiKeyUpdatedProcessed = true;
    }
}
