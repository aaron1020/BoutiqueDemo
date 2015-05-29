package jie.example.widget;

import java.io.Serializable;

/**
 * 柱状图实体类
 */
public class HistogramEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String color;
	private float y;

	public HistogramEntity() {
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "ImageData [name=" + name + ", y=" + y + ", color=" + color
				+ "]";
	}

}
