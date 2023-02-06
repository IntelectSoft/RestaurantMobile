package md.edi.mobilewaiter.data.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import md.edi.mobilewaiter.R;

import java.util.List;

public class BaseAdapter extends BaseAdapterRecycler {

    protected Context context;
    protected List<Item> items;
    protected GenericViewHolder genericViewHolder;

    @SuppressWarnings("unchecked")
    public BaseAdapter(List<Item> objects) {
        super(objects);
        this.items = objects;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return defaultViewHolder(viewGroup);
    }

    protected RecyclerView.ViewHolder defaultViewHolder(ViewGroup viewGroup){
        View view = LayoutInflater.from(context).inflate(R.layout.item_default, viewGroup, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        genericViewHolder = (GenericViewHolder) viewHolder;
        genericViewHolder.setContext(context);
    }
}
