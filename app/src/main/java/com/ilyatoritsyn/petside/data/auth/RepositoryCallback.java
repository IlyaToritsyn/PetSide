package com.ilyatoritsyn.petside.data.auth;

// Интерфейс для реализации асинхронных операций с данными
// Описывает метод, который будет вызываться при завершении работы с данными
public interface RepositoryCallback<T> {
    void onComplete(T result);
}
