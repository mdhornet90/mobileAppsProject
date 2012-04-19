package com.android.googleplaces;

public class GooglePlacesGenericResult {

	private GPEvent[] events;
	private String iconURL;
	private GPGeometry geometry;
	private String name;
	private String rating;
	private String[] types;
	private String vicinity;

	public static class GPEvent {
		private String eventID;
		private String summary;
		private String eventURL;
		private String eventTime;
		
		public String getEventTime() {
			return eventTime;
		}
	
		public void setEventTime(String eventTime) {
			this.eventTime = eventTime;
		}
	
		public GPEvent(String eventID, String summary, String eventURL) {
			this(eventID, summary, eventURL, null);
		}
		
		public GPEvent(String eventID, String summary, String eventURL, String eventTime) {
			this.eventID = eventID;
			this.summary = summary;
			this.eventURL = eventURL;
			this.eventTime = eventTime;
		}
	
		public String getEventID() {
			return eventID;
		}
	
		public void setEventID(String eventID) {
			this.eventID = eventID;
		}
	
		public String getSummary() {
			return summary;
		}
	
		public void setSummary(String summary) {
			this.summary = summary;
		}
	
		public String getEventURL() {
			return eventURL;
		}
	
		public void setEventURL(String eventURL) {
			this.eventURL = eventURL;
		}
		
	}

	public GPEvent[] getEvents() {
		return events;
	}

	public void setEvents(GPEvent[] events) {
		this.events = events;
	}

	public String getIconURL() {
		return iconURL;
	}

	public void setIconURL(String iconURL) {
		this.iconURL = iconURL;
	}

	public GPGeometry getGeometry() {
		return geometry;
	}

	public void setGeometry(GPGeometry geometry) {
		this.geometry = geometry;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

	public String getVicinity() {
		return vicinity;
	}

	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}

	public static class GPGeometry {
		private GPLocation location;
		private String viewport;
		
		public GPGeometry(String longitude, String latitude, String viewport) {
			this.location = new GPLocation(longitude, latitude);
			this.viewport = viewport;
		}
	
		public GPGeometry(String longitude, String latitude) {
			this(longitude, latitude, null);
		}
		
		public GPLocation getLocation() {
			return location;
		}
	
		public void setLocation(GPLocation location) {
			this.location = location;
		}
	
		public String getViewport() {
			return viewport;
		}
	
		public void setViewport(String viewport) {
			this.viewport = viewport;
		}
		
		private class GPLocation {
			private String longitude;
			private String latitude;
			
			private GPLocation(String longitude, String latitude) {
				this.longitude = longitude;
				this.latitude = latitude;
			}
			public String getLongitude() {
				return longitude;
			}
			public void setLongitude(String longitude) {
				this.longitude = longitude;
			}
			public String getLatitude() {
				return latitude;
			}
			public void setLatitude(String latitude) {
				this.latitude = latitude;
			}
		}
	}

	public GooglePlacesGenericResult() {
		super();
	}

}