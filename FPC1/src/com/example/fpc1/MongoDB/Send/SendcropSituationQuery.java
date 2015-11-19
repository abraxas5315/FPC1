package com.example.fpc1.MongoDB.Send;

public class SendcropSituationQuery {
	public String workerID; 
	public String areaID;
	public String fieldID;
	public int vegeCode;
	
	public SendcropSituationQuery(String workerID , String areaID , String fieldID , int vegeCode){
		this.workerID = workerID ;
		this.areaID = areaID ;
		this.fieldID = fieldID ; 
		this.vegeCode = vegeCode ;
	}
	
}
