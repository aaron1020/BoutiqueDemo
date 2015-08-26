package jie.example.entity;

public class TimeEntity {

	private long between, day, hour, minute, second;

	public TimeEntity(long between, long day, long hour, long minute,
			long second) {
		this.between = between;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}

	public long getBetween() {
		return between;
	}

	public long getDay() {
		return day;
	}

	public long getHour() {
		return hour;
	}

	public long getMinute() {
		return minute;
	}

	public long getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return "TimeEntity [day=" + day + ", hour=" + hour + ", minute="
				+ minute + ", second=" + second + "]";
	}

}
