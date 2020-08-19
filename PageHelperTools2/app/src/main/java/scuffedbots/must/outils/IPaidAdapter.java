package scuffedbots.must.outils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class IPaidAdapter extends RecyclerView.Adapter<IPaidAdapter.ViewHolder> {


    private CommunicationInterface2 callback;
    private List<LoadingPage.Client> ipaids;
    private LayoutInflater mInflater;
    private Context context;


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView client_name;
        LinearLayout holder;
        ViewHolder(View itemView) {
            super(itemView);
            holder = itemView.findViewById(R.id.holder);
            client_name = itemView.findViewById(R.id.client_name);
        }
    }


    public IPaidAdapter(Context context, List<LoadingPage.Client> ipaids) {
        this.ipaids = ipaids;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        callback = (CommunicationInterface2) context;
    }


    @NonNull
    @Override
    public IPaidAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contactView = mInflater.inflate(R.layout.ipaid, parent, false);
        return new ViewHolder(contactView);
    }


    @Override
    public int getItemCount() { return ipaids.size(); }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final LoadingPage.Client ipaid = ipaids.get(position);
        viewHolder.client_name.setText(ipaid.name);

        viewHolder.holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.checkOutIPaid(ipaid);
            }
        });
    }



}