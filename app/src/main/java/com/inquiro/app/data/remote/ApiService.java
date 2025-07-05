package com.inquiro.app.data.remote;

import com.inquiro.app.data.remote.dto.*;
import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    String BASE_URL = "http://10.0.2.2:8000/api/v1/";

    @POST("auth/login")
    Call<LoginResponseDto> login(@Body RequestBody body);

    @POST("auth/register")
    Single<UserResponseDto> register(@Body RegisterRequestDto body);

    @GET("documents/")
    Single<List<DocumentResponseDto>> getDocuments();

    @Multipart
    @POST("documents/upload")
    Single<DocumentResponseDto> uploadDocument(@Part MultipartBody.Part file);

    @DELETE("documents/{id}")
    Completable deleteDocument(@Path("id") int documentId);

    @POST("chat/")
    Single<ChatResponseDto> postChatMessage(@Body ChatRequestDto request);
}