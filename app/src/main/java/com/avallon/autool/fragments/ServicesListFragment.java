package com.avallon.autool.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.avallon.autool.R;
import com.avallon.autool.activities.MainActivity;
import com.avallon.autool.adapters.ServicesListAdapter;
import com.avallon.autool.items.ServiceItem;

public class ServicesListFragment extends Fragment implements OnItemClickListener {

	private View mView;
	private ListView servicesList;
	private ServicesListAdapter servicesListAdapter;
	private View servicesListProgressLayout;
	private View noResultsLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.services_list_fragment, container, false);

		return mView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		servicesList = (ListView) mView.findViewById(R.id.servicesList);
		servicesListProgressLayout = mView.findViewById(R.id.services_list_progress_layout);
		noResultsLayout = mView.findViewById(R.id.no_results_layout);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		loadData();
	}
	
	public void loadData() {
		if (ServicesFragment.serviceItems == null || getActivity() == null) {
			return;
		}
		
		if (ServicesFragment.serviceItems.size() > 0) {
			displayList();
			
			servicesListAdapter = new ServicesListAdapter(getActivity(), R.layout.service_item, ServicesFragment.serviceItems,
					ServicesFragment.selectedType);
			servicesList.setAdapter(servicesListAdapter);
			servicesList.setOnItemClickListener(this);
		} else {
			servicesListProgressLayout.setVisibility(View.GONE);
			servicesList.setVisibility(View.GONE);
			noResultsLayout.setVisibility(View.VISIBLE);
		}
	}
	
	public void displayProgress() {
		servicesListProgressLayout.setVisibility(View.VISIBLE);
		servicesList.setVisibility(View.GONE);
		noResultsLayout.setVisibility(View.GONE);
	}
	
	
	public void displayList() {
		servicesListProgressLayout.setVisibility(View.GONE);
		servicesList.setVisibility(View.VISIBLE);
		noResultsLayout.setVisibility(View.GONE);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		ServiceItem serviceItem = ServicesFragment.serviceItems.get(position);
		ServicesFragment.selectedService = serviceItem;
		
		((MainActivity) getActivity()).toggleServicesMapList(serviceItem);
	}
}
