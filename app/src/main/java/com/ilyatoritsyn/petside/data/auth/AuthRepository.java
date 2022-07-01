package com.ilyatoritsyn.petside.data.auth;

import android.content.Context;

import com.ilyatoritsyn.petside.R;
import com.ilyatoritsyn.petside.data.auth.model.SignUpMessage;
import com.ilyatoritsyn.petside.data.auth.model.SignUpRequest;
import com.ilyatoritsyn.petside.data.auth.source.AuthLocalSource;
import com.ilyatoritsyn.petside.data.auth.source.AuthNetworkSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executor;
import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class AuthRepository {
    // DataSource для работы с сетевыми данными аутентификации
    private final AuthNetworkSource authNetworkSource;

    // DataSource для работы с локальными данными аутентификации
    private final AuthLocalSource authLocalSource;

    // Для выполнения работы с данными асинхронно, чтобы интерфейс не лагал
    private final Executor executor;

    @Inject
    public AuthRepository(
            AuthNetworkSource authNetworkSource,
            AuthLocalSource authLocalSource,
            Executor executor) {
        this.authNetworkSource = authNetworkSource;
        this.authLocalSource = authLocalSource;
        this.executor = executor;
    }

    // Проверить: есть ли в SharedPreferences сохранённый ключ API, - и вернуть ответ:
    // true - есть, false - нет
    public void isApiKey(Context context, RepositoryCallback<Boolean> callback) {
        executor.execute(() -> callback
                .onComplete(authLocalSource.readApiKey(context) != null));
    }

    // Преобразовать ответ от сервера в модель, чтобы UI-слой получил только нужные ему данные
    // и вернуть эту модель
    // Если ответа от сервера нет, то возвращаем null
    // Если ошибка, то записываем её в поле error
    // Если ответ без ошибки, то возвращем объект с error=null
    private SignUpMessage processResponse(Context context, Response<ResponseBody> response) {
        // Нет ответа
        if (response == null) {
            return null;
        }

        // Если есть ответ сервера о неуспехе
        if (!response.isSuccessful()) {
            // Если сервер почему-то не указал ошибку
            if (response.errorBody() == null) {
                return new SignUpMessage(
                        context.getString(R.string.server_unknown_error)
                );
            } else {
                try {
                    return new SignUpMessage(
                            new JSONObject(response.errorBody().string())
                                    .getString(
                                            context.getString(R.string.response_message_key)
                                    )
                    );
                } catch (IOException | JSONException e) {
                    return new SignUpMessage(
                            context.getString(R.string.error_parsing_error)
                    );
                }
            }
        } else {
            return new SignUpMessage(null);
        }
    }

    // Асинхронно отправляем серверу E-mail, описание, параметры по умолчанию и
    // возвращаем через Callback ответ от сервера, который уже преобразован в модель
    public void signUp(
            Context context,
            String email,
            String desc,
            RepositoryCallback<SignUpMessage> callback
    ) {
        executor.execute(() -> callback
                .onComplete(
                        processResponse(
                                context,
                                authNetworkSource.signUp(new SignUpRequest(email, desc))
                        )
                ));
    }

    // Асинхронно отправляем серверу введённый ключ API для проверки: рабочий ли он
    // И возвращаем через Callback ответ от сервера, который уже преобразован в модель
    public void checkApiKeyServiceability(
            Context context,
            String apiKey,
            RepositoryCallback<SignUpMessage> callback
    ) {
        executor.execute(() -> callback
                .onComplete(processResponse(context, authNetworkSource.checkApiKey(apiKey))));
    }

    // Асинхронно перезаписываем ключ API в SharedPreferences
    // И возвращаем через Callback успешность перезаписи: true или false
    public void updateApiKey(Context context, String apiKey, RepositoryCallback<Boolean> callback) {
        executor.execute(() -> callback.onComplete(authLocalSource.updateApiKey(context, apiKey)));
    }

    // Синхронно читаем ключ API из SharedPreferences
    // Асинхронно сделать не получилось,
    // потому что этот метод используется при создании билдера Retrofit,
    // который создаётся через инъекцию зависимостей
    // Теоретически может вызвать проблемы, если, например, при чтении данных программа затупит
    public String readApiKeySync(Context context) {
        return authLocalSource.readApiKey(context);
    }
}
