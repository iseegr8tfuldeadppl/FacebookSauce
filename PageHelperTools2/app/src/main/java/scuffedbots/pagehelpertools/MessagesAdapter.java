package scuffedbots.pagehelpertools;


import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import scuffedbots.pagehelpertools.MessagesList.Message;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private CommunicationInterface callback;
    private List<Message> messages;
    private LayoutInflater mInflater;
    private Context context;
    private int CLIENTS;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView index, preview, counter, continueMessaging;
        ViewHolder(View itemView) {
            super(itemView);
            index = itemView.findViewById(R.id.index);
            preview = itemView.findViewById(R.id.preview);
            counter = itemView.findViewById(R.id.counter);
            continueMessaging = itemView.findViewById(R.id.continueMessaging);
        }
    }

    public MessagesAdapter(Context context, List<Message> messages, int CLIENTS) {
        this.messages = messages;
        this.context = context;
        this.callback = (CommunicationInterface) context;
        this.mInflater = LayoutInflater.from(context);
        this.CLIENTS = CLIENTS;
    }

    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contactView = mInflater.inflate(R.layout.message, parent, false);
        return new ViewHolder(contactView); }
    @Override
    public int getItemCount() { return messages.size(); }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final Message message = messages.get(position);

        String id = "#" + message.ID;
        String counter = message.CLIENTS_MESSAGED + "/" + CLIENTS + "\n" + "Clients Messaged";
        viewHolder.index.setText(id);
        viewHolder.preview.setText(message.MESSAGE);
        viewHolder.counter.setText(counter);

        viewHolder.continueMessaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.continueMessaging(message);
            }
        });
    }



}