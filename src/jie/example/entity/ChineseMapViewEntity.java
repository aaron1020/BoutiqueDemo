package jie.example.entity;

import java.io.Serializable;

public class ChineseMapViewEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String province;

	public ChineseMapViewEntity() {
	}

	public ChineseMapViewEntity(int id) {
		this.id = id;
	}

	public ChineseMapViewEntity(int id, String province) {
		this(id);
		this.province = province;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	@Override
	public String toString() {
		return "ChineseMapViewEntity [id=" + id + ", province=" + province
				+ "]";
	}

}
