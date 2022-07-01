package com.ilyatoritsyn.petside.data.auth.source;

import com.ilyatoritsyn.petside.data.auth.model.SignUpRequest;
import com.ilyatoritsyn.petside.di.NetworkModule;
import com.ilyatoritsyn.petside.network.CatApi;
import java.io.IOException;
import javax.inject.Inject;
import okhttp3.ResponseBody;
import retrofit2.Response;

// DataSource для работы с сетевыми данными аутентификации
public class AuthNetworkSource {
    // Интерфейс Retrofit с запросами
    private final CatApi catApi;

    @Inject
    public AuthNetworkSource(@NetworkModule.CatApi CatApi catApi) {
        this.catApi = catApi;
    }

    // Отправить запрос с E-mail, описанием, параметрами по умолчанию и вернуть ответ
    // Если не удалось достучаться до сервера, то возвращаем null, т.е. ответа нет
    public Response<ResponseBody> signUp(SignUpRequest signUpRequest) {
        try {
            return catApi.signUp(signUpRequest).execute();
        } catch (IOException e) {
            return null;
        }
    }

    // Отправить запрос с ключом API и вернуть ответ
    // Если не удалось достучаться до сервера, то возвращаем null, т.е. ответа нет
    public Response<ResponseBody> checkApiKey(String apiKey) {
        try {
            return catApi.checkApiKeyServiceability(apiKey).execute();
        } catch (IOException e) {
            return null;
        }
    }
}
