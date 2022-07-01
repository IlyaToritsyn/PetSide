package com.ilyatoritsyn.petside.data.auth.model;

import androidx.annotation.NonNull;

// Модель ответа сервера на отправку ему:
// 1) E-mail и описания
// 2) ключа API
// Эта модель нужна, чтобы передавать в UI-слой только нужные ему данные
public class SignUpMessage {
    private final String error;

    public SignUpMessage(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    // Для вывода в лог при отладке через System.out.println(signUpMessage);
    @NonNull
    @Override
    public String toString() {
        return "SignUpMessage{" +
                "error='" + error + '\'' +
                '}';
    }
}
