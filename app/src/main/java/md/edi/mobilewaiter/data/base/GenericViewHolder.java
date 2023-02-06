package md.edi.mobilewaiter.data.base;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


public abstract class GenericViewHolder extends RecyclerView.ViewHolder implements GenericInterfaceHolder {
    private BaseInterfaceListener listener;
    private Context context;
    private FragmentManager fragmentManager;

    public GenericViewHolder(View itemView) {
        super(itemView);
    }

    public BaseInterfaceListener getListener() {
        return listener;
    }

    public void setListener(BaseInterfaceListener listener) {
        this.listener = listener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }
}
