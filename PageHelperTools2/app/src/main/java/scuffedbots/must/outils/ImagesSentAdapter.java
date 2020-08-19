package scuffedbots.must.outils;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;


public class ImagesSentAdapter extends RecyclerView.Adapter<ImagesSentAdapter.ViewHolder> {


    private CommunicationInterface2 callback;
    private List<Conversations.Image> images;
    private LayoutInflater mInflater;
    private Context context;


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView client_name, copyButton;
        RelativeLayout holder;
        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            holder = itemView.findViewById(R.id.holder);
            client_name = itemView.findViewById(R.id.client_name);
            copyButton = itemView.findViewById(R.id.copyButton);
        }
    }


    public ImagesSentAdapter(Context context, List<Conversations.Image> images) {
        this.images = images;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        callback = (CommunicationInterface2) context;
    }


    @NonNull
    @Override
    public ImagesSentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contactView = mInflater.inflate(R.layout.image, parent, false);
        return new ViewHolder(contactView);
    }


    @Override
    public int getItemCount() { return images.size(); }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final Conversations.Image image = images.get(position);
        Picasso.get().load(image.url).into(viewHolder.image);
        viewHolder.client_name.setText(image.sender.name);

        viewHolder.holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.previewPhoto(image);
            }
        });

        viewHolder.copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.checkOutPhoto(image);
            }
        });
    }



}