package models;

import java.util.Arrays;

import javax.swing.DefaultListModel;

public class AvtaleListModel<Appointment> extends DefaultListModel<Appointment>{
	
	public AvtaleListModel(){
		super();
	}
	
	public void sort(){
		Object[] contents = this.toArray();
		Arrays.sort(contents);
		for(int i = 0; i < this.size(); i++){
			this.setElementAt((Appointment) contents[i], i);
		}
	}
	
	public int getIndex(Appointment app){
		for (int i = 0; i < this.size(); i++){
			if(app.equals(this.get(i)))return i;
		}
		return -1;
	}

	

}
