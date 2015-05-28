package jie.example.widget;

import java.io.Serializable;

public class ImageData implements Serializable {

	/**
	 * 柱状图实体类
	 */
	private static final long serialVersionUID = 1L;

	private String t1;
	private String t2;
	private String t3;
	private String name;
	private String province;
	private float y;
	private float business_volum;
	private float consumption;
	private float process;
	private float load_pressure;
	private String server_style;
	private String time;
	private long endTime;
	private long startTime;
	private String color;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getShowid1() {
		return showid1;
	}

	public void setShowid1(int showid1) {
		this.showid1 = showid1;
	}

	private int showid1 = 0;
	private int showid2 = 0;
	private int showid3 = 0;

	public int getShowid2() {
		return showid2;
	}

	public void setShowid2(int showid2) {
		this.showid2 = showid2;
	}

	public int getShowid3() {
		return showid3;
	}

	public void setShowid3(int showid3) {
		this.showid3 = showid3;
	}

	public ImageData() {
		t1 = "";
		t2 = "";
		t3 = "";
		name = "";
		province = "";
		y = 0;
		business_volum = 0;
		consumption = 0;
		process = 0;
		load_pressure = 0;
		server_style = "";
		time = "";
		endTime = 0;
		startTime = 0;
	}

	@Override
	public String toString() {
		return "ImageData [t1=" + t1 + ", t2=" + t2 + ", t3=" + t3 + ", name="
				+ name + ", province=" + province + ", y=" + y
				+ ", business_volum=" + business_volum + ", consumption="
				+ consumption + ", process=" + process + ", load_pressure="
				+ load_pressure + ", server_style=" + server_style + ", time="
				+ time + ", endTime=" + endTime + ", startTime=" + startTime
				+ ", color=" + color + ", showid1=" + showid1 + ", showid2="
				+ showid2 + ", showid3=" + showid3 + "]";
	}

	public String getT1() {
		return t1;
	}

	public void setT1(String t1) {
		this.t1 = t1;
	}

	public String getT2() {
		return t2;
	}

	public void setT2(String t2) {
		this.t2 = t2;
	}

	public String getT3() {
		return t3;
	}

	public void setT3(String t3) {
		this.t3 = t3;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getBusiness_volum() {
		return business_volum;
	}

	public void setBusiness_volum(float business_volum) {
		this.business_volum = business_volum;
	}

	public float getConsumption() {
		return consumption;
	}

	public void setConsumption(float consumption) {
		this.consumption = consumption;
	}

	public float getProcess() {
		return process;
	}

	public void setProcess(float process) {
		this.process = process;
	}

	public float getLoad_pressure() {
		return load_pressure;
	}

	public void setLoad_pressure(float load_pressure) {
		this.load_pressure = load_pressure;
	}

	public String getServer_style() {
		return server_style;
	}

	public void setServer_style(String server_style) {
		this.server_style = server_style;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

}
