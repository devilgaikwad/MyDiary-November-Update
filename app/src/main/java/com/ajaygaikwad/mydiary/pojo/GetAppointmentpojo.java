package com.ajaygaikwad.mydiary.pojo;

import java.util.List;

public class GetAppointmentpojo{
	private List<AppointmentItem> appointment;

	public void setAppointment(List<AppointmentItem> appointment){
		this.appointment = appointment;
	}

	public List<AppointmentItem> getAppointment(){
		return appointment;
	}

	@Override
 	public String toString(){
		return 
			"GetAppointmentpojo{" + 
			"appointment = '" + appointment + '\'' + 
			"}";
		}
}