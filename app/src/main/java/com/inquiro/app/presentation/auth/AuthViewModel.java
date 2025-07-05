package com.inquiro.app.presentation.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.inquiro.app.data.local.PrefsManager;
import com.inquiro.app.data.remote.dto.LoginResponseDto;
import com.inquiro.app.data.repository.AppRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {
    private final AppRepository appRepository;
    private final PrefsManager prefsManager;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<AuthResult> _authResult = new MutableLiveData<>();
    public LiveData<AuthResult> authResult = _authResult;

    public AuthViewModel() {
        this.appRepository = new AppRepository();
        this.prefsManager = PrefsManager.getInstance();
    }

    public void login(String email, String password) {
        _authResult.setValue(AuthResult.loading());

        appRepository.login(email, password).enqueue(new Callback<LoginResponseDto>() {
            @Override
            public void onResponse(Call<LoginResponseDto> call, Response<LoginResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    prefsManager.saveAuthToken(response.body().getAccessToken());
                    _authResult.setValue(AuthResult.success());
                } else {
                    _authResult.setValue(AuthResult.error("Login failed. Check credentials."));
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDto> call, Throwable t) {
                _authResult.setValue(AuthResult.error("Network Error: " + t.getMessage()));
            }
        });
    }

    public void register(String email, String password) {
        _authResult.setValue(AuthResult.loading());
        disposables.add(appRepository.register(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userResponseDto -> login(email, password),
                        throwable -> _authResult.setValue(AuthResult.error("Registration failed: " + throwable.getMessage()))
                )
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}