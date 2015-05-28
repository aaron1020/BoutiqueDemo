package jie.example.widget;

import java.io.Serializable;

public class ChooseCylinder implements Serializable {

	private static final long serialVersionUID = 1L;

	private float x;
	private float xnum;
	private float y;
	private float ynum;
	private int id;
	private String name;

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getXnum() {
		return xnum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setXnum(float xnum) {
		this.xnum = xnum;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getYnum() {
		return ynum;
	}

	public void setYnum(float ynum) {
		this.ynum = ynum;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ChooseCylinder [name=" + name + ", x=" + x + ", xnum=" + xnum
				+ ", y=" + y + ", ynum=" + ynum + ", id=" + id + "]";
	}

}
