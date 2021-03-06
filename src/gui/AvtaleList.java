package gui;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import models.Appointment;
import models.AvtaleListModel;
import models.ParticipantEntity;
import renderers.AvtaleRenderer;
import db.DBConnection;
import db.ObjectFactory;

public class AvtaleList extends JList{
	private String date;

	public AvtaleList(String date){
		this(date, new AvtaleListModel<Appointment>());
	}
	public AvtaleList(String date, ArrayList<ParticipantEntity> employees){
		this.date = date;
		this.setCellRenderer((ListCellRenderer<? super Appointment>) new AvtaleRenderer());
		this.setModel(new AvtaleListModel<Appointment>());
		

		fetchApps(employees);

	}
	public AvtaleList(String date, AvtaleListModel<Appointment> model){
		this.date = date;
		this.setCellRenderer((ListCellRenderer<? super Appointment>) new AvtaleRenderer());
		this.setModel(model);
	}

	public void setDate(String date){
		this.date = date;
	}

	public void fetchApps(ArrayList<ParticipantEntity> employees){
		((DefaultListModel<Appointment>) this.getModel()).clear();
		//Hente avtaler fra databasen
		String employeesString = "";
		for (ParticipantEntity employee : employees){
			employeesString += "EAA.Username = '"+employee+"' ";
			if(!employee.equals(employees.get(employees.size()-1)))employeesString += " OR ";
		}

		DBConnection connection = null;
		ResultSet rs = null;
		try {
			connection = new DBConnection("src/db/props.properties", true);
			PreparedStatement pst = connection.prepareStatement(	
					"SELECT AP.AppointmentNumber, AP.AppointmentName, AP.StartTime, " +
							"AP.EndTime, AP.RoomNumber, EAA.Status, EAA.Edited " +
							"FROM (appointment AS AP) NATURAL JOIN (employeeappointmentalarm AS EAA)" +
							"WHERE (DATE(AP.StartTime)  = " + "'" +this.date+"'" +
									"AND ("+employeesString+"))");
			rs = pst.executeQuery();
			while (rs.next()) {
				Appointment app = new Appointment(rs.getInt(1), rs.getString(2), rs.getString(3),
						rs.getString(4), rs.getInt(5), rs.getString(6), rs.getInt(7));
				//app.setStatus(ObjectFactory.getStatus(employees[0], app));
				((AvtaleListModel<Appointment>) this.getModel()).addElement(app);
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
	}
	public void fetchApps(ParticipantEntity employee){
		ArrayList<ParticipantEntity> empList = new ArrayList<ParticipantEntity>();
		empList.add(employee);
		fetchApps(empList);
	}


}
