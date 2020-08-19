package scuffedbots.pagehelpertools;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;
import scuffedbots.pagehelpertools.Conversations.Image;


public class ImagesSentAdapter extends RecyclerView.Adapter<ImagesSentAdapter.ViewHolder> {

    private CommunicationInterface callback;
    private List<Image> images;
    private LayoutInflater mInflater;
    private Context context;


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView client_name;
        RelativeLayout holder;
        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            holder = itemView.findViewById(R.id.holder);
            client_name = itemView.findViewById(R.id.client_name);
        }
    }


    public ImagesSentAdapter(Context context, List<Image> images) {
        this.images = images;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        callback = (CommunicationInterface) context;
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
        final Image image = images.get(position);
        callback = (CommunicationInterface) viewHolder.itemView.getContext();
        Log.i("HH", "image " + image.url + " from " + image.sender.name);

        Picasso.get()
                .load(image.url)
                .into(viewHolder.image);

        viewHolder.client_name.setText(image.sender.name);
    }



}