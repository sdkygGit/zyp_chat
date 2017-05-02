package com.wiz.dev.wiztalk.service.writer;

import android.content.Context;

import com.epic.traverse.push.smack.XmppManager;
import com.epic.traverse.push.util.L;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppDbMessage;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;
import com.wiz.dev.wiztalk.utils.SaveConfig;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.ArrayBlockingQueueWithShutdown;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Dong on 2015/11/24.
 */
public class MsgWriter {

    public static final int QUEUE_SIZE = 500;
    private final ArrayBlockingQueueWithShutdown<XmppMessage> queue = new ArrayBlockingQueueWithShutdown<XmppMessage>(
            QUEUE_SIZE, true);

    private Thread writerThread;

    volatile boolean done;

    AtomicBoolean shutdownDone = new AtomicBoolean(false);

    private Context context;

    public MsgWriter(Context context) {
        this.context = context;
        init();
    }

    protected void init() {
        done = false;
        shutdownDone.set(false);

        queue.start();
        writerThread = new Thread() {
            @Override
            public void run() {
                writePackets(this);
            }
        };
        writerThread.setName("Msg Writer ");
        writerThread.setDaemon(true);
    }

    public void sendMsgs(List<XmppMessage> msgs) {
        if (msgs != null && msgs.size() > 0) {
            for (XmppMessage msg : msgs) {
                try {
                    sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendMsg(XmppMessage msg) {
        msg.setRecVoiceReadStatus(MsgInFo.READ_FALSE);
        sendMessage(msg);
    }


    public void sendMessage(XmppMessage packet) /* throws NotConnectedException */ {
        if (packet != null) {

            if (packet.getToReadStatus() == null)
                packet.setToReadStatus(XmppDbMessage.READ_FALSE);

            if (packet.getType().equals(MsgInFo.TYPE_CHAT) && packet.getBody().equals(SaveConfig.UPDATA_TO_READ_PWD) )
            {
                try {
                    queue.put(packet);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                return;
            }

                // TODO: 2015/11/24
            if (packet.getId() == null || packet.getId() <= 0) {
                XmppDbManager.getInstance(context).insertMessage(packet);
            }
//            else {
//                packet.setCreateTime(System.currentTimeMillis());
//                packet.setStatus(MsgInFo.STATUS_PENDING);
//                XmppDbManager.getInstance(context).updateWithNotify(packet);
//            }

            if (done) {
                return;
            }

            try {
                queue.put(packet);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

        }
    }

    public void startup() {
        writerThread.start();
    }

    public void shutdown() {
        done = true;
        queue.shutdown();
        synchronized (shutdownDone) {
            if (!shutdownDone.get()) {
                try {
                    // shutdownDone.wait(connection.getPacketReplyTimeout());
                    shutdownDone.wait(10 * 1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private XmppMessage nextMsg() {
        if (done) {
            return null;
        }

        XmppMessage packet = null;
        try {
            packet = queue.take();
        } catch (InterruptedException e) {
            // Do nothing
        }
        return packet;
    }

    private void writePackets(Thread thisThread) {
        try {
            // Write out packets from the queue.
            while (!done && (writerThread == thisThread)) {
                XmppMessage packet = nextMsg();
                push(packet);
            }
            // Flush out the rest of the queue. If the queue is extremely large,
            // it's possible
            // we won't have time to entirely flush it before the socket is
            // forced closed
            // by the shutdown process.
            try {
                while (!queue.isEmpty()) {
                    XmppMessage packet = queue.remove();
                    push(packet);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // LOGGER.log(Level.WARNING,
                // "Exception flushing queue during shutdown, ignore and continue",
                // e);
            }

            // Delete the queue contents (hopefully nothing is left).
            queue.clear();

            shutdownDone.set(true);
            synchronized (shutdownDone) {
                shutdownDone.notify();
            }
        } catch (/* IO */Exception ioe) {
            // or if the it was caused because the socket got closed
            // if (!(done || connection.isSocketClosed())) {
            if (!(done)) {
                shutdown();
            }
        }
    }

    // TODO: 2015/11/24 发送消息的核心代码
    private void push(XmppMessage message) {
        if (message == null) {
            return;
        }
        boolean response = false;

        if (message.getType().equals(MsgInFo.TYPE_CHAT) && message.getBody().equals(SaveConfig.UPDATA_TO_READ_PWD)) {
            try {
                Message msg2 = XmppDbMessage.retriveMessageFromXmppMessage(message);
                response = XmppManager.getInstance().sendMessage(msg2);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
            return;
        }

        // TODO: 2015/11/24
        message.setStatus(MsgInFo.STATUS_SENDING);
        XmppDbManager.getInstance(context).updateWithNotify(message);
        Message msg = XmppDbMessage.retriveMessageFromXmppMessage(message);

        L.d("push", "send msg:" + message);
        try {
            if (message.getType().equals(MsgInFo.TYPE_CHAT) || message.getType().equals(MsgInFo.TYPE_HEADLINE)) {
                response = XmppManager.getInstance().sendMessage(msg);
            } else if (message.getType().equals(MsgInFo.TYPE_GROUPCHAT)) {
//				TODO groupService 的connection
                String jid = message.getTo_();
                response = XmppManager.getInstance().sendMsg2Group(jid, msg, OpenfireConstDefine.OPENFIRE_SERVER_NAME);
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.setStatus(MsgInFo.STATUS_ERROR);
            XmppDbManager.getInstance(context).updateWithNotify(message);
        }
        L.d("push", "response:" + response);
        if (response) {
            message.setStatus(MsgInFo.STATUS_SUCCESS);
            message.setSid(msg.getStanzaId());
            XmppDbManager.getInstance(context).updateWithNotify(message);
        } else {
            message.setStatus(MsgInFo.STATUS_ERROR);
            XmppDbManager.getInstance(context).updateWithNotify(message);
        }
    }
}
