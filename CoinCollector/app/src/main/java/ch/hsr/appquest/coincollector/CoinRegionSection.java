package ch.hsr.appquest.coincollector;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class CoinRegionSection extends Section {

    private CoinRegion coinRegion;
    private String title;
    private Context context;

    CoinRegionSection(CoinRegion coinRegion, Context context) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.section_item)
                .headerResourceId(R.layout.section_header)
                .build());
        this.coinRegion = coinRegion;
        this.title = coinRegion.getRegionName();
        this.context = context;
    }

    @Override
    public int getContentItemsTotal() {
        return coinRegion.getCoinList().size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new CoinItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        CoinItemViewHolder coinItemViewHolder = (CoinItemViewHolder) holder;
        // TODO: Befülle den CoinItemViewHolder mit den Informationen der Münze an der angegebenen Position.
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new CoinRegionHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        CoinRegionHeaderViewHolder headerHolder = (CoinRegionHeaderViewHolder) holder;
        headerHolder.getSectionTitleView().setText(title);
    }

}
