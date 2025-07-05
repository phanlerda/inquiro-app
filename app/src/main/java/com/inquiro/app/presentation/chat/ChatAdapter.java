package com.inquiro.app.presentation.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.inquiro.app.R;
import com.inquiro.app.data.remote.dto.SourceDto;

import java.util.List;

public class ChatAdapter extends ListAdapter<ChatMessage, ChatAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;

    public ChatAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<ChatMessage> DIFF_CALLBACK = new DiffUtil.ItemCallback<ChatMessage>() {
        @Override
        public boolean areItemsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            return oldItem.getText().equals(newItem.getText());
        }
    };

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = getItem(position);
        if (message.getSender() == ChatMessage.Sender.USER) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_USER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_user, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_bot, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewMessage;
        // THÊM MỚI: Tham chiếu đến TextView nguồn
        private final TextView textViewSources;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            // THÊM MỚI: Lấy ID của TextView nguồn
            textViewSources = itemView.findViewById(R.id.textViewSources);
        }

        public void bind(ChatMessage message) {
            // Gán nội dung tin nhắn chính
            textViewMessage.setText(message.getText());

            // CHỈ ÁP DỤNG CHO VIEW CỦA BOT: Kiểm tra và hiển thị nguồn
            if (textViewSources != null) {
                List<SourceDto> sources = message.getSources();
                if (sources != null && !sources.isEmpty()) {
                    // Nếu có nguồn, làm cho TextView hiển thị và gán nội dung
                    textViewSources.setVisibility(View.VISIBLE);

                    StringBuilder sourcesText = new StringBuilder();
                    sourcesText.append("Sources:\n");
                    for (int i = 0; i < sources.size(); i++) {
                        SourceDto source = sources.get(i);
                        // Định dạng cho đẹp: [Nguồn 1: ten_file.pdf] Doan van ban...
                        sourcesText.append(String.format("[%d: %s]\n\"%s...\"\n\n",
                                i + 1,
                                source.getFilename(),
                                // Lấy 80 ký tự đầu của nguồn
                                source.getText().substring(0, Math.min(source.getText().length(), 80))
                        ));
                    }
                    textViewSources.setText(sourcesText.toString().trim());

                } else {
                    // Nếu không có nguồn, đảm bảo TextView ẩn đi
                    textViewSources.setVisibility(View.GONE);
                }
            }
        }
    }
}