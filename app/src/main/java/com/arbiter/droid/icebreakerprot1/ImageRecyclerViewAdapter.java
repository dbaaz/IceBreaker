package com.arbiter.droid.icebreakerprot1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import static com.arbiter.droid.icebreakerprot1.Common.image_viewer_mode;


/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<ImageRecyclerViewModel> modelList;

    private OnItemClickListener mItemClickListener;


    public ImageRecyclerViewAdapter(Context context, ArrayList<ImageRecyclerViewModel> modelList) {
        this.mContext = context;
        this.modelList = modelList;
    }

    public void updateList(ArrayList<ImageRecyclerViewModel> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_list_image, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        //Here you can fill your row view
        if (holder instanceof ViewHolder) {
            final ImageRecyclerViewModel model = getItem(position);
            ViewHolder genericViewHolder = (ViewHolder) holder;
            GlideApp.with(mContext).load(model.getUrl()).thumbnail(0.4F).into(genericViewHolder.imgUser);
        }
    }


    @Override
    public int getItemCount() {

        return modelList.size();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    private ImageRecyclerViewModel getItem(int position) {
        return modelList.get(position);
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position, ImageRecyclerViewModel model);
        void onDeleteClick(View view, int position, ImageRecyclerViewModel model);
        void onUploadClick(View view, int position, ImageRecyclerViewModel model);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgUser;
        private ImageButton deleteBtn;
        private ImageButton uploadBtn;
        private int mode;
        public ViewHolder(final View itemView) {
            super(itemView);
            // ButterKnife.bind(this, itemView);

            this.imgUser = (ImageView) itemView.findViewById(R.id.img_user);
            this.deleteBtn = itemView.findViewById(R.id.imageButton2);
            this.uploadBtn = itemView.findViewById(R.id.imageButton3);
            Log.v("myapp",mode+"");
            if(image_viewer_mode==1)
                this.deleteBtn.setVisibility(View.GONE);
            else if(image_viewer_mode==2)
                this.uploadBtn.setVisibility(View.GONE);
            else if(image_viewer_mode==3){
                this.deleteBtn.setVisibility(View.GONE);
                this.uploadBtn.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(itemView, getAdapterPosition(), modelList.get(getAdapterPosition()));
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onDeleteClick(itemView, getAdapterPosition(),modelList.get(getAdapterPosition()));
                }
            });
            uploadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onUploadClick(itemView, getAdapterPosition(),modelList.get(getAdapterPosition()));
                }
            });
        }
    }

}

