package com.iBeiKe.InfoPortal.database;

import android.database.Cursor;

import com.iBeiKe.InfoPortal.common.ComTimes;

public class ClassQuery extends Query {
	int floor1;
	int floor2;
	int classes1;
	int classes2;
	boolean build1;
	boolean build2;
	private String[] column;
	private String WHERE;
	private String tableName;
	private String selection;
	private String orderBy;
    private ComTimes times;
    public ClassQuery() {
		column = new String[]{"room"};
		orderBy = " room ASC ";
		times = new ComTimes();
    }
	public void setQuery(int floorNum1, int floorNum2, int classesNum1, int classesNum2,
			boolean buildName1, boolean buildName2, long timeMillis) {
		floor1 = floorNum1;
		floor2 = floorNum2;
		classes1 = classesNum1;
		classes2 = classesNum2;
		build1 = buildName1;
		build2 = buildName2;
		times.setTime(timeMillis);
	}
	public int[] getArray() {
		WHERECreater WHERE = new WHERECreater();
		selection = WHERE.getWHERE();
		Cursor cursor = getCursor(tableName, column, selection, orderBy);
		int[] rooms = getIntResults(cursor);
		return rooms;
	}
	
	/**********************An inner class to build the query WHERE.**************/
	class WHERECreater {
		private void turnASC(int num1, int num2) {
			if(num1 > num2) {
				int swap;
				swap = num1;
				num1 = num2;
				num2 = swap;
			}
		}
		private String buildHandler(boolean buildName1, boolean buildName2) {
			String buildWHERE = "";
			if(!(buildName1 || buildName2)) {
				buildName1 = true;
				buildName2 = true;
			}
			if(buildName1) {
				buildWHERE += "build = 0 ";
			}
			if(buildName2) {
				buildWHERE += "build = 1 ";
			}
			return buildWHERE;
		}
		String getWHERE() {
			int room1;
			int room2;
			String className;
			//int weekInTerm = times.getWeekInTerm();
			int weekInTerm = 1;
			int weekNum = 1 << (weekInTerm-1);
			turnASC(floor1,floor2);
			room1 = floor1 * 100;
			room2 = (floor2 + 1) * 100;
			if(floor1 == 0) {
				WHERE = "";
				WHERE = "";
			} else {
				WHERE = "room > " + room1 + " and room < " + room2 + " and ";
			}
			turnASC(classes1, classes2);
			if(classes1 != classes2) {
				for(int i=classes1; i<classes2; i++) {
					className = "class" + i;
					WHERE += className + " & " + weekNum + " = " + weekNum + " and ";
				}
			}
			className = "class" + classes2;
			WHERE += className + " & " + weekNum + " = " + weekNum + " and ";
			WHERE += buildHandler(build1, build2);
			return WHERE;
		}
	}
}
