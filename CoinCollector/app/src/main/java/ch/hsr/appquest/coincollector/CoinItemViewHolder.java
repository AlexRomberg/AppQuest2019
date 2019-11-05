package ch.hsr.appquest.coincollector;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;

public class CoinItemViewHolder extends RecyclerView.ViewHolder {

    private final ImageView coinImageView;

    public CoinItemViewHolder(View itemView) {
        super(itemView);
        coinImageView = itemView.findViewById(R.id.coinImageView);
    }

    public ImageView getCoinImageView() { return coinImageView; }

}
