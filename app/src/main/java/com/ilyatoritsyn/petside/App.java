package com.ilyatoritsyn.petside;

import android.app.Application;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

// Чтобы заработал Hilt для реализации инъекции зависимостей
@HiltAndroidApp
public class App extends Application {
    // Чтоб наверняка был лишь один ExecutorService на всё приложение
    // Для реализации асинхронной работы с данными
    @Inject
    ExecutorService executorService;
}
