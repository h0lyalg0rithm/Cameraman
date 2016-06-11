package com.surajms.cameraman;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by surajshirvankar on 6/11/16.
 */
public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder> {
    private ArrayList<ImageData> imageDataArrayList;
    private OnItemClickListener listener;
    Context context;
    private int maxImages;

    public void setImages(ArrayList<ImageData> images) {
        this.imageDataArrayList = images;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ImageData item);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ImagePreviewAdapter(Context context, ArrayList<ImageData> imageDataArrayList) {
        this.context = context;
        this.imageDataArrayList = imageDataArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_preview_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (imageDataArrayList.size() > 0 && imageDataArrayList.size() > position)
            holder.setData(position, imageDataArrayList.get(position));
        else
            holder.setData(position, null);
    }

    public void setMaxImageCount(int size) {
        maxImages = size;
    }

    @Override
    public int getItemCount() {
        return maxImages;
    }

    public void deleteImageData(int position, ArrayList<ImageData> imageDatas) {
        imageDatas.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, imageDatas.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView previewImage;

        public ViewHolder(View itemView) {
            super(itemView);
            previewImage = (ImageView) itemView.findViewById(R.id.preview_image);
        }

        public void setData(final int position, final ImageData imageData) {
            if (imageData != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(imageData.getThumbnail(), options);
                previewImage.setImageBitmap(bitmap);
                previewImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(position, imageData);
                    }
                });
            } else {
                Resources resources = context.getResources();
                previewImage.setImageDrawable(resources.getDrawable(R.drawable.default_preview_image));
            }
        }
    }
}
