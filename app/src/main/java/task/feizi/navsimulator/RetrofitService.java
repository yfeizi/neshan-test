package task.feizi.navsimulator;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import task.feizi.navsimulator.model.DirectionRepository;

public class RetrofitService {
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(Config.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client( new OkHttpClient.Builder()
                            .build())
                    .build();
        }
        return retrofit;
    }
}
