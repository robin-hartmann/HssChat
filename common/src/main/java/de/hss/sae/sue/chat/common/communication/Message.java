package de.hss.sae.sue.chat.common.communication;

import java.io.Serializable;

public class Message implements Serializable
{
    private static final String ZERO_TIMESTAMP = "00000000/0000";

    private final int     id;
    private final String  msg;
    private final String  sender;
    private final String  timestamp; // format: 'YYYYMMDD/HHMM'

    public Message(int id, String sender, String msg, String timestamp)
    {
        this.id         = id;
        this.msg        = msg;
        this.sender     = sender;
        this.timestamp  = timestamp;
    }

    public static Message obtainMessage(String sender, String message) {
        return new Message(0, sender, message, ZERO_TIMESTAMP);
    }

    public int      getId()                 { return this.id;           }
    public String   getMessage()            { return this.msg;          }
    public String   getSender()             { return this.sender;       }
    public String   getTimestamp()          { return this.timestamp;    }
}