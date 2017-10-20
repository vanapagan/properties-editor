package com.palo.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.palo.util.Action.Type.Activity;

public class Action {

	private Type type;
	private LocalDateTime dateTime;
	private List<String> list;

	public Action(Type type, LocalDateTime dateTime, List<String> list) {
		this.type = type;
		this.dateTime = LocalDateTime.now();
		this.list = list;
	}
	
	public Activity getActivity() {
		return type.getActivity(list);
	}
	
	public Type getType() {
		return type;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public enum Type {

		NEW_ITEM {

			@Override
			public Activity getActivity(List<String> list) {
				return super.getActivity(list, "Added new", "item");
			}

		},

		EDIT_ITEM {

			@Override
			public Activity getActivity(List<String> list) {
				return super.getActivity(list, "Edited", "item");
			}

		},

		REMOVE_ITEM {

			@Override
			public Activity getActivity(List<String> list) {
				return super.getActivity(list, "Removed", "item");
			}
			
		},
		
		LOAD_ITEM {
			
			@Override
			public Activity getActivity(List<String> list) {
				return super.getActivity(list, "Loaded", "item");
			}
			
		},

		REMOVE_LANGUAGE {

			@Override
			public Activity getActivity(List<String> list) {
				return super.getActivity(list, "Removed", "language");
			}

		},

		SAVE {

			@Override
			public Activity getActivity(List<String> list) {
				return super.getActivity(list, "Saved", "file");
			}

		};

		public abstract Activity getActivity(List<String> list);
		
		public Activity getActivity(List<String> list, String action, String subject) {
			String genericInfo = getGenericInformation(list, action, subject);
			List<String> detailedInfoList = getDetailedInfoList(list, action, subject);
			return new Activity(genericInfo, detailedInfoList);
		}

		public String getGenericInformation(List<String> list, String action, String subject) {
			int size = list.size();
			return size > 1 ? String.join(" ", action, Integer.toString(size), getSubject(size, subject))
					: String.join(" ", action, subject, list.get(0));
		}

		public List<String> getDetailedInfoList(List<String> list, String action, String subject) {
			return list.stream().map(l -> String.join(" ", action, subject, l)).collect(Collectors.toList());
		}

		private static String getSubject(int len, String subjectSingular) {
			return len > 1 ? subjectSingular + "s" : subjectSingular;
		}
		
		public class Activity {

			private String genericInfo;
			private List<String> detailedInfoList;

			public Activity(String genericInfo, List<String> detailedInfoList) {
				this.genericInfo = genericInfo;
				this.detailedInfoList = detailedInfoList;
			}

			public String getGenericInfo() {
				return genericInfo;
			}

			public List<String> getDetailedInfoList() {
				return detailedInfoList;
			}

		}

	}

}
