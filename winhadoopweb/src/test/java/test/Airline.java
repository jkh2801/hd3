package test;

import org.apache.hadoop.io.Text;

public class Airline {
	private int year, month, arriveDelayTime, departureDelayTime, distance;
	private boolean arriveDelayAvailable = true;
	private boolean departureDelayAvailable = true;
	private boolean distanceAvailable = true;
	private String uniqueCarrier;
	public Airline(Text text) {
		try {
			String [] columns = text.toString().split(","); // csv 파일: ,로 데이터 구분
			year = Integer.parseInt(columns[0]); // 년도
			month = Integer.parseInt(columns[1]); // 월
			uniqueCarrier = columns[8]; // 항공사 코드 : PS
			// NA : 지연이 없다.
			if(!columns[15].equals("NA")) { // 도착지연에 대한 정보
				departureDelayTime = Integer.parseInt(columns[15]);
			} else {
				departureDelayAvailable = false;
			}
			if(!columns[14].equals("NA")) {
				arriveDelayTime = Integer.parseInt(columns[14]);
			} else {
				arriveDelayAvailable = false;
			}
			if(!columns[18].equals("NA")) {
				distance = Integer.parseInt(columns[18]);
			} else {
				distanceAvailable = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public int getYear() {
		return year;
	}
	public int getMonth() {
		return month;
	}
	public int getArriveDelayTime() {
		return arriveDelayTime;
	}
	public int getDepartureDelayTime() {
		return departureDelayTime;
	}
	public int getDistance() {
		return distance;
	}
	public boolean isArriveDelayAvailable() {
		return arriveDelayAvailable;
	}
	public boolean isDepartureDelayAvailable() {
		return departureDelayAvailable;
	}
	public boolean isDistanceAvailable() {
		return distanceAvailable;
	}
	public String getUniqueCarrier() {
		return uniqueCarrier;
	}
	
}