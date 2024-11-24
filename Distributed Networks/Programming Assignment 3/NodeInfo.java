import java.io.Serializable;

public class NodeInfo implements Serializable {
    private int id;
    private String ip;
    private int port;
    private boolean join;
    private boolean leave;

    public NodeInfo(int id, String ip, int port, boolean join, boolean leave) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.join = join;
        this.leave = leave;
    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public boolean isJoin() {
        return join;
    }

    public boolean isLeave() {
        return leave;
    }
}
