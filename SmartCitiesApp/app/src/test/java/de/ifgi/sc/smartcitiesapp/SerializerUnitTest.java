package de.ifgi.sc.smartcitiesapp;

import android.util.Log;

import org.junit.Test;

import java.util.Date;

import de.ifgi.sc.smartcitiesapp.messaging.Message;
import de.ifgi.sc.smartcitiesapp.p2p.Serializer;

import static org.junit.Assert.assertEquals;

/**
 * Created by helo on 04.07.16.
 */
public class SerializerUnitTest {

    @Test
    public void transformation_isCorrect() throws Exception {
        Message inMessage = new de.ifgi.sc.smartcitiesapp.messaging.Message("c_id", "m_id", "z_id", new Date(), 51, 7, new Date(2016,6,21), "top", "tit", "msg");
        String in = inMessage.toString();
        Log.i("Unit in", in );
        byte[] seriMessage = Serializer.serialize(in);
        String out = (String)Serializer.deserialize(seriMessage);
        Log.i("Unit out", out);
        assertEquals(in, out);
    }

    @Test
    public void transformation_isCorrect1() throws Exception {
        String in = "hallo";
        byte[] seri = Serializer.serialize(in);
        String out = (String) Serializer.deserialize(seri);
        assertEquals(in, out);
    }
}
