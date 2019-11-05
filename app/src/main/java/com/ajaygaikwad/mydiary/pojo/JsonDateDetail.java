package com.ajaygaikwad.mydiary.pojo;

import java.util.List;

public class JsonDateDetail{
	private List<DetailsItem> details;

	public void setDetails(List<DetailsItem> details){
		this.details = details;
	}

	public List<DetailsItem> getDetails(){
		return details;
	}
}