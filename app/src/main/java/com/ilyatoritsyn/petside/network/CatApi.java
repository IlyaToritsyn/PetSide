package com.ilyatoritsyn.petside.network;

import com.ilyatoritsyn.petside.data.auth.model.SignUpRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

// Интерфейс Retrofit
public interface CatApi {
    // Для отправки серверу E-mail, описания, параметров по умолчанию
    // и получения ответа без автоматического преобрбазования в модель,
    // потому что нам нужны не все данные из ответа
    @POST("user/passwordlesssignup")
    Call<ResponseBody> signUp(@Body SignUpRequest signUpRequest);

    // Для проверки введённого ключа API: рабочий ли он
    // Хардкод "x-api-key" мне не очень нравится
    // хотел подтягивать строку из строковых ресурсов - не нашёл красивого решения
    // Ответ получаем без автоматического преобразования в модель,
    // потому что нам нужны не все данные из ответа
    @GET("favourites")
    Call<ResponseBody> checkApiKeyServiceability(@Header("x-api-key") String apiKey);
}

