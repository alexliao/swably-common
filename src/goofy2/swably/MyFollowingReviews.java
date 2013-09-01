package goofy2.swably;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;

import goofy2.swably.About;
import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.SelectLocalAppToReview;
import goofy2.swably.TabStripActivity;
import goofy2.swably.Utils;
import goofy2.swably.fragment.MyFollowingReviewsFragment;
import goofy2.swably.fragment.MyReviewsFragment;
import goofy2.swably.fragment.PublicReviewsFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MyFollowingReviews extends PeopleReviews {

    void setContent(){
        setContentView(R.layout.my_following_reviews);
    }

}
