package jie.example.widget;

import java.io.Serializable;

/**
 * 柱子属性，用来点击跳转到下一个
 * 
 * @author kenneth
 * 
 */
public class PillarVO implements Serializable {

	private static final long serialVersionUID = 1L;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getRegpillar() {
		return regpillar;
	}

	public void setRegpillar(float regpillar) {
		this.regpillar = regpillar;
	}

	public float getAzurepillar() {
		return azurepillar;
	}

	public void setAzurepillar(float azurepillar) {
		this.azurepillar = azurepillar;
	}

	@Override
	public String toString() {
		return "PillarVO [name=" + name + ", regpillar=" + regpillar
				+ ", azurepillar=" + azurepillar + ", xpillar=" + xpillar
				+ ", regpillarsize=" + regpillarsize + ", azurepillarsize="
				+ azurepillarsize + "]";
	}

	private String name;
	private float regpillar;
	private float azurepillar;

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	private String name2;

	public float getXpillar() {
		return xpillar;
	}

	public void setXpillar(float xpillar) {
		this.xpillar = xpillar;
	}

	private float xpillar;
	private int regpillarsize;

	public int getRegpillarsize() {
		return regpillarsize;
	}

	public void setRegpillarsize(int regpillarsize) {
		this.regpillarsize = regpillarsize;
	}

	public float getAzurepillarsize() {
		return azurepillarsize;
	}

	public void setAzurepillarsize(float azurepillarsize) {
		this.azurepillarsize = azurepillarsize;
	}

	private float azurepillarsize;
}
