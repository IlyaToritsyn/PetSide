package com.ilyatoritsyn.petside.di;

import android.content.Context;
import android.content.SharedPreferences;
import com.ilyatoritsyn.petside.R;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

// Модуль инъекции зависимостей Hilt, который содержит объекты для локальной работы
@Module
// Поскольку объекты не относятся ни к активити, ни к фрагментам, относятся к приложению в целом
@InstallIn(SingletonComponent.class)
public class LocalModule {
    // Для работы с потоками, 1 фоновый поток для работы с данными: сеть + SharedPreferences
    @Provides
    public static ExecutorService provideExecutorService() {
        return Executors.newFixedThreadPool(1);
    }

    // Запускатор потоков
    @Provides
    public static Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    // Для чтения из SharedPreferences
    @Provides
    public static SharedPreferences provideSharedPreferences(@ApplicationContext Context context) {
        return context
                .getSharedPreferences(
                        context.getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE
                );
    }

    // Для записи в SharedPreferences
    @Provides
    public static SharedPreferences.Editor provideEditor(
            SharedPreferences sharedPreferences
    ) {
        return sharedPreferences.edit();
    }
}
