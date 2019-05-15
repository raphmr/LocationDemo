package tech.gregori.locationdemo;

import android.util.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class ContatoParser {
    private ArrayList<Contato> contatos;

    ContatoParser() {
        this.contatos = new ArrayList<>();
    }

    ArrayList<Contato> getContatos() {
        return contatos;
    }

    public boolean parse(String object) throws IOException {

        JsonReader reader = new JsonReader(new StringReader(object));

        reader.beginObject();

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("pessoas")) {
                reader.beginArray();

                while (reader.hasNext()) {
                    contatos.add(parseContato(reader));
                }

                reader.endArray();
            }
        }

        reader.endObject();
        return true;

    }

    public Contato parseContato(JsonReader reader) throws IOException{
        String nome = null;
        String email = null;
        double latitude = 0.0;
        double longitude = 0.0;

        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            if (key.equals("nome")) {
                nome = reader.nextString();
            } else if (key.equals("email")) {
                email = reader.nextString();
            } else if (key.equals("latitude")) {
                latitude = reader.nextDouble();
            } else if (key.equals("longitude")) {
                longitude = reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new Contato(nome, email, latitude, longitude);
    }
}
