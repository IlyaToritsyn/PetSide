package com.ilyatoritsyn.petside.di;

import android.content.Context;
import com.ilyatoritsyn.petside.R;
import com.ilyatoritsyn.petside.data.auth.AuthRepository;

import javax.inject.Qualifier;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Модуль для работы с сетью, в частности для создания объектов Retrofit 2
// (библиотека для работы с сетью)
@Module
// Поскольку объекты не относятся ни к активити, ни к фрагментам, относятся к приложению в целом
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    // Задаём название, чтобы Hilt понимал,
    // какой именно объект мы хотим создать при использовании @Inject
    @Qualifier
    // Для реализации интерфейса, где по умолчанию не встроен API ключ в хэдер
    public @interface CatApi {

    }

    @CatApi
    // Создаём объект, реализующий интерфейс
    @Provides
    // Реализация интерфейса, где по умолчанию не встроен API ключ в хэдер
    // Требуется на этапе аутентификации
    public static com.ilyatoritsyn.petside.network.CatApi provideCatApi(
            @ApplicationContext Context context
    ) {
        // Реализуем интерфейс для подключения к серверу
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(context.getString(R.string.server_address_main_part))
                .build()
                .create(com.ilyatoritsyn.petside.network.CatApi.class);
    }

    @Provides
    // Перехватчик, который добавляет API ключ в хэдер
    // Ключ читается синхронно из SharedPreferences -
    // теоретически могут быть проблемы, если при чтении данных программа затупит
    public static Interceptor provideInterceptor(
            @ApplicationContext Context context,
            AuthRepository authRepository
    ) {
        return chain -> chain.proceed(
                chain
                        .request()
                        .newBuilder()
                        .addHeader(
                                context.getString(R.string.api_key_key),
                                authRepository.readApiKeySync(context)
                        )
                        .build()
        );
    }

    // Для создания билдера со встроенным ключом API в хэдер
    @Qualifier
    public @interface WithBuiltInApiKeyTheCatApiService {

    }

    @Provides
    public static OkHttpClient provideOkHttpClient(Interceptor interceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
    }

    // Билдер со встроенным ключом API в хэдер
    @WithBuiltInApiKeyTheCatApiService
    @Provides
    public static com.ilyatoritsyn.petside.network.CatApi provideWithBuiltInApiKeyTheCatApiService(
            @ApplicationContext Context context,
            OkHttpClient okHttpClient
    ) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(context.getString(R.string.server_address_main_part))
                .client(okHttpClient)
                .build()
                .create(com.ilyatoritsyn.petside.network.CatApi.class);
    }
}
