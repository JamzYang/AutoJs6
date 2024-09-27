package org.autojs.autojs.network;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.ResponseBody;
import org.autojs.autojs.ui.Constants;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public enum RetrofitClient {
    INSTANCE;

    private final Retrofit retrofit;

    RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static <T> T createApi(final Class<T> service) {
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