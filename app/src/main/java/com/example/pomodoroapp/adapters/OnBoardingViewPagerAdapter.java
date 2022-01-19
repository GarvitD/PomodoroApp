package com.example.pomodoroapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.example.pomodoroapp.R;

public class OnBoardingViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    public OnBoardingViewPagerAdapter(Context context) {
        this.context = context;
    }

    int[] imageIds = new int[] {R.drawable.graph_image,R.drawable.graph_image,R.drawable.graph_image};
    String[] title = new String[] {"PG1","PG2","PG3", "PG4"};
    String[] description = new String[] {"This is the first page", "This is the second page", "This is the third page", "This is the fourth page"};

    @Override
    public int getCount() {
        return imageIds.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.onboarding_viewpager_layout, container, false);

        ImageView img = view.findViewById(R.id.onboarding_viewPager_img);
        TextView txtMain = view.findViewById(R.id.onboarding_viewPager_txt1);
        TextView txtDescp = view.findViewById(R.id.onboarding_viewPager_txt2);

        img.setImageResource(imageIds[position]);
        txtMain.setText(title[position]);
        txtDescp.setText(description[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
