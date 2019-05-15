package tech.gregori.locationdemo;

import android.os.Parcel;
import android.os.Parcelable;

public class Contato implements Parcelable {

    private String nome;
    private String email;
    private double latitude;
    private double longitude;

    public Contato(String name, String email, double latitude, double longitude) {
        this.nome = name;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Contato(Parcel in) {
        nome = in.readString();
        email = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<Contato> CREATOR = new Creator<Contato>() {
        @Override
        public Contato createFromParcel(Parcel in) {
            return new Contato(in);
        }

        @Override
        public Contato[] newArray(int size) {
            return new Contato[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public String getNome() {
        return nome;
    }

    public double getLongitude() { return longitude; }

    public String getEmail() { return email; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeString(email);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
