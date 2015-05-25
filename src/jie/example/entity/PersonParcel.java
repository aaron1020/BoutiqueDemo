package jie.example.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 实体类实现Percelable接口，可以按需对对象的某个或某几个字段进行序列化，因此使用Intent传输对象时，
 * 使用Percelable接口的效益可能会高一些 。注：需要序列化的字段必须是Intent所支持的数据类型。
 */
public class PersonParcel implements Parcelable {

	private String name;
	private int age;
	private String sex;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// 写出我们需要传输的字段
		dest.writeString(name);
		dest.writeInt(age);
	}

	// 需要提供一个Parcelable.Creator<实体类>常量
	public static final Parcelable.Creator<PersonParcel> CREATOR = new Parcelable.Creator<PersonParcel>() {

		@Override
		public PersonParcel createFromParcel(Parcel source) {
			// 读取的顺序一定要和我们刚才写出的顺序一样
			PersonParcel person = new PersonParcel();
			person.name = source.readString();
			person.age = source.readInt();
			return person;
		}

		@Override
		public PersonParcel[] newArray(int size) {
			return new PersonParcel[size];
		}
	};

	public PersonParcel() {
	}

	public PersonParcel(String name, int age, String sex) {
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
		return "PersonParcel [name=" + name + ", age=" + age + ", sex=" + sex
				+ "]";
	}

}
