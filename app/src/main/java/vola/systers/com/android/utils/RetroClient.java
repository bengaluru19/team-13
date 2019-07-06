package vola.systers.com.android.utils;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import vola.systers.com.android.model.Distance;

public interface RetroClient {

    @GET("json")
    Call<Distance> calulateDistance(@Query("units")String units,@Query("origins")String origins,@Query("destinations")String destinations,@Query("key")String key);

}
