package de.bwaldvogel.mongo.exception;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.jboss.netty.channel.Channel;

public class MongoServerException extends Exception {

    private static final long serialVersionUID = 3357301041846925271L;

    public MongoServerException(String message) {
        super(message);
    }

    public MongoServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public BSONObject createBSONObject(Channel channel, BSONObject query) {
        BSONObject obj = new BasicBSONObject();
        obj.put("err", getMessage());
        obj.put("ok", Integer.valueOf(0));
        return obj;
    }

}
