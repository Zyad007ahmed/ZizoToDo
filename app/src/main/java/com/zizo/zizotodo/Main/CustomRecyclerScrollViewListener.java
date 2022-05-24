package com.zizo.zizotodo.Main;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class CustomRecyclerScrollViewListener extends RecyclerView.OnScrollListener {
    int scrollDist = 0;
    boolean isVisible = true;
    static final float MINIMUM = 20;

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (isVisible && scrollDist > MINIMUM) {
            hide();
            isVisible = false;
            scrollDist = 0;
        } else if (!isVisible && scrollDist < -MINIMUM){
            show();
            isVisible =  true;
            scrollDist =0;
        }

        if((isVisible && dy > 0) || (!isVisible && dy < 0)){
            scrollDist += dy;
        }
    }

    public abstract void show();

    public abstract void hide();
}
