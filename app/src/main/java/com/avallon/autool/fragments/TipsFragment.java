package com.avallon.autool.fragments;

import java.util.Random;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.avallon.autool.R;

public class TipsFragment extends Fragment implements OnClickListener {

	private View mView;
	private TextView tipTitle;
	private TextView tipDescription;
	private Button prevButton;
	private Button nextButton;
	private String[] tipsTitles;
	private String[] tipsDescriptions;
	private int generalIndex;;
	private long mShortAnimationDuration;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.tips_fragment, container, false);

		return mView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		tipTitle = (TextView) mView.findViewById(R.id.tip_title);
		tipDescription = (TextView) mView.findViewById(R.id.tip_description);
		prevButton = (Button) mView.findViewById(R.id.prevTip_button);
		prevButton.setOnClickListener(this);
		nextButton = (Button) mView.findViewById(R.id.nextTip_button);
		nextButton.setOnClickListener(this);

		tipsTitles = getResources().getStringArray(R.array.tips_titles);
		tipsDescriptions = getResources().getStringArray(R.array.tips_descriptions);

		Random randomGenerator = new Random();
		mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

		generalIndex = randomGenerator.nextInt(tipsTitles.length);

		tipTitle.setText(String.format(getString(R.string.tip_title_format), (generalIndex + 1), tipsTitles[generalIndex]));
		tipDescription.setText(tipsDescriptions[generalIndex]);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.prevTip_button:
			tipTitle.animate().alpha(0f).setDuration(mShortAnimationDuration).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					if (generalIndex == -1) {
						generalIndex = (tipsTitles.length - 1);
					}
					tipTitle.setText(String.format(getString(R.string.tip_title_format), (generalIndex + 1), tipsTitles[generalIndex]));
					tipTitle.animate().alpha(1f).setDuration(mShortAnimationDuration).setListener(null);
				}
			});

			tipDescription.animate().alpha(0f).setDuration(mShortAnimationDuration).setListener(new AnimatorListenerAdapter() {

				@Override
				public void onAnimationEnd(Animator animation) {
					tipDescription.setText(tipsDescriptions[generalIndex]);
					tipDescription.animate().alpha(1f).setDuration(mShortAnimationDuration).setListener(null);
				}
			});
			generalIndex = generalIndex - 1;
			break;
		case R.id.nextTip_button:
			tipTitle.animate().alpha(0f).setDuration(mShortAnimationDuration).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					if (generalIndex == (tipsTitles.length)) {
						generalIndex = 0;
					}
					tipTitle.setText(String.format(getString(R.string.tip_title_format), (generalIndex + 1), tipsTitles[generalIndex]));
					tipTitle.animate().alpha(1f).setDuration(mShortAnimationDuration).setListener(null);
				}
			});

			tipDescription.animate().alpha(0f).setDuration(mShortAnimationDuration).setListener(new AnimatorListenerAdapter() {

				@Override
				public void onAnimationEnd(Animator animation) {
					tipDescription.setText(tipsDescriptions[generalIndex]);
					tipDescription.animate().alpha(1f).setDuration(mShortAnimationDuration).setListener(null);
				}
			});
			generalIndex = generalIndex + 1;
			break;
		default:
			break;
		}
	}

	public void shareTip() {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);

		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, String.format(getString(R.string.share_subject_format), tipTitle.getText().toString()));
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, tipDescription.getText().toString());

		startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
	}
}
