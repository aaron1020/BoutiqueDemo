package jie.example.entity;

public class DynamicAddViewEntity {

	private String beginTime;
	private String endTime;
	private String type;
	private String typeRadio;

	public DynamicAddViewEntity() {
	}

	public DynamicAddViewEntity(String type, String beginTime, String endTime,
			String typeRadio) {
		this.type = type;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.typeRadio = typeRadio;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getTypeRadio() {
		return typeRadio;
	}

	public void setTypeRadio(String typeRadio) {
		this.typeRadio = typeRadio;
	}

	@Override
	public String toString() {
		return "DynamicAddViewEntity [type=" + type + ", beginTime="
				+ beginTime + ", endTime=" + endTime + ", typeRadio="
				+ typeRadio + "]";
	}

}
