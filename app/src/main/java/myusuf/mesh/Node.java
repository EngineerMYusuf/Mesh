package myusuf.mesh;

import android.os.Parcel;
import android.os.Parcelable;


public class Node implements Parcelable {
    int num;
    int type;
    String data;

    public static final Creator<Node> CREATOR = new Creator<Node>() {
        @Override
        public Node createFromParcel(Parcel in) {
            return new Node(in);
        }

        @Override
        public Node[] newArray(int size) {
            return new Node[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(num);
        out.writeInt(type);
        out.writeString(data);
    }

    public Node(int num, int type, String data){
        this.num = num;
        this.type = type;
        this.data = data;
    }

    public Node(Parcel in){
        int num;
        int type;
        String data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public int getNum() {
        return num;
    }

    public int getType() {
        return type;
    }
}
