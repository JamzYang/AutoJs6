package org.autojs.autojs.network;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.autojs.autojs6.BuildConfig;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public enum RetrofitClient {
    INSTANCE;
    private final String BASE_URL = BuildConfig.BASE_URL;
    private final Retrofit retrofit;

    RetrofitClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static <T> T createApi(Class<T> service) {
        return INSTANCE.retrofit.create(service);
    }
}

class StringConverterFactory extends Converter.Factory {
    private static final StringConverterFactory INSTANCE = new StringConverterFactory();

    public static StringConverterFactory create() {
        return INSTANCE;
    }

    private StringConverterFactory() {}

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (String.class.equals(type)) {
            return ResponseBody::string;
        }
        return null;
    }
}