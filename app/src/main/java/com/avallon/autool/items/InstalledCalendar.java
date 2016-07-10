package com.avallon.autool.items;

import com.avallon.autool.utils.Utils;

import android.database.Cursor;

public class InstalledCalendar {

	private long id;
	private String displayName;
	private String accountName;
	private String ownerName;

	public InstalledCalendar createInstalledCalendar(Cursor cursor) {
		setId(cursor.getLong(Utils.PROJECTION_ID_INDEX));
		setDisplayName(cursor.getString(Utils.PROJECTION_DISPLAY_NAME_INDEX));
		setAccountName(cursor.getString(Utils.PROJECTION_ACCOUNT_NAME_INDEX));
		setOwnerName(cursor.getString(Utils.PROJECTION_OWNER_ACCOUNT_INDEX));

		return this;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

}
