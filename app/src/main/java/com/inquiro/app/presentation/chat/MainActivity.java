package com.inquiro.app.presentation.chat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.inquiro.app.R;
import com.inquiro.app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private ChatAdapter chatAdapter;
    private Integer selectedDocumentId = null;
    private ActivityResultLauncher<String> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupFilePicker();
        setupToolbarAndDrawer();
        setupRecyclerView();
        setupObservers();
        setupClickListeners();

        viewModel.fetchDocuments();
    }

    private void setupFilePicker() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        Toast.makeText(this, "Uploading file...", Toast.LENGTH_SHORT).show();
                        viewModel.uploadDocument(this, uri);
                    }
                }
        );
    }

    private void setupToolbarAndDrawer() {
        setSupportActionBar(binding.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.recyclerViewChat.setLayoutManager(layoutManager);
        binding.recyclerViewChat.setAdapter(chatAdapter);
    }

    // Trong file MainActivity.java
    private void setupObservers() {
        viewModel.documents.observe(this, documents -> {
            Menu menu = binding.navigationView.getMenu();

            // ---- BẮT ĐẦU LOGIC MỚI, ĐẢM BẢO HOẠT ĐỘNG ----

            // 1. XÓA TẤT CẢ CÁC ITEM TÀI LIỆU CŨ
            // Chúng ta duyệt ngược để tránh lỗi khi xóa item khỏi danh sách đang duyệt
            for (int i = menu.size() - 1; i >= 0; i--) {
                MenuItem item = menu.getItem(i);
                // Nếu một item không phải là nút Upload tĩnh, nó chắc chắn là một tài liệu cũ cần xóa.
                if (item.getGroupId() != R.id.group_static) {
                    menu.removeItem(item.getItemId());
                }
            }

            // 2. THÊM CÁC ITEM TÀI LIỆU MỚI TỪ API
            if (documents != null && !documents.isEmpty()) {
                for (int i = 0; i < documents.size(); i++) {
                    // Thêm vào một group mới (sẽ được tạo ngầm), có thể check được
                    MenuItem newItem = menu.add(
                            R.id.group_documents,               // ID cho group động mới
                            documents.get(i).getId(),           // itemId = ID của document từ DB
                            Menu.NONE,                          // order
                            documents.get(i).getFilename()      // title
                    );
                    newItem.setIcon(android.R.drawable.ic_menu_agenda);
                    newItem.setCheckable(true);
                }
            }
            // Đặt group động này có thể check (chọn 1 trong nhiều)
            menu.setGroupCheckable(R.id.group_documents, true, true);

            // ---- KẾT THÚC LOGIC MỚI ----
        });

        viewModel.messages.observe(this, messages -> {
            chatAdapter.submitList(messages);
            if (messages != null && !messages.isEmpty()) {
                binding.recyclerViewChat.smoothScrollToPosition(messages.size() - 1);
            }
        });

        viewModel.isLoading.observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.buttonSend.setEnabled(!isLoading);
            binding.editTextMessage.setEnabled(!isLoading);
        });

        viewModel.errorEvent.observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

// File: com/inquiro/app/presentation/chat/MainActivity.java
// Thay thế toàn bộ phương thức này

    private void setupClickListeners() {
        binding.buttonSend.setOnClickListener(v -> {
            String messageText = binding.editTextMessage.getText().toString().trim();
            if (messageText.isEmpty()) return;

            if (selectedDocumentId == null) {
                Toast.makeText(this, "Please select a document from the drawer first.", Toast.LENGTH_LONG).show();
                return;
            }
            viewModel.sendMessage(messageText, selectedDocumentId);
            binding.editTextMessage.setText("");
        });

        // ---- BẮT ĐẦU LOGIC MỚI CHO NAVIGATION DRAWER ----
        binding.navigationView.setNavigationItemSelectedListener(item -> {
            int clickedId = item.getItemId();
            int groupId = item.getGroupId();

            // Xử lý nút Upload
            if (clickedId == R.id.nav_upload_document) {
                filePickerLauncher.launch("*/*");
                // Không làm gì khác, không đóng drawer
                return false; // Trả về false để item này không bị highlight như là đã được chọn
            }

            // Chỉ xử lý nếu item được nhấn thuộc về group của các tài liệu động
            if (groupId == R.id.group_documents) {
                // KIỂM TRA QUAN TRỌNG: Chỉ reset chat nếu người dùng chọn một TÀI LIỆU MỚI
                // So sánh ID được click với ID đang được lưu trữ
                if (selectedDocumentId == null || !selectedDocumentId.equals(clickedId)) {

                    // 1. BÁO CHO VIEWMODEL BẮT ĐẦU CUỘC HỘI THOẠI MỚI
                    viewModel.startNewConversation();

                    // 2. Cập nhật ID của tài liệu đang được chọn
                    selectedDocumentId = clickedId;

                    // 3. Cập nhật tiêu đề phụ trên Toolbar
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setSubtitle("Chatting with: " + item.getTitle());
                    }

                    // 4. Thông báo cho người dùng
                    Toast.makeText(this, "Started new chat with: " + item.getTitle(), Toast.LENGTH_SHORT).show();

                }

                // Dù là chọn lại tài liệu cũ hay mới, vẫn đóng drawer
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true; // Trả về true để cho thấy chúng ta đã xử lý sự kiện click này
            }

            return false;
        });
        // ---- KẾT THÚC LOGIC MỚI ----
    }
}