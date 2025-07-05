package com.inquiro.app.presentation.chat;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.inquiro.app.data.remote.dto.ChatRequestDto;
import com.inquiro.app.data.remote.dto.DocumentResponseDto;
import com.inquiro.app.data.repository.AppRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainViewModel extends ViewModel {
    private final AppRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<List<ChatMessage>> _messages = new MutableLiveData<>(new ArrayList<>());
    public final LiveData<List<ChatMessage>> messages = _messages;

    private final MutableLiveData<List<DocumentResponseDto>> _documents = new MutableLiveData<>();
    public final LiveData<List<DocumentResponseDto>> documents = _documents;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _errorEvent = new MutableLiveData<>();
    public final LiveData<String> errorEvent = _errorEvent;

    public MainViewModel() {
        this.repository = new AppRepository();
    }

    public void fetchDocuments() {
        _isLoading.setValue(true);
        disposables.add(repository.getDocuments()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> _isLoading.setValue(false))
                .subscribe(
                        documentList -> _documents.setValue(documentList),
                        throwable -> _errorEvent.setValue("Failed to fetch documents: " + throwable.getMessage())
                )
        );
    }

    // Thay thế toàn bộ phương thức sendMessage bằng phiên bản này
    public void sendMessage(String text, Integer documentId) {
        ChatMessage userMessage = new ChatMessage(text, ChatMessage.Sender.USER);

        List<ChatMessage> updatedMessages = new ArrayList<>(_messages.getValue());
        updatedMessages.add(userMessage);

        _messages.setValue(updatedMessages);
        _isLoading.setValue(true);

        // --- BẮT ĐẦU LOGIC MỚI ĐỂ TẠO HISTORY ĐÚNG ĐỊNH DẠNG ---

        // 1. Chuẩn bị một danh sách rỗng để chứa các cặp [hỏi, đáp]
        List<List<String>> historyForApi = new ArrayList<>();

        // 2. Lấy danh sách tin nhắn để tạo history (không bao gồm tin nhắn vừa gửi)
        List<ChatMessage> messagesForHistory = new ArrayList<>(updatedMessages);
        messagesForHistory.remove(messagesForHistory.size() - 1);

        // 3. Lặp qua danh sách theo từng cặp (user, bot)
        for (int i = 0; i < messagesForHistory.size() - 1; i += 2) {
            ChatMessage q = messagesForHistory.get(i);
            ChatMessage a = messagesForHistory.get(i + 1);

            // Đảm bảo đúng là một cặp hỏi-đáp
            if (q.getSender() == ChatMessage.Sender.USER && a.getSender() == ChatMessage.Sender.BOT) {
                List<String> pair = new ArrayList<>();
                pair.add(q.getText());
                pair.add(a.getText());
                historyForApi.add(pair);
            }
        }

        // 4. Tạo request với tên trường và cấu trúc history chính xác
        // Chú ý: `text` chính là `query`
        ChatRequestDto request = new ChatRequestDto(text, historyForApi, documentId);

        // 5. Gửi request
        disposables.add(repository.postChatMessage(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> _isLoading.setValue(false))
                .subscribe(
                        chatResponse -> {
                            // Sửa lỗi: dùng getAnswer() thay vì getResponse()
                            ChatMessage botMessage = new ChatMessage(chatResponse.getAnswer(), ChatMessage.Sender.BOT,chatResponse.getSources());
                            List<ChatMessage> finalMessages = new ArrayList<>(_messages.getValue());
                            finalMessages.add(botMessage);
                            _messages.setValue(finalMessages);
                        },
                        throwable -> {
                            ChatMessage errorMessage = new ChatMessage("Error sending message: " + throwable.getMessage(), ChatMessage.Sender.BOT);
                            List<ChatMessage> finalMessages = new ArrayList<>(_messages.getValue());
                            finalMessages.add(errorMessage);
                            _messages.setValue(finalMessages);
                        }
                )
        );
        // --- KẾT THÚC LOGIC MỚI ---
    }

    public void uploadDocument(Context context, Uri fileUri) {
        _isLoading.setValue(true);
        disposables.add(
                Single.fromCallable(() -> createMultipartBody(context, fileUri))
                        .flatMap(repository::uploadDocument)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(() -> _isLoading.setValue(false))
                        .subscribe(
                                documentResponseDto -> fetchDocuments(),
                                throwable -> _errorEvent.setValue("Upload failed: " + throwable.getMessage())
                        )
        );
    }

    private MultipartBody.Part createMultipartBody(Context context, Uri uri) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = "unknown_file";
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) fileName = cursor.getString(nameIndex);
            }
        }

        File tempFile = File.createTempFile("upload", ".tmp", context.getCacheDir());
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
        inputStream.close();

        RequestBody requestFile = RequestBody.create(tempFile, MediaType.parse(context.getContentResolver().getType(uri)));
        return MultipartBody.Part.createFormData("file", fileName, requestFile);
    }

    public void startNewConversation() {
        // Chỉ cần gán một ArrayList rỗng mới cho LiveData là đủ
        _messages.setValue(new ArrayList<>());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}