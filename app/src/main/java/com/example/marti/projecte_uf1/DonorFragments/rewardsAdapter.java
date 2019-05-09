package com.example.marti.projecte_uf1.DonorFragments;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marti.projecte_uf1.MainActivity;
import com.example.marti.projecte_uf1.NotificationActionReceiver;
import com.example.marti.projecte_uf1.R;
import com.example.marti.projecte_uf1.SQLiteManager;
import com.example.marti.projecte_uf1.interfaces.ApiMecAroundInterfaces;
import com.example.marti.projecte_uf1.model.Reward;
import com.example.marti.projecte_uf1.model.RewardInfoLang;
import com.example.marti.projecte_uf1.remote.ApiUtils;
import com.example.marti.projecte_uf1.utils.NotificationHelper;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class rewardsAdapter extends RecyclerView.Adapter<rewardsAdapter.MyViewHolder> {

    private ArrayList<Reward> list;
    private Context context;
    public static final String EXTRA_ID = "ID";
    SQLiteManager manager = new SQLiteManager(context);
    private String sharedPrefFile = "prefsFile";
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private int currentUserId;
    public ApiMecAroundInterfaces mAPIService;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView rewardTitle;
        public LinearLayout linearLayout;
        public LinearLayout linearLayoutHeader;
        public LinearLayout linearLayoutContent;
        public TextView rewardInfo;
        public TextView rewardPoints;
        public ImageView rewardImage;
        public Button claimRewardBt;

        public MyViewHolder(View view) {
            super(view);
            rewardTitle = view.findViewById(R.id.RewardTitle);
            linearLayout = view.findViewById(R.id.rewardLayout);
            linearLayoutHeader = view.findViewById(R.id.rewardHeaderLaout);
            linearLayoutContent = view.findViewById(R.id.RewardLayoutContent);
            rewardInfo = view.findViewById(R.id.rewardInfo);
            rewardPoints = view.findViewById(R.id.RewardPoints);
            rewardImage = view.findViewById(R.id.rewardImageExpand);
            claimRewardBt = view.findViewById(R.id.claimRewardButton);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public rewardsAdapter(ArrayList<Reward> rewardsList, Context context, int currentUserId0) {
        this.list = rewardsList;
        this.context = context;
        this.currentUserId = currentUserId0;
    }

    @NonNull
    @Override
    public rewardsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View item = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rewards_list_item, viewGroup, false);


        mAPIService = ApiUtils.getAPIService();

        return new MyViewHolder(item);
    }


    @Override
    public void onBindViewHolder(@NonNull final rewardsAdapter.MyViewHolder myViewHolder, final int i) {
        final Reward reward = list.get(i);
//        if (reward == null) {
//            return;
//        }
//
//        if (reward.id == null){
//            return;
//        }
        RewardInfoLang infoEng = new RewardInfoLang();
        for (RewardInfoLang item : reward.getRewardInfoLangs()) {
            if (item.languageId == 1) { //TODO:  s' hauria de posar aixi pero peta el segon item item.language.code.equalsIgnoreCase("en")
                infoEng = item;
            }
        }


        myViewHolder.rewardTitle.setText(infoEng.title);
        myViewHolder.rewardPoints.setText(String.valueOf("Points: " + reward.neededPoints));
        myViewHolder.rewardInfo.setText(infoEng.description);

        myViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup group = (ViewGroup) myViewHolder.linearLayout;
                //Reward esd = list.get(i);
                //esdG = esd;
                if (myViewHolder.linearLayoutContent.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(group);
                    myViewHolder.linearLayoutContent.setVisibility(View.VISIBLE);
                    myViewHolder.rewardImage.animate().rotation(180).setDuration(400).start();


                } else {

                    myViewHolder.rewardImage.animate().rotation(0).setDuration(400).start();
                    myViewHolder.linearLayoutContent.setVisibility(View.GONE);

                }
            }
        });

        myViewHolder.claimRewardBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int rewardId = reward.id.intValue();
                mAPIService.claimReward(rewardId, currentUserId).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful()) {
                            if (response.body()) {
                                Toast.makeText(context, "Reward claimed", Toast.LENGTH_SHORT).show();
                                NotificationHelper nHelper = new NotificationHelper(context);
                                nHelper.createNotificationRewardClaimed("Reward claimed!", "Check your inbox for more information.");

                                list.remove(i);
                                notifyItemRemoved(i);
                                notifyItemRangeChanged(i, list.size());

                            } else {
                                Toast.makeText(context, "You don't have enough points", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(context, response.code() + response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        Toast.makeText(context, "Server error " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


            }
        });


    }


    @Override
    public int getItemCount() {
        return list.size();
    }


}
