package com.ilyatoritsyn.petside.data.auth.model;

// Модель, которая будет использоваться для регистрации
// Эта модель передаётся в метод Retrofit и затем уже там преобразовывается в JSON-формат
public class SignUpRequest {
    private final String email;
    private final String appDescription;
    private final boolean opted_into_mailing_list;
    private final Details details;

    public SignUpRequest(String email, String appDescription) {
        this.email = email;
        this.appDescription = appDescription;

        // Значения по умолчанию
        this.opted_into_mailing_list = false;
        this.details = new Details();
    }
}

class Details {
    private final String user_type;

    public Details() {
        this.user_type = "personal";
    }
}
