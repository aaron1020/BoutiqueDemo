package jie.example.entity;

import java.io.Serializable;

/**
 * 实体类实例Serializable接口，可以把整个对象进行序列化。注：这个类存在的意义是和PersonParcel作比较
 */
public class PersonSerialize implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private int age;
	private String sex;

	public PersonSerialize() {
	}

	public PersonSerialize(String name, int age, String sex) {
		this.name = name;
		this.age = age;
		this.sex = sex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@Override
	public String toString() {
		return "PersonSerialize [name=" + name + ", age=" + age + ", sex="
				+ sex + "]";
	}

}
