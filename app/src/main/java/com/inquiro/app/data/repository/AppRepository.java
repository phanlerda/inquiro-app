package com.inquiro.app.data.repository;

import com.inquiro.app.data.remote.ApiClient;
import com.inquiro.app.data.remote.ApiService;
import com.inquiro.app.data.remote.dto.*;
import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class AppRepository {
    private final ApiService apiService;

    public AppRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public Call<LoginResponseDto> login(String username, String password) {
        String requestBodyString = "username=" + username + "&password=" + password;
        RequestBody requestBody = RequestBody.create(
                requestBodyString,
                MediaType.parse("application/x-www-form-urlencoded")
        );
        return apiService.login(requestBody);
    }

    public Single<UserResponseDto> register(String email, String password) {
        RegisterRequestDto requestDto = new RegisterRequestDto(email, password);
        return apiService.register(requestDto);
    }

    public Single<List<DocumentResponseDto>> getDocuments() {
        return apiService.getDocuments();
    }

    public Single<DocumentResponseDto> uploadDocument(MultipartBody.Part file) {
        return apiService.uploadDocument(file);
    }

    public Completable deleteDocument(int documentId) {
        return apiService.deleteDocument(documentId);
    }

    public Single<ChatResponseDto> postChatMessage(ChatRequestDto request) {
        return apiService.postChatMessage(request);
    }
}