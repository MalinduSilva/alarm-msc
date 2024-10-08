package com.malindu.alarm15.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.malindu.alarm15.R;
import com.malindu.alarm15.adapters.WorldClockRecyclerViewAdapter;
import com.malindu.alarm15.models.WorldClockItem;
import com.malindu.alarm15.utils.Constants;

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

public class ClockFragment extends Fragment {
    private static final String TAG = "ClockFragment";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private TextView clockHour, clockMinute, clockSecond, clockAmPm;
    private int screenRight, screenBottom;
    private RelativeLayout worldClockLayout;
    private ConstraintLayout clockLayout;
    private ScrollView scrollView;
    private FloatingActionButton fab;
    private WorldClockItem worldClockItem;
    private RecyclerView recyclerView;
    private WorldClockRecyclerViewAdapter adapter;

    private ImageView sun;
    private Slider slider;
    private int time;
    private int left, right, top, bottom;
    private int screenWidth;
    private SunPathView sunPathView;

    public ClockFragment() {
        // Required empty public constructor
    }
    public static ClockFragment newInstance() { return new ClockFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clock, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = requireContext().getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
        clockHour = view.findViewById(R.id.cardview_clock_hour);
        clockMinute = view.findViewById(R.id.cardview_clock_minute);
        clockSecond = view.findViewById(R.id.cardview_clock_second);
        clockAmPm = view.findViewById(R.id.cardview_clock_ampm);
        recyclerView = view.findViewById(R.id.recview);
        fab = view.findViewById(R.id.fab);
        screenBottom = view.getBottom();
        screenRight = view.getRight();
        clockLayout = view.findViewById(R.id.clock_layout);
        worldClockLayout = view.findViewById(R.id.worldclock_layout);
        scrollView = view.findViewById(R.id.scrollView);
        worldClockItem = new WorldClockItem(WorldClockItem.getAllTimeZones().get(0).getCity(), WorldClockItem.getAllTimeZones().get(0).getTimeZoneID());
        sun = view.findViewById(R.id.image_sun);
        slider = view.findViewById(R.id.slider);
        //getCoordinatesOfClock(view);

//        clockLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                clockLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                clockLayout.setMinHeight((int) (clockLayout.getWidth() * 0.7));
//                // Check if the ScrollView content is empty
//                if (scrollView.getChildCount() == 0) {
//                    // Set the height to the screen height
//                    ViewGroup.LayoutParams params = clockLayout.getLayoutParams();
//                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
//                    clockLayout.setLayoutParams(params);
//                }
//                getCoordinatesOfClock(view);
////                ViewGroup.LayoutParams params = clockLayout.getLayoutParams();
////                if (params.height < (int) (0.7 * params.width)) {
////                    params.height = (int) (0.7 * params.width);
////                }
////                clockLayout.setLayoutParams(params);
//            }
//        });
//        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                int maxClockHeight = (int) (view.getWidth() * 0.7);
//                int maxWorldClockLayoutHeight = view.getHeight() - maxClockHeight;
//                ViewGroup.LayoutParams params = scrollView.getLayoutParams();
//                int h = scrollView.getHeight();
//                Log.d(TAG, "onGlobalLayout: " + view.getHeight() +","+ view.getWidth()+","+maxClockHeight+","+maxWorldClockLayoutHeight+","+params.height+","+ params.width+","+scrollView.getHeight());
//                if (scrollView.getHeight() > maxWorldClockLayoutHeight) {
//                    Log.d(TAG, "onGlobalLayout: true");
//                    params.height = maxWorldClockLayoutHeight;
//                    //params.height = maxWorldClockLayoutHeight;
//                    scrollView.setLayoutParams(params);
//                }
//            }
//        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.worldclock_add_title)
                        .setSingleChoiceItems(TimeZone.getAvailableIDs(), 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String tid = TimeZone.getAvailableIDs()[which];
                                String cityName = tid.substring(tid.lastIndexOf('/') + 1).replace('_', ' ');
                                worldClockItem = new WorldClockItem(cityName, tid);
                            }
                        })
                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "onClick: test1");
                                if (sharedPreferences.getString(Constants.WORLD_CLOCK_ITEM_KEY + worldClockItem.getCity(), "").isEmpty()) {
                                    Log.d(TAG, "onClick: test");
                                    sharedPreferencesEditor = sharedPreferences.edit();
                                    sharedPreferencesEditor.putString(Constants.WORLD_CLOCK_ITEM_KEY + worldClockItem.getCity(), worldClockItem.toString());
                                    sharedPreferencesEditor.apply();
                                    adapter.updateData();
                                } else {
                                    Toast.makeText(requireContext(), getString(R.string.toast_city_exists), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                worldClockItem = null;
                            }
                        })
                        //.setPositiveButtonIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.icon_plus_sign))
                        .show();
            }
        });
        fab.setVisibility(View.GONE);
        adapter = new WorldClockRecyclerViewAdapter(requireContext());
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        gridLayoutManager.setReverseLayout(true);
//        gridLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(gridLayoutManager);
//        slider.addOnChangeListener(new Slider.OnChangeListener() {
//            @Override
//            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
//                time = (int) value;
//                positionSun();
//            }
//        });
//        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                // Remove the listener to avoid multiple calls
//                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//                // Now you can safely get the view dimensions and position the sun
//                screenWidth = view.getWidth();
//                getCoordinatesOfClock(view);
//                positionSun();
//            }
//        });
//        sunPathView = view.findViewById(R.id.sunpath);
//        sunPathView.post(() -> {
//            int width = sunPathView.getWidth();
//            int height = sunPathView.getHeight();
//            int radius = Math.min(width, height) / 2;
//
//            Path path = new Path();
//            path.addArc(new RectF(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius),
//                    180, 180);
//
////            ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, View.X, View.Y, path);
////            animator.setDuration(2000);
////            animator.setInterpolator(new LinearInterpolator());
////            animator.setRepeatCount(ValueAnimator.INFINITE);
////            animator.setRepeatMode(ValueAnimator.REVERSE);
////            animator.start();
//        });
    }

    private void positionSun() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24-hour format
        int minute = calendar.get(Calendar.MINUTE);
        double hourFraction = time; //hour + minute / 60.0;
        double angle = Math.toRadians((hourFraction - 6) * 15);
        int centerX = screenWidth / 2;
        int centerY = bottom;
        int iconWidth = sun.getWidth();
        int radius = centerX + (iconWidth / 2);
        int x = (int) (centerX - radius * Math.cos(angle));
        int y = (int) (centerY - radius * Math.sin(angle));
        sun.setX(x);
        sun.setY(y);
        Log.d(TAG, "positionSun: " +screenWidth+":"+ x + ":" + y);
    }

    public void getCoordinatesOfClock(View view) {
        ConstraintLayout clockLayout = view.findViewById(R.id.clock);
        clockLayout.post(() -> {
            Rect rect = new Rect();
            clockLayout.getWindowVisibleDisplayFrame(rect);
            left = clockLayout.getLeft();
            top = clockLayout.getTop();
            right = clockLayout.getRight();
            bottom = clockLayout.getBottom();
            Log.d("ClockLayoutCoordinates", "Left: " + left + ", Top: " + top + ", Right: " + right + ", Bottom: " + bottom);
            Log.d(TAG, "Fragment dimensions: " + view.getLeft()+", "+view.getTop()+", "+view.getRight()+", "+view.getBottom());
            Log.d(TAG, "Screen dimensions: height-" + view.getContext().getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE).getInt(Constants.DISPLAY_BOTTOM_EDGE_KEY, 1)
                + ", width-" + view.getContext().getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE).getInt(Constants.DISPLAY_RIGHT_EDGE_KEY, 1));
        });
    }
}