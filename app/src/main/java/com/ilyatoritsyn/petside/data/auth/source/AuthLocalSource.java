package com.ilyatoritsyn.petside.data.auth.source;

import android.content.Context;
import android.content.SharedPreferences;

import com.ilyatoritsyn.petside.R;

import javax.inject.Inject;

// DataSource для работы с локальными данными аутентификации
public class AuthLocalSource {
    // Для чтения SharedPreferences
    private final SharedPreferences sharedPreferences;

    // Для записи в SharedPreferences
    private final SharedPreferences.Editor editor;

    @Inject
    public AuthLocalSource(SharedPreferences.Editor editor, SharedPreferences sharedPreferences) {
        this.editor = editor;
        this.sharedPreferences = sharedPreferences;
    }

    // Прочитать ключ API из SharedPreferences и вернуть его
    // Если ключа API нет, то возвращаем null
    public String readApiKey(Context context) {
        String apiKey = sharedPreferences
                .getString(
                        context.getString(R.string.api_key_key),
                        context.getString(R.string.api_key_default)
                );

        if (apiKey.equals(context.getString(R.string.api_key_default))) {
            return null;
        } else {
            return apiKey;
        }
    }

    // Перезаписать значение ключа API в SharedPreferences и вернуть результат:
    // true - успех, false - неудача
    public boolean updateApiKey(Context context, String apiKey) {
        editor.putString(context.getString(R.string.api_key_key), apiKey).apply();

        return apiKey.equals(readApiKey(context));
    }
}
