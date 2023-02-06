package md.edi.mobilewaiter.data.base;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class BaseAdapterRecycler<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<T> mObjects;
    private ItemClickListener itemClickListener;
    private View emptyView;


    public BaseAdapterRecycler(final List<T> objects) {
        mObjects = objects;
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                checkIfEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                checkIfEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                checkIfEmpty();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    public void add(final T object) {
        mObjects.add(object);
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        mObjects.clear();
        notifyDataSetChanged();
    }
    public void clearNotify() {

        notifyItemRangeRemoved(0, mObjects.size());
        mObjects.clear();
    }

    private void checkIfEmpty(){
        if (emptyView != null) {
            final boolean emptyViewVisible =
                    getItemCount() == 0;
            System.out.println("emptyViewVisible"+emptyViewVisible);
            emptyView.setVisibility(emptyViewVisible ? View.VISIBLE : View.GONE);
        }
    }

    public T getItem(final int position) {
        return mObjects.get(position);
    }

    public long getItemId(final int position) {
        return position;
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     * @return The position of the specified item.
     */
    public int getPosition(final T item) {
        return mObjects.indexOf(item);
    }

    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insert(final T object, int index) {
        mObjects.add(index, object);
        notifyItemInserted(index);

    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public void remove(T object) {
        final int position = getPosition(object);
        mObjects.remove(object);
        notifyItemRemoved(position);
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained in this adapter.
     */
    public void sort(Comparator<? super T> comparator) {
        Collections.sort(mObjects, comparator);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public void setEmptyView(View emptyView){
        checkIfEmpty();
        this.emptyView=emptyView;
    }

    public void onItemClick(View view , int position){
        if(itemClickListener!=null){
            itemClickListener.onItemClick(view,position);
        }
    }

    public interface ItemClickListener{
        public void onItemClick(View view, int position);
    }

    public interface DataEmptyObserver{
        public void onDataChanged(boolean isEmpty);
    }

    public void notifyItem(Object item){
        int index = 0;
        for (int i = 0; i < mObjects.size(); i++) {
            if(mObjects.get(i).equals(item)){
                index = i;
                break;
            }
        }
        System.out.println("notifyItemChanged "+index);
        notifyItemChanged(index);
    }
    public void notifyItemInserted(Object item){
        int index = 0;
        for (int i = 0; i < mObjects.size(); i++) {
            if(mObjects.get(i).equals(item)){
                index = i;
                break;
            }
        }
        notifyItemInserted(index);
    }

    public List<T> getmObjects() {
        return mObjects;
    }
}