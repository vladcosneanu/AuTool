package com.avallon.autool.adapters;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avallon.autool.R;
import com.avallon.autool.items.ServiceItem;
import com.avallon.autool.requests.RequestService;
import com.avallon.autool.utils.FormatHelper;
import com.avallon.autool.utils.PreferenceHelper;

public class ServicesListAdapter extends ArrayAdapter<ServiceItem> {

	private Context context;
	private List<ServiceItem> serviceItems;
	private int layoutResource;
	private String type;

	public ServicesListAdapter(Context context, int resource, List<ServiceItem> serviceItems, String type) {
		super(context, resource, serviceItems);

		this.context = context;
		this.layoutResource = resource;
		this.serviceItems = serviceItems;
		this.type = type;
	}

	public ServiceItem getItem(int position) {
		return serviceItems.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		boolean isMetric = PreferenceHelper.isMetric(context);
		final ServiceItem serviceItem = getItem(position);
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			rowView = inflater.inflate(layoutResource, parent, false);
			ViewHolder viewHolder = new ViewHolder();

			viewHolder.serviceIcon = (ImageView) rowView.findViewById(R.id.serviceIcon);
			viewHolder.serviceName = (TextView) rowView.findViewById(R.id.serviceName);
			viewHolder.serviceVicinity = (TextView) rowView.findViewById(R.id.serviceVicinity);
			viewHolder.serviceDistance = (TextView) rowView.findViewById(R.id.serviceDistance);
			viewHolder.serviceDuration = (TextView) rowView.findViewById(R.id.serviceDuration);
			
			rowView.setTag(viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) rowView.getTag();
		
		if (type.equals(RequestService.GAS_STATION)) {
			viewHolder.serviceIcon.setImageResource(R.drawable.gas2);
		} else if (type.equals(RequestService.CAR_REPAIR)) {
			viewHolder.serviceIcon.setImageResource(R.drawable.settings2);
		} else if (type.equals(RequestService.CAR_WASH)) {
			viewHolder.serviceIcon.setImageResource(R.drawable.car_wash2);
		} else if (type.equals(RequestService.PARKING)) {
			viewHolder.serviceIcon.setImageResource(R.drawable.parking2);
		}
		
		viewHolder.serviceName.setText(serviceItem.getName());
		viewHolder.serviceVicinity.setText(serviceItem.getVicinity());
		
		NumberFormat formatter = new DecimalFormat("#.#");
		String totalDistanceValue = "";
		
		if (isMetric) {
			totalDistanceValue = String.format(context.getResources().getString(R.string.distance_value_km), formatter.format((float)serviceItem.getDistance()/1000));
		} else {
			float totalDistanceMiles = (float) ((serviceItem.getDistance() / 1000) * 0.621371192);
			totalDistanceValue = String.format(context.getResources().getString(R.string.distance_value_mi), formatter.format((float)totalDistanceMiles));
		}
		viewHolder.serviceDistance.setText(totalDistanceValue);
		
		viewHolder.serviceDuration.setText(FormatHelper.getTime(serviceItem.getDuration(), true));

		return rowView;
	}

	static class ViewHolder {
		private ImageView serviceIcon;
		private TextView serviceName;
		private TextView serviceVicinity;
		private TextView serviceDistance;
		private TextView serviceDuration;
	}
}
